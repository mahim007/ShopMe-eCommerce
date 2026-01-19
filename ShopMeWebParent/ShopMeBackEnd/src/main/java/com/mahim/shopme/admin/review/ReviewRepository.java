package com.mahim.shopme.admin.review;

import com.mahim.shopme.admin.paging.SearchRepository;
import com.mahim.shopme.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends SearchRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE concat(r.headline, ' ', r.comment, ' ', " +
            "r.product.alias, ' ', " +
            "r.customer.firstName , ' ', r.customer.lastName) LIKE %:keyword%")
    Page<Review> findAllByKeyword(String keyword, Pageable pageable);
}
