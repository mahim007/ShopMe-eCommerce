package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> countries = customerService.listAllCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        return "register/register_form";
    }
}
