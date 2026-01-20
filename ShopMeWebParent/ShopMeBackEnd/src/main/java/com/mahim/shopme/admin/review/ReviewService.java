package com.mahim.shopme.admin.review;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 10;

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Page<Review> listByKeyword(int pageNo, PagingAndSortingHelper helper) {
        return (Page<Review>) helper.listEntities(pageNo, REVIEWS_PER_PAGE, reviewRepository);
    }

    public Review saveReview(Review review) {
        Review reviewToBeSaved = findById(review.getId());
        reviewToBeSaved.setHeadline(review.getHeadline());
        reviewToBeSaved.setComment(review.getComment());
        reviewToBeSaved.setModerated(true);
        return reviewRepository.save(reviewToBeSaved);
    }

    public List<Review> listAllReviews() {
        return (List<Review>) reviewRepository.findAll();
    }

    public Review findById(Integer id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) throw new com.mahim.shopme.common.exception.ReviewNotFoundException("Review with id: " + id + " not found.");
        return reviewOptional.get();
    }

    public void deleteReview(Integer id) {
        Review review = findById(id);
        reviewRepository.delete(review);
    }
}
