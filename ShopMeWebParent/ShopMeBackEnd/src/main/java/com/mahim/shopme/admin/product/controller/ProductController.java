package com.mahim.shopme.admin.product.controller;

import com.mahim.shopme.admin.brand.service.BrandService;
import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.common.exception.CategoryNotFoundException;
import com.mahim.shopme.admin.category.service.CategoryService;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.admin.product.service.ProductService;
import com.mahim.shopme.admin.security.ShopmeUserDetails;
import com.mahim.shopme.common.dto.CategoryDTO;
import com.mahim.shopme.common.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mahim.shopme.admin.product.controller.ProductSaveHelper.*;
import static com.mahim.shopme.admin.product.service.ProductService.PRODUCTS_PER_PAGE;

@Controller
@RequestMapping("/products")
public class ProductController {
    private static  final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, BrandService brandService, CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/products/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(
            @PathVariable(name = "pageNo") int pageNo,
            @PagingAndSortingParam(listName = "products", moduleURL = "/products") PagingAndSortingHelper helper,
            @RequestParam(name = "categoryId", defaultValue = "0") Integer categoryId,
            Model model) {

        Page<Product> products = productService.listByKeyword(pageNo, helper, categoryId);
        helper.updateModelAttributes(pageNo, products);

        List<Category> hierarchicalCategories = categoryService.listAll();
        String categoryName = "";
        try {
            Category categoryById = categoryService.findCategoryById(categoryId);
            categoryName = categoryById.getName();
        } catch (CategoryNotFoundException ex) {
            categoryName = "All Categories";
        }

        helper.addAttribute("categories", hierarchicalCategories);
        helper.addAttribute("categoryId", categoryId);
        helper.addAttribute("categoryName", categoryName);
        return "products/products";
    }

    @GetMapping("/new")
    public String newProduct(Model model, @AuthenticationPrincipal ShopmeUserDetails loggedInUser) {
        List<Brand> brands = brandService.listAllSorted();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        boolean isReadOnlyForSalesPerson = false;

        if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Editor")
                && loggedInUser.hasRole("Salesperson")) {
            isReadOnlyForSalesPerson = true;
        }

        model.addAttribute("product", product);
        model.addAttribute("brands", brands);
        model.addAttribute("categories", new HashSet<Product>());
        model.addAttribute("numOfExtraImages", 0);
        model.addAttribute("numOfDetailItems", 0);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("isReadOnlyForSalesPerson", isReadOnlyForSalesPerson);
        return "products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product product,
                              @RequestParam(name = "image", value = "image", required = false) MultipartFile mainImage,
                              @RequestParam(name = "extraImage", value = "extraImage", required = false) MultipartFile[] extraImages,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @AuthenticationPrincipal ShopmeUserDetails loggedInUser,
                              RedirectAttributes redirectAttributes) {
        Product productToBeSaved = null;
        try {
            if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Editor") 
                    && loggedInUser.hasRole("Salesperson")) {
                productService.saveProductPrice(product);
                redirectAttributes.addFlashAttribute("message","The product has been saved successfully.");
                return listAll();
            }

            setMainImageName(mainImage, product);
            setExistingExtraImageNames(imageIDs, imageNames, product);
            setNewExtraImageNames(extraImages, product);

            detailIDs = detailIDs == null ? new String[0] : detailIDs;
            setProductDetails(detailNames, detailValues, detailIDs, product);

            productToBeSaved = productService.save(product);

            saveUploadedImages(mainImage, extraImages, productToBeSaved);
            deleteUnusedImages(product);
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
            @PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The Product ( id: " + id +" ) has been deleted");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Product with id: " + id + " not found.");
        }

        return listAll();
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal ShopmeUserDetails loggedInUser) {
        try {
            Product productById = productService.findProductById(id);
            List<Brand> brands = brandService.listAllSorted();
            boolean isReadOnlyForSalesPerson = false;

            if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Editor")
                    && loggedInUser.hasRole("Salesperson")) {
                isReadOnlyForSalesPerson = true;
            }

            int numOfExtraImages = productById.getImages().size();
            int numOfDetailItems = productById.getDetails().size();

            List<CategoryDTO> categories = productById.getBrand().getCategories().stream()
                    .filter(category -> Objects.equals(category.getId(), productById.getCategory().getId()))
                    .map(category -> new CategoryDTO(category.getId(), category.getName()))
                    .sorted((a, b) -> a.getId() - b.getId())
                    .collect(Collectors.toList());

            model.addAttribute("product", productById);
            model.addAttribute("brands", brands);
            model.addAttribute("categories", categories);
            model.addAttribute("numOfExtraImages", numOfExtraImages);
            model.addAttribute("numOfDetailItems", numOfDetailItems);
            model.addAttribute("isReadOnlyForSalesPerson", isReadOnlyForSalesPerson);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + " )");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Product with id: " + id + " not found.");
            return "redirect:/products";
        }

        return "products/product_form";
    }

    @GetMapping("/details/{id}")
    public String viewProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product productById = productService.findProductById(id);
            model.addAttribute("product", productById);
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "Product with id: " + id + " not found.");
            return "redirect:/products";
        }

        return "products/product_details_modal";
    }

}
