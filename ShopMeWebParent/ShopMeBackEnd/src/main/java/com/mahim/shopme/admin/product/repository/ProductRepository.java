package com.mahim.shopme.admin.product.repository;

import com.mahim.shopme.common.entity.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
}
