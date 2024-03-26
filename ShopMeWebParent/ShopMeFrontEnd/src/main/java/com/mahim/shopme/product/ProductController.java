package com.mahim.shopme.product;

import com.mahim.shopme.category.CategoryService;
import com.mahim.shopme.common.entity.Category;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.exception.CategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.mahim.shopme.product.ProductService.PRODUCTS_PER_PAGE;

@Controller
@RequestMapping("/c")
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/{category_alias}/page/{pageNo}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                               @PathVariable("pageNo") Integer pageNo,
                               Model model) {
        Category category;

        try {
            category = categoryService.getCategory(alias);
        } catch (CategoryNotFoundException e) {
            return "error/404";
        }

        List<Category> listCategoryParents = categoryService.getCategoryParents(category);
        Page<Product> productPage = productService.listByCategory(pageNo, category.getId());
        List<Product> products = productPage.getContent();

        int totalPages = productPage.getTotalPages();
        long totalElements = productPage.getTotalElements();
        int startNo = ((pageNo - 1) * PRODUCTS_PER_PAGE) + 1;
        int endNo = pageNo * PRODUCTS_PER_PAGE;

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo < totalElements ? endNo : totalElements);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("pageTitle", category.getName());
        model.addAttribute("alias", alias);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("listCategoryParents", listCategoryParents);

        return "product/products_by_category";
    }
}
