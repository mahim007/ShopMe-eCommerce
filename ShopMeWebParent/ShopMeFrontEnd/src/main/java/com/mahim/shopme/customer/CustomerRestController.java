package com.mahim.shopme.customer;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers/check_unique_email")
    public String checkUniqueEmail(@RequestParam("email") String email) {
        return customerService.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
