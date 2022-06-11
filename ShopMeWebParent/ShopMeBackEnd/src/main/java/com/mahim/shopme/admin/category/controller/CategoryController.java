package com.mahim.shopme.admin.category.controller;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.category.CategoryNotFoundException;
import com.mahim.shopme.admin.category.service.CategoryService;
import com.mahim.shopme.admin.user.UserNotFoundException;
import com.mahim.shopme.common.entity.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.mahim.shopme.admin.category.service.CategoryService.CATEGORIES_PER_PAGE;
import static com.mahim.shopme.admin.utils.StaticPathUtils.CATEGORY_UPLOAD_DIR;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/categories/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(@PathVariable(name = "pageNo") int pageNo,
                             @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
                             @RequestParam(name = "keyword", defaultValue = "") String keyword,
                             Model model
                             ) {
        keyword = StringUtils.trim(keyword);
        if (StringUtils.isEmpty(keyword) || StringUtils.isBlank(keyword)) {
            keyword = null;
        }

        Page<Category> categoriesPage = keyword == null ? categoryService.listByPage(pageNo, sortField, sortDir) :
                categoryService.listByKeyword(pageNo, sortField, sortDir, keyword);

        int totalPages = categoriesPage.getTotalPages();
        long totalElements = categoriesPage.getTotalElements();
        int currentPageSize = categoriesPage.getNumberOfElements();
        int startNo = ((pageNo - 1) * CATEGORIES_PER_PAGE) + 1;
        int endNo = (startNo + currentPageSize) - 1;

        List<Category> categories = categoriesPage.getContent();
        String reverseSortDir = StringUtils.equals(sortDir, "asc") ? "desc" : "asc";

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("categories", categories);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        return "categories/categories";
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
        Category category = new Category();
        category.setEnabled(true);

        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getHierarchicalCategories());
        model.addAttribute("pageTitle", "Create new category");
        return "categories/category_form";
    }

    @PostMapping("/save")
    public String saveCategory(Category category, @RequestParam(name = "image") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {

        Category categorySaved = null;
        if (!multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
            String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setPhotos(fileName);

            categorySaved = categoryService.save(category);
            FileUploadUtil.cleanDir(CATEGORY_UPLOAD_DIR + "/" + categorySaved.getId());
            FileUploadUtil.saveFile(CATEGORY_UPLOAD_DIR + "/" + categorySaved.getId(),
                    fileName, multipartFile);
        } else {
            categorySaved = categoryService.save(category);
        }

        redirectAttributes.addFlashAttribute("message", "New category has been saved successfully");
        return getRedirectUrlForAffectedCategory(categorySaved);
    }

    private String getRedirectUrlForAffectedCategory(Category categorySaved) {
        return "redirect:/categories/page/1?sortField=id&sortDir=asc&keyword=" + categorySaved.getName();
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            Category categoryById = categoryService.findCategoryById(id);
            model.addAttribute("category", categoryById);
            model.addAttribute("pateTitle", "Edit category (Id: " + id + " )");
            return "categories/category_form";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", e.getMessage());
            return listAll();
        }

    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Category with id: " + id
                    + " deleted");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Category with id: " + id
                    + " not found.");
        }

        return listAll();
    }
}
