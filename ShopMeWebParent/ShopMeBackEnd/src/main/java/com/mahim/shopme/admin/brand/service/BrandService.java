package com.mahim.shopme.admin.brand.service;

import com.mahim.shopme.admin.brand.repository.BrandRepository;
import com.mahim.shopme.common.entity.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandService {
    public static final int BRANDS_PER_PAGE = 5;
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> listAll() {
        return null;
    }

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = StringUtils.equals(sortDir, "asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
        return brandRepository.findAll(pageable);
    }

    public Page<Brand> listByKeyword(int pageNum, String sortField, String sortDir, String keyword) {
        return new PageImpl<Brand>(new ArrayList<>());
    }

    public Brand save(Brand brand) {
        return null;
    }

    public Brand findBrandById(Integer id) {
        return null;
    }

    public void delete(Integer id) {

    }

    public List<Brand> getHierarchicalBrands(Iterable<Brand> brandIterable) {
        return null;
    }

    private void listBrandChildren(List<Brand> hierarchicalBrands, Brand brand, StringBuilder level) {

    }

    public String checkUnique(Integer id, String name, String alias) {
        return null;
    }
}
