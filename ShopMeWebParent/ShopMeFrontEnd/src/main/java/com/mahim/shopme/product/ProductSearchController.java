package com.mahim.shopme.product;

import com.mahim.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.mahim.shopme.product.ProductService.PRODUCTS_PER_PAGE;

@Controller
@RequestMapping("")
public class ProductSearchController {

    private final ProductService productService;

    public ProductSearchController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public String searchFirstPage(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {
        return searchByPage(keyword, 1, model);
    }

    @GetMapping("/search/page/{pageNo}")
    public String searchByPage(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @PathVariable(name = "pageNo") int pageNo,
            Model model) {
        Page<Product> productPage = productService.search(keyword, pageNo);
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
        model.addAttribute("pageTitle", keyword + " - Search Result");
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product/search_results";
    }
}
