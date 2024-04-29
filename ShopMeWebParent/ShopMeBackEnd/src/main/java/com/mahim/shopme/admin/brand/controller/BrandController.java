package com.mahim.shopme.admin.brand.controller;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.brand.exception.BrandNotFoundException;
import com.mahim.shopme.admin.brand.exporter.BrandCsvExporter;
import com.mahim.shopme.admin.brand.exporter.BrandExcelExporter;
import com.mahim.shopme.admin.brand.exporter.BrandPdfExporter;
import com.mahim.shopme.admin.brand.service.BrandService;
import com.mahim.shopme.admin.category.service.CategoryService;
import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.common.entity.Brand;
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

import static com.mahim.shopme.admin.brand.service.BrandService.BRANDS_PER_PAGE;
import static com.mahim.shopme.common.util.StaticPathUtils.BRAND_UPLOAD_DIR;

@Controller
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;
    private final CategoryService categoryService;
    private final BrandCsvExporter csvExporter;
    private final BrandExcelExporter excelExporter;
    private final BrandPdfExporter pdfExporter;

    public BrandController(BrandService brandService,
                           CategoryService categoryService,
                           BrandCsvExporter csvExporter,
                           BrandExcelExporter excelExporter,
                           BrandPdfExporter pdfExporter) {

        this.brandService = brandService;
        this.categoryService = categoryService;
        this.csvExporter = csvExporter;
        this.excelExporter = excelExporter;
        this.pdfExporter = pdfExporter;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/brands/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(
            @PagingAndSortingParam(listName = "brands", moduleURL = "/brands") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNo") int pageNo) {

        Page<Brand> brands = brandService.listByKeyword(pageNo, helper);
        helper.updateModelAttributes(pageNo, brands);
        return "brands/brands";
    }

    @GetMapping("/new")
    public String newBrand(Model model) {
        Brand brand = new Brand();
        brand.setEnabled(true);

        model.addAttribute("brand", brand);
        model.addAttribute("nestedCategories", categoryService.listAll());
        model.addAttribute("pageTitle", "Create new brand");
        return "brands/brand_form";
    }

    @PostMapping("/save")
    public String saveCategory(Brand brand, @RequestParam(name = "image") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        Brand brandToBeSaved = null;
        if (!multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
            String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            brandToBeSaved = brandService.save(brand);
            FileUploadUtil.cleanDir(BRAND_UPLOAD_DIR + "/" + brandToBeSaved.getId());
            FileUploadUtil.saveFile(BRAND_UPLOAD_DIR + "/" + brandToBeSaved.getId(), fileName, multipartFile);
        } else {
            brandToBeSaved = brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message", "New brand has been added successfully");
        return getRedirectUrlForAffectedBrand(brandToBeSaved);
    }

    private String getRedirectUrlForAffectedBrand(Brand brand) {
        return "redirect:/brands/page/1?sortField=id&sortDir=asc&keyword=" + brand.getName();
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") int id, RedirectAttributes attributes) {
        try {
            brandService.delete(id);
            attributes.addFlashAttribute("message", "Brand with id: " + id +  " deleted.");
        } catch (RuntimeException ex) {
            attributes.addFlashAttribute("exceptionMessage", "Failed to delete brand with id: " + id + " .");
        } catch (BrandNotFoundException e) {
            attributes.addFlashAttribute("exceptionMessage", "Brand with id: " + id + " not found.");
        }

        return listAll();
    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateEnabledStatus(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "enabled") boolean enabled,
            RedirectAttributes attributes
    ) {
        try {
            Brand brandById = brandService.findBrandById(id);
            brandById.setEnabled(!enabled);
            Brand savedBrand = brandService.save(brandById);
            attributes.addFlashAttribute("message", "Enabled status updated for " +
                    "Brand(ID: " + savedBrand.getId() + ")");
        } catch (RuntimeException ex) {
            attributes.addFlashAttribute("exceptionMessage", "Failed to activate/deactivate " +
                    "brand with id: " + id);
        } catch (BrandNotFoundException e) {
            attributes.addFlashAttribute("exceptionMessage", e.getMessage());
        }

        return listAll();
    }

    @GetMapping("/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, RedirectAttributes attributes, Model model) {
        try {
            Brand brandById = brandService.findBrandById(id);
            model.addAttribute("brand", brandById);
            model.addAttribute("nestedCategories", categoryService.listAll());
            model.addAttribute("pageTitle", "Update brand: " + brandById.getId());
            return "brands/brand_form";

        } catch (RuntimeException ex) {
            attributes.addFlashAttribute("exceptionMessage", "Error occurred due to " + ex.getMessage());
        } catch (BrandNotFoundException e) {
            attributes.addFlashAttribute("exceptionMessage", "Brand with id: " + id + " not found!");
        }

        return listAll();
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Brand> brands = brandService.listAll();
        csvExporter.export(response, brands);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) {
        List<Brand> brands = brandService.listAll();
        excelExporter.export(response, brands);
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) {
        List<Brand> brands = brandService.listAll();
        pdfExporter.export(response, brands);
    }
}
