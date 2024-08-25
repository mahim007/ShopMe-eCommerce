package com.mahim.shopme.admin.category.controller;

import com.mahim.shopme.admin.category.service.CategoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/check_unique")
    public String checkUnique(@RequestParam("id") Integer id,
                              @RequestParam("name") String name,
                              @RequestParam("alias") String alias) {
        return categoryService.checkUnique(id, name, alias);
    }
}
