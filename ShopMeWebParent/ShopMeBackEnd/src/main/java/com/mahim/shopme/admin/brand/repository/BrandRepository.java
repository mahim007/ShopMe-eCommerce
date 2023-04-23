package com.mahim.shopme.admin.brand.repository;

import com.mahim.shopme.common.entity.Brand;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
    Brand findByName(String name);
    int countById(Integer id);
}
