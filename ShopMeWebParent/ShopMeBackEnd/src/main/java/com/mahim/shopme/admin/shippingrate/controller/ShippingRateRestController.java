package com.mahim.shopme.admin.shippingrate.controller;

import com.mahim.shopme.admin.shippingrate.service.ShippingRateService;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.common.exception.ShippingRateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(name = "country", defaultValue = "") String countryCode,
            @RequestParam(name = "state", defaultValue = "") String state
    ) throws ShippingRateNotFoundException {
        return shippingRateService.checkUnique(id, countryCode, state);
    }

    @GetMapping("/shipping_cost")
    public ResponseEntity<String> getShippingCost(
            @RequestParam(name = "productId", defaultValue = "-1") Integer productId,
            @RequestParam(name = "countryId", defaultValue = "") Integer countryId,
            @RequestParam(name = "state", defaultValue = "") String state
    ) {
        try {
            float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);
            return new ResponseEntity<>(String.valueOf(shippingCost), HttpStatus.OK);
        } catch (ShippingRateNotFoundException | ProductNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
