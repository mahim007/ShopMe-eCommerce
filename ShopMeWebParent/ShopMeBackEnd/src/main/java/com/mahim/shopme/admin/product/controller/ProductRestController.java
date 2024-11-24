package com.mahim.shopme.admin.product.controller;

import com.mahim.shopme.admin.product.ProductDTO;
import com.mahim.shopme.admin.product.service.ProductService;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get/{id}")
    public ProductDTO getProductInfo(@PathVariable Integer id) throws ProductNotFoundException {
        Product product = productService.findProductById(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }
}
