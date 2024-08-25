package com.mahim.shopme.admin.brand.controller;

import com.mahim.shopme.admin.brand.exception.BrandNotFoundException;
import com.mahim.shopme.admin.brand.exception.BrandNotFoundRestException;
import com.mahim.shopme.admin.brand.service.BrandService;
import com.mahim.shopme.common.dto.CategoryDTO;
import com.mahim.shopme.common.entity.Brand;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/brands")
public class BrandRestController {
    private final BrandService brandService;

    public BrandRestController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/check_unique")
    public String checkUnique(@RequestParam("id") Integer id,
                              @RequestParam("name") String name) {
        return brandService.checkUnique(id, name);
    }

    @GetMapping("/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId)
            throws BrandNotFoundRestException {

        try {
            Brand brand = brandService.findBrandById(brandId);

            return  brand.getCategories().stream()
                    .map(category -> new CategoryDTO(category.getId(), category.getName()))
                    .sorted((a, b) -> a.getId() - b.getId())
                    .collect(Collectors.toList());
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
    }
}
