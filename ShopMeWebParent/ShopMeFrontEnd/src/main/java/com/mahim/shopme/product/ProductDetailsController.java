package com.mahim.shopme.product;

import com.mahim.shopme.category.CategoryService;
import com.mahim.shopme.common.entity.Category;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/p")
public class ProductDetailsController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductDetailsController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/{product_alias}")
    public String viewProductDetails(@PathVariable(name = "product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> categoryParents = categoryService.getCategoryParents(product.getCategory());

            model.addAttribute("pageTitle", product.getShortName());
            model.addAttribute("product", product);
            model.addAttribute("listCategoryParents", categoryParents);

            return "product/product_details";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }
}
