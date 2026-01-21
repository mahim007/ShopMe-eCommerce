package com.mahim.shopme.product;

import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.entity.Review;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.review.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 12;
    public static final int SEARCH_RESULTS_PER_PAGE = 12;
    public static final int REVIEWS_PER_PAGE = 5;

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    public Page<Product> listByCategory(int pageNum, Integer categoryId) {
        String categoryIdMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
    }

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Product with alias: " + alias + " not found.");
        }

        return product;
    }

    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
        return productRepository.search(keyword, pageable);
    }

    public Page<Review> listReviewsForProductByPage(String alias, int pageNum, String sortField) {
        Sort sort = Sort.by(sortField).descending();
        Pageable pageRequest = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
        return reviewRepository.findAllByProductId(alias, pageRequest);
    }
}
