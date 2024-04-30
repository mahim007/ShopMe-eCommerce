package com.mahim.shopme.admin.brand.repository;

import com.mahim.shopme.admin.paging.SearchRepository;
import com.mahim.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends SearchRepository<Brand, Integer> {
    Brand findByName(String name);
    int countById(Integer id);

    @Query("select b from Brand b where concat(b.id, ' ', b.name) like %?1%")
    Page<Brand> findAllByKeyword(String keyword, Pageable pageable);

    @Query("select new Brand(b.id, b.name) from Brand b order by b.name asc ")
    List<Brand> listAllBrandsSorted();
}
