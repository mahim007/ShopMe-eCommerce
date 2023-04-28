package com.mahim.shopme.admin.product.controller;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.brand.service.BrandService;
import com.mahim.shopme.admin.product.exception.ProductNotFoundException;
import com.mahim.shopme.admin.product.service.ProductService;
import com.mahim.shopme.common.entity.Brand;
import com.mahim.shopme.common.entity.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.mahim.shopme.admin.product.service.ProductService.PRODUCTS_PER_PAGE;
import static com.mahim.shopme.admin.utils.StaticPathUtils.PRODUCT_UPLOAD_DIR;

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
    public String saveProduct(Product product,
                              @RequestParam(name = "image") MultipartFile multipartFile,
                              RedirectAttributes redirectAttributes) {
        Product productToBeSaved = null;
        try {
            if (!multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
                String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
                product.setMainImage(fileName);

                productToBeSaved = productService.save(product);
                FileUploadUtil.cleanDir(PRODUCT_UPLOAD_DIR + "/" + productToBeSaved.getId());
                FileUploadUtil.saveFile(PRODUCT_UPLOAD_DIR + "/" + productToBeSaved.getId(), fileName, multipartFile);
            } else {
                productToBeSaved = productService.save(product);
            }

            redirectAttributes.addFlashAttribute("message","The product has been saved successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Product with id: " +
                    productToBeSaved.getId() + " could not be saved!");
        }
        return listAll();
    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateEnableStatus(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "enabled") boolean enabled,
            RedirectAttributes redirectAttributes
    ) {
        try {
            boolean status = productService.updateProductStatus(id, enabled);
            if (status) {
                redirectAttributes.addFlashAttribute("message", "Enabled status updated for " +
                        "Product (ID:" + id + ")");
            } else {
                redirectAttributes.addFlashAttribute("exceptionMessage", "Enabled status could not be updated.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Product not found " +
                    "(ID: " + id + ")");
        }

        return listAll();
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable(name = "id") Integer id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The Product ( id: " + id +" ) has been deleted");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Product with id: " + id + " not found.");
        }

        return listAll();
    }

}
