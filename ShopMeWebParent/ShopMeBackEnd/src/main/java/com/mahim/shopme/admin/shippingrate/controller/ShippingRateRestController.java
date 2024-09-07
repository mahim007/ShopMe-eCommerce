package com.mahim.shopme.admin.shippingrate.controller;

import com.mahim.shopme.admin.shippingrate.service.ShippingRateService;
import com.mahim.shopme.common.exception.ShippingRateNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping_rates")
public class ShippingRateRestController {
    private final ShippingRateService shippingRateService;

    public ShippingRateRestController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @PostMapping("/check_unique")
    public String checkUnique(
            @RequestParam(name = "id", defaultValue = "-1") Integer id,
            @RequestParam(name = "country", defaultValue = "") String country,
            @RequestParam(name = "state", defaultValue = "") String state
    ) throws ShippingRateNotFoundException {
        return shippingRateService.checkUnique(id, country, state);
    }
}
