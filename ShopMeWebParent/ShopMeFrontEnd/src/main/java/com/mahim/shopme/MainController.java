package com.mahim.shopme;

import com.mahim.shopme.category.CategoryService;
import com.mahim.shopme.common.entity.Category;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private final CategoryService categoryService;

    public MainController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Category> categories = categoryService.listNoChildrenCategories();
        model.addAttribute("categories", categories);
        return "index";
    }
}
