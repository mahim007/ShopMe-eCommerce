package com.mahim.shopme.admin.order;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.admin.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class ProductSearchController {

    private final ProductService productService;

    public ProductSearchController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search_product")
    public String showSearchProductPage() {
        return "orders/search_product";
    }

    @GetMapping("/search_product/page/{pageNum}")
    public String searchProductByPage(
            @PagingAndSortingParam(listName = "products", moduleURL = "/orders/search_product") PagingAndSortingHelper helper,
            @PathVariable int pageNum) {
        productService.searchProducts(pageNum, helper);
        return "/orders/search_product";
    }

    @PostMapping("/search_product")
    public String searchProducts(String keyword, Model model) {
        return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
    }
}
