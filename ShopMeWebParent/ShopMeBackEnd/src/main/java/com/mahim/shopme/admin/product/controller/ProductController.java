package com.mahim.shopme.admin.product.controller;

import com.mahim.shopme.admin.brand.service.BrandService;
import com.mahim.shopme.admin.product.service.ProductService;
import com.mahim.shopme.common.entity.Brand;
import com.mahim.shopme.common.entity.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mahim.shopme.admin.product.service.ProductService.PRODUCTS_PER_PAGE;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final BrandService brandService;

    public ProductController(ProductService productService, BrandService brandService) {
        this.productService = productService;
        this.brandService = brandService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/products/page/1";
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

        Page<Product> productsPage = keyword == null ? productService.listByPage(pageNo, sortField, sortDir) :
                productService.listByKeyword(pageNo, sortField, sortDir, keyword);

        int totalPages = productsPage.getTotalPages();
        long totalElements = productsPage.getTotalElements();
        int startNo = ((pageNo - 1) * PRODUCTS_PER_PAGE) + 1;
        int endNo = pageNo * PRODUCTS_PER_PAGE;

        List<Product> products = productsPage.getContent();
        String reverseSortDir = StringUtils.equals(sortDir, "asc") ? "asc" : "desc";

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo < totalElements ? endNo : totalElements);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("products", products);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        return "products/products";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        List<Brand> brands = brandService.listAllSorted();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", "Create New Product");
        return "products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product product) {
        System.out.println(product.toString());
        return listAll();
    }
}
