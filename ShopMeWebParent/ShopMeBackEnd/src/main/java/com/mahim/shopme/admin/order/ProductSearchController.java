package com.mahim.shopme.admin.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class ProductSearchController {

    @GetMapping("/search_product")
    public String showSearchProductPage() {
        return "orders/search_product";
    }
}
