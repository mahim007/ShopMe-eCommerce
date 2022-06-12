package com.mahim.shopme.admin.category.controller;

import com.mahim.shopme.admin.category.service.CategoryService;
import com.mahim.shopme.common.entity.Category;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/check_unique")
    public String checkUnique(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias) {
        return categoryService.checkUnique(id, name, alias);
    }
}
