package com.mahim.shopme.product;

import com.mahim.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 12;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> listByCategory(int pageNum, Integer categoryId) {
        String categoryIdMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
    }
}
