package com.mahim.shopme.admin.shippingrate.controller;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.admin.shippingrate.service.ShippingRateService;
import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.ShippingRate;
import com.mahim.shopme.common.exception.ShippingRateNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/shipping_rates")
public class ShippingRateController {
    private final ShippingRateService shippingRateService;

    public ShippingRateController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/shipping_rates/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(@PathVariable("pageNo") int pageNo,
                             @PagingAndSortingParam(listName = "shippingRates", moduleURL = "/shipping_rates") PagingAndSortingHelper helper) {
        Page<ShippingRate> shippingRates = shippingRateService.listByKeyword(pageNo, helper);
        helper.updateModelAttributes(pageNo, shippingRates);
        return "shipping_rates/shipping_rates";
    }

    @PostMapping("/save")
    public String save(ShippingRate shippingRate, RedirectAttributes ra) throws ShippingRateNotFoundException {
        boolean isNew = shippingRate.getId() == null;
        ShippingRate saved = shippingRateService.save(shippingRate);

        if (isNew) {
            ra.addFlashAttribute("message", "New shipping rate added.");
        } else {
            ra.addFlashAttribute("message", "Shipping rate updated.");
        }

        return getRedirectUrlForAffectedShippingRate(saved);
    }

    private String getRedirectUrlForAffectedShippingRate(ShippingRate shippingRate) {
        return "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc&keyword=" + URLEncoder.encode(shippingRate.getCountry().getName() + " " + shippingRate.getState(), StandardCharsets.UTF_8);
    }

    @GetMapping("/new")
    public String newShippingRate(Model model) {
        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setCodSupported(false);

        List<Country> countries = shippingRateService.listAllCountry();

        model.addAttribute("shippingRate", shippingRate);
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Create new Shipping Rate");
        return "shipping_rates/shipping_rate_form";
    }

    @GetMapping("/edit/{id}")
    public String editShippingRate(@PathVariable("id") Integer id, RedirectAttributes ra, Model model) {
        try {
            ShippingRate shippingRate = shippingRateService.findById(id);
            List<Country> countries = shippingRateService.listAllCountry();

            model.addAttribute("shippingRate", shippingRate);
            model.addAttribute("countries", countries);
            model.addAttribute("pageTitle", "Edit Shipping Rate (Id: " + id + " )");
            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("exceptionMessage", ex.getMessage());
            return listAll();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteShippingRate(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            shippingRateService.delete(id);
            ra.addFlashAttribute("message", "Category with id: " + id + " deleted");
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Shipping Rate with id: " + id
                    + " not found.");
        }

        return listAll();
    }

    @GetMapping("cod/{id}/enabled/{enabled}")
    public String updateCODStatus(@PathVariable("id") Integer id,
                                  @PathVariable("enabled") boolean enabled,
                                  RedirectAttributes ra) {
        try {
            ShippingRate shippingRate = shippingRateService.findById(id);
            shippingRate.setCodSupported(!enabled);
            ShippingRate saved = shippingRateService.save(shippingRate);
            ra.addFlashAttribute("message", "Cash On Delivery updated for id: " + saved.getId());
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Shipping Rate with id: " + id + " not found.");
        }

        return listAll();
    }
}
