package com.mahim.shopme.admin.category.controller;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.common.exception.CategoryNotFoundException;
import com.mahim.shopme.admin.category.exporter.CategoryCsvExporter;
import com.mahim.shopme.admin.category.exporter.CategoryExcelExporter;
import com.mahim.shopme.admin.category.exporter.CategoryPdfExporter;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.mahim.shopme.admin.category.service.CategoryService.CATEGORIES_PER_PAGE;
import static com.mahim.shopme.common.util.StaticPathUtils.CATEGORY_UPLOAD_DIR;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryCsvExporter categoryCsvExporter;
    private final CategoryExcelExporter categoryExcelExporter;
    private final CategoryPdfExporter categoryPdfExporter;

    public CategoryController(CategoryService categoryService,
                              CategoryCsvExporter categoryCsvExporter,
                              CategoryExcelExporter categoryExcelExporter,
                              CategoryPdfExporter categoryPdfExporter) {
        this.categoryService = categoryService;
        this.categoryCsvExporter = categoryCsvExporter;
        this.categoryExcelExporter = categoryExcelExporter;
        this.categoryPdfExporter = categoryPdfExporter;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/categories/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(
            @PagingAndSortingParam(listName = "categories", moduleURL = "/categories") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNo") int pageNo) {

        Page<Category> categories = categoryService.listByKeyword(pageNo, helper);
        helper.updateModelAttributes(pageNo, categories);
        return "categories/categories";
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
        Category category = new Category();
        category.setEnabled(true);

        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.listAll());
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
            model.addAttribute("categories", categoryService.listAll());
            model.addAttribute("pageTitle", "Edit category (Id: " + id + " )");
            return "categories/category_form";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", e.getMessage());
            return listAll();
        }

    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateEnableStatus(@PathVariable(name = "id") Integer id,
                                     @PathVariable(name = "enabled") boolean enabled,
                                     RedirectAttributes redirectAttributes) {
        try {
            Category categoryById = categoryService.findCategoryById(id);
            categoryById.setEnabled(!enabled);
            Category savedCategory = categoryService.save(categoryById);
            redirectAttributes.addFlashAttribute("message", "Enabled status updated for " +
                    "category (ID:" + savedCategory.getId() + ")");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Category not found " +
                    "(ID: " + id + ")");
        }

        return listAll();
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

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> categories = categoryService.listAll();
        categoryCsvExporter.export(response, categories);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) {
        List<Category> categories = categoryService.listAll();
        categoryExcelExporter.export(response, categories);
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) {
        List<Category> categories = categoryService.listAll();
        categoryPdfExporter.export(response, categories);
    }
}
