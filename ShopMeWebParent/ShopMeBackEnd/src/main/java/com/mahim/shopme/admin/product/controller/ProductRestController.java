package com.mahim.shopme.admin.product.controller;

import com.mahim.shopme.admin.product.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductRestController {
    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/check_unique")
    public String checkUnique(
            @RequestParam(name = "id", defaultValue = "") Integer id,
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "alias", defaultValue = "") String alias
    ) {
        return productService.checkUnique(id, name, alias);
    }
}
