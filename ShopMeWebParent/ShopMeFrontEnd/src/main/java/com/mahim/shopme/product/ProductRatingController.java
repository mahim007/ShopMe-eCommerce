package com.mahim.shopme.product;

import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.entity.Review;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.review.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.mahim.shopme.product.ProductService.REVIEWS_PER_PAGE;

@Controller
@RequestMapping("/ratings")
public class ProductRatingController {
    private final ProductService productService;
    private final ReviewService reviewService;

    public ProductRatingController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{product_alias}")
    public String viewProductRating(@PathVariable(name = "product_alias") String alias, Model model) throws ProductNotFoundException {
        return viewProductRatingByPage(alias, 1, model);
    }

    @GetMapping("/{product_alias}/page/{pageNo}")
    public String viewProductRatingByPage(@PathVariable(name = "product_alias") String alias,
                                          @PathVariable(name = "pageNo") int pageNo, Model model) throws ProductNotFoundException {
        Product product = productService.getProduct(alias);
        Page<Review> page = productService.listReviewsForProductByPage(alias, pageNo, "reviewTime");
        List<Review> reviews = page.getContent();

        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        int startNo = ((pageNo - 1) * REVIEWS_PER_PAGE) + 1;
        int endNo = pageNo * REVIEWS_PER_PAGE;

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo < totalElements ? endNo : totalElements);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("totalItems", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("pageTitle", "Product Reviews for: " + product.getName());
        model.addAttribute("alias", alias);
        model.addAttribute("reviews", reviews);
        model.addAttribute("product", product);

        return "rating/ratings";
    }
}
