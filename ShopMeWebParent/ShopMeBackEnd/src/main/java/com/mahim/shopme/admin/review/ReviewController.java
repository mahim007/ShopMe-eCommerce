package com.mahim.shopme.admin.review;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.admin.security.ShopmeUserDetails;
import com.mahim.shopme.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:reviews/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listReviewsByPage(
            @PagingAndSortingParam(listName = "reviews", moduleURL = "/reviews") PagingAndSortingHelper helper,
            @PathVariable("pageNo") int pageNo) {

        Page<Review> reviews = reviewService.listByKeyword(pageNo, helper);
        helper.updateModelAttributes(pageNo, reviews);
        return "reviews/reviews";
    }

    @PostMapping("/save")
    public String saveReview(Review review, RedirectAttributes ra) {
        try {
            Review updated = reviewService.saveReview(review);
            ra.addFlashAttribute("message", "Review id: " + updated.getId() + " updated successfully");
        } catch (ReviewNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Review with id: " + review.getId() + " not found");
        } catch (Exception e) {
            ra.addFlashAttribute("exceptionMessage", "Review could not be updated");
        }
        return listAll();
    }

    @GetMapping("/details/{id}")
    public String view(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Review review = reviewService.findById(id);
            model.addAttribute("review", review);
        } catch (ReviewNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Review with id: " + id + " not found.");
            return listAll();
        }
        return "reviews/review_details_modal";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
                       @AuthenticationPrincipal ShopmeUserDetails loggedInUser) {
        try {
            Review review = reviewService.findById(id);
            boolean isReadOnly = true;

            if (loggedInUser.hasRole("Admin") || loggedInUser.hasRole("Editor")) {
                isReadOnly = false;
            }

            model.addAttribute("review", review);
            model.addAttribute("isReadOnly", isReadOnly);
        } catch (ReviewNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Review with id: " + id + " not found.");
            return listAll();
        }
        return "reviews/review_form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
        try {
            reviewService.deleteReview(id);
            ra.addFlashAttribute("message", "Review deleted successfully");
        } catch (ReviewNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Review with id: " + id + " not found");
        }

        return listAll();
    }
}
