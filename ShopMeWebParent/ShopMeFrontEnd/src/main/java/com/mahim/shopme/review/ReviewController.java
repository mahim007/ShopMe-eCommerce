package com.mahim.shopme.review;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.entity.Review;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import com.mahim.shopme.product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.mahim.shopme.review.ReviewService.REVIEWS_PER_PAGE;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final CustomerService customerService;
    private final ProductService productService;

    public ReviewController(ReviewService reviewService, CustomerService customerService, ProductService productService) {
        this.reviewService = reviewService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/reviews/page/1";
    }

    @GetMapping("/page/{pageNum}")
    public String listReviewsByPage(@PathVariable int pageNum,
                                    @RequestParam(name = "sortField", defaultValue = "reviewTime") String sortField,
                                    @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
                                    @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                    HttpServletRequest request, RedirectAttributes ra, Model model) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Page<Review> page = reviewService.listReviewsForCustomerByPage(customer.getEmail(), pageNum, sortField, sortDir, keyword);

            long totalItems = page.getTotalElements();
            List<Review> reviews = page.getContent();
            int startNo = ((pageNum - 1) * REVIEWS_PER_PAGE) + 1;
            int endNo = pageNum * REVIEWS_PER_PAGE;

            model.addAttribute("pageTitle", "My Reviews");
            model.addAttribute("totalPageNo", page.getTotalPages());
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("currentPageNo", pageNum);
            model.addAttribute("reviews", reviews);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("keyword", keyword);
            model.addAttribute("moduleURL", "/reviews");
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
            model.addAttribute("startNo", startNo);
            model.addAttribute("endNo", endNo < totalItems ? endNo : totalItems);

            return "review/reviews";
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer is not logged in");
            return "redirect:/login";
        } catch (Exception e) {
            return listAll();
        }

    }

    @GetMapping("/product/{alias}/new")
    public String writeReview(@PathVariable String alias, Model model, HttpServletRequest request, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Product product = productService.getProduct(alias);
            Review review = new Review(customer, product);

            model.addAttribute("pageTitle", "Write Product Review");
            model.addAttribute("review", review);
            return "review/review_form";
        } catch (CustomerNotFoundException e) {
            return "redirect:/login";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Product with alias: " + alias + " not found.");
        }

        return listAll();
    }

    @PostMapping("/save")
    public String save(Review review,
                       @RequestParam String productAlias,
                       @RequestParam Integer customerId,
                       HttpServletRequest request,
                       RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            review.setCustomer(customer);

            Product product = productService.getProduct(productAlias);
            review.setProduct(product);

            reviewService.save(review);
        } catch (CustomerNotFoundException e) {
            return "redirect:/login";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Product with alias: " + productAlias + " not found.");
        }

        return listAll();
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Review review = reviewService.getReviewById(id);
            model.addAttribute("review", review);
            return "review/review_details_modal";
        } catch (CustomerNotFoundException e) {
            return "redirect:/login";
        } catch (com.mahim.shopme.common.exception.ReviewNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Review with id: " + id + " not found.");
        }

        return listAll();
    }
}
