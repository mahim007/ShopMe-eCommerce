package com.mahim.shopme.admin.brand.controller;

import com.mahim.shopme.admin.brand.service.BrandService;
import com.mahim.shopme.common.entity.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.mahim.shopme.admin.brand.service.BrandService.BRANDS_PER_PAGE;

@Controller
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/brands/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(
            @PathVariable(name = "pageNo") int pageNo,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        keyword = StringUtils.trim(keyword);
        if (StringUtils.isEmpty(keyword) || StringUtils.isBlank(keyword)) {
            keyword = null;
        }

        Page<Brand> brandPage = keyword == null ? brandService.listByPage(pageNo, sortField, sortDir) :
                brandService.listByKeyword(pageNo, sortField, sortDir, keyword);

        int totalPages = brandPage.getTotalPages();
        long totalElements = brandPage.getTotalElements();
        int currentPageSize = brandPage.getNumberOfElements();
        int startNo = ((pageNo - 1) * BRANDS_PER_PAGE) + 1;
        int endNo = pageNo * BRANDS_PER_PAGE;

        List<Brand> brands = brandPage.getContent();
        String reverseSortDir = StringUtils.equals(sortDir, "asc") ? "desc" : "asc";

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("brands", brands);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        return "brands/brands";
    }
}
