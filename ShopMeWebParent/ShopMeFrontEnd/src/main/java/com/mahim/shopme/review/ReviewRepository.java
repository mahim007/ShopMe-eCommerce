package com.mahim.shopme.review;

import com.mahim.shopme.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends PagingAndSortingRepository<Review, Integer> {

    @Query("select r from Review r where r.product.id = :productId and r.customer.id = :customerEmail")
    Optional<Review> findByProductIdAndCustomerEmail(Integer productId, String customerEmail);

    @Query("select r from Review r where r.product.id = :productId order by r.reviewTime desc")
    List<Review> findByProductId(Integer productId);

    @Query("select r from Review r where r.customer.email = :customerEmail")
    List<Review> getReviewsByCustomerEmail(String customerEmail);

    @Query("select r from Review r where r.comment like %:keyword% or r.headline like %:keyword% and r.customer.email like %:customerEmail%")
    Page<Review> searchAllReviews(String keyword, String customerEmail, Pageable pageable);
}
