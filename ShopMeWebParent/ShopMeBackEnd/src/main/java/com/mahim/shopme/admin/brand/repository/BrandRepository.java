package com.mahim.shopme.admin.brand.repository;

import com.mahim.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
    Brand findByName(String name);
    int countById(Integer id);

    @Query("select b from Brand b where concat(b.id, ' ', b.name) like %?1%")
    Page<Brand> findAllByKeyword(String keyword, PageRequest pageRequest);
}
