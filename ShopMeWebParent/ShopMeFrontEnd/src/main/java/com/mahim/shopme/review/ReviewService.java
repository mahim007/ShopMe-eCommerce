package com.mahim.shopme.review;

import com.mahim.shopme.common.entity.Review;
import com.mahim.shopme.common.exception.ReviewNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 10;
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review getReviewByProductAndCustomerEmail(Integer productId, String customerEmail) {
        Optional<Review> reviewOpt = reviewRepository.findByProductIdAndCustomerEmail(productId, customerEmail);
        return reviewOpt.orElse(null);
    }

    public List<Review> getReviewsByProductId(Integer productId) {
        return reviewRepository.findByProductId(productId);

    }

    public List<Review> getReviewsByCustomerEmail(String customerEmail) {
        return reviewRepository.getReviewsByCustomerEmail(customerEmail);
    }

    public Page<Review> listReviewsForCustomerByPage(String customerEmail, int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageRequest = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        return reviewRepository.searchAllReviews(keyword, customerEmail, pageRequest);
    }

    public Review save(Review review) {
        review.setReviewTime(new Date());
        return reviewRepository.save(review);
    }

    public Review getReviewById(Integer id) {
        return findById(id);
    }

    public Review findById(Integer id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) throw new ReviewNotFoundException("Review with id: " + id + " not found.");
        return reviewOptional.get();
    }
}
