package com.mahim.shopme.admin.order;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.admin.setting.SettingService;
import com.mahim.shopme.common.entity.Order;
import com.mahim.shopme.common.entity.Setting;
import com.mahim.shopme.common.exception.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final SettingService settingService;

    public OrderController(OrderService orderService, SettingService settingService) {
        this.orderService = orderService;
        this.settingService = settingService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(
            @PagingAndSortingParam(listName = "orders", moduleURL = "/orders") PagingAndSortingHelper helper,
            @PathVariable("pageNo") int pageNo,
            HttpServletRequest request) {

        Page<Order> orders = orderService.listByKeyword(pageNo, helper);
        helper.updateModelAttributes(pageNo, orders);
        loadCurrencySetting(request);
        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/details/{id}")
    public String get(@PathVariable("id") Integer id,HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            Order order = orderService.findById(id);
            model.addAttribute("order", order);
            loadCurrencySetting(request);
            return "orders/order_details_modal";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Order not found with ID: " + id);
        }

        return listAll();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "Order ID:" + id + " deleted successfully");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Order ID: " + id + " not found");
        }

        return listAll();
    }
}
