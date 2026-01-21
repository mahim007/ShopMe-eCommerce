package com.mahim.shopme.product;

import com.mahim.shopme.category.CategoryService;
import com.mahim.shopme.common.entity.Category;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.entity.Review;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.review.ReviewService;
import org.apache.commons.lang3.StringUtils;
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
    private final ReviewService reviewService;

    public ProductDetailsController(ProductService productService, CategoryService categoryService, ReviewService reviewService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{product_alias}")
    public String viewProductDetails(@PathVariable(name = "product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> categoryParents = categoryService.getCategoryParents(product.getCategory());
            List<Review> reviews = reviewService.getReviewsByProductId(product.getId())
                    .stream()
                    .limit(3)
                    .toList();

            model.addAttribute("pageTitle", product.getShortName());
            model.addAttribute("product", product);
            model.addAttribute("listCategoryParents", categoryParents);
            model.addAttribute("reviews", reviews);

            return "product/product_details";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }
}
