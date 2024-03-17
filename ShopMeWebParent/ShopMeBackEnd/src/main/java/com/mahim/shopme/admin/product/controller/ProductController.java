package com.mahim.shopme.admin.product.controller;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.brand.service.BrandService;
import com.mahim.shopme.admin.category.CategoryNotFoundException;
import com.mahim.shopme.admin.category.service.CategoryService;
import com.mahim.shopme.admin.product.exception.ProductNotFoundException;
import com.mahim.shopme.admin.product.service.ProductService;
import com.mahim.shopme.common.dto.CategoryDTO;
import com.mahim.shopme.common.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mahim.shopme.admin.product.service.ProductService.PRODUCTS_PER_PAGE;
import static com.mahim.shopme.admin.utils.StaticPathUtils.PRODUCT_UPLOAD_DIR;

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
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "categoryId", defaultValue = "0") Integer categoryId,
            Model model
    ) {
        System.out.println("Category Id: " + categoryId);
        keyword = StringUtils.trim(keyword);
        if (StringUtils.isEmpty(keyword) || StringUtils.isBlank(keyword)) {
            keyword = null;
        }

        Page<Product> productsPage = productService.listByKeyword(pageNo, sortField, sortDir, keyword, categoryId);

        int totalPages = productsPage.getTotalPages();
        long totalElements = productsPage.getTotalElements();
        int startNo = ((pageNo - 1) * PRODUCTS_PER_PAGE) + 1;
        int endNo = pageNo * PRODUCTS_PER_PAGE;

        List<Product> products = productsPage.getContent();
        String reverseSortDir = StringUtils.equals(sortDir, "asc") ? "desc" : "asc";

        List<Category> hierarchicalCategories = categoryService.listAll();
        String categoryName = "";
        try {
            Category categoryById = categoryService.findCategoryById(categoryId);
            categoryName = categoryById.getName();
        } catch (CategoryNotFoundException ex) {
            categoryName = "All Categories";
        }

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo < totalElements ? endNo : totalElements);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("products", products);
        model.addAttribute("categories", hierarchicalCategories);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryName", categoryName);
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
        model.addAttribute("categories", new HashSet<Product>());
        model.addAttribute("numOfExtraImages", 0);
        model.addAttribute("numOfDetailItems", 0);
        model.addAttribute("pageTitle", "Create New Product");
        return "products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product product,
                              @RequestParam(name = "image") MultipartFile mainImage,
                              @RequestParam(name = "extraImage") MultipartFile[] extraImages,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              RedirectAttributes redirectAttributes) {
        Product productToBeSaved = null;
        try {
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

    private void deleteUnusedImages(Product product) {
        String extraImagesDirectory = PRODUCT_UPLOAD_DIR + "/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImagesDirectory);

        try {
            Files.list(dirPath).forEach(file  -> {
                String fileName = file.toFile().getName();
                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        LOGGER.error("Error occurred when deleting {} due to {}", fileName, e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Error occurred when traversing directory {} due to {}", dirPath.toString() , e.getMessage());
        }
    }

    private void setMainImageName(MultipartFile mainImage, Product product) {
        if (!mainImage.isEmpty() && mainImage.getOriginalFilename() != null) {
            String fileName = org.springframework.util.StringUtils.cleanPath(mainImage.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;
        if (imageNames == null || imageNames.length == 0) return;

        Set<ProductImage> images = new HashSet<>();

        for (int i = 0; i < imageIDs.length; i++) {
            int id = Integer.parseInt(imageIDs[i]);
            String name = imageNames[i];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    private void setNewExtraImageNames(MultipartFile[] extraImages, Product product) {
        if (extraImages != null) {
            for(MultipartFile extraImage : extraImages) {
                if (!extraImage.isEmpty() && extraImage.getOriginalFilename() != null) {
                    String fileName = org.springframework.util.StringUtils.cleanPath(extraImage.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, String[] detailIDs, Product product) {
        if (detailNames == null || detailNames.length == 0) return;
        if (detailValues == null || detailValues.length == 0) return;

        Set<ProductDetail> details = new HashSet<>();
        for (int i = 0; i < detailNames.length; i++) {
            if (StringUtils.isNotEmpty(detailNames[i]) && StringUtils.isNotEmpty(detailValues[i])) {
                if (i < detailIDs.length) {
                    details.add(new ProductDetail(Integer.parseInt(detailIDs[i]), detailNames[i], detailValues[i], product));
                } else {
                    details.add(new ProductDetail(detailNames[i], detailValues[i], product));
                }
            }
        }

        product.setDetails(details);
    }

    private void saveUploadedImages(MultipartFile mainImage, MultipartFile[] extraImages, Product product)
            throws IOException {

        if (!mainImage.isEmpty() && mainImage.getOriginalFilename() != null) {
            String fileName = org.springframework.util.StringUtils.cleanPath(mainImage.getOriginalFilename());
//            FileUploadUtil.cleanDir(PRODUCT_UPLOAD_DIR + "/" + product.getId());
            FileUploadUtil.saveFile(PRODUCT_UPLOAD_DIR + "/" + product.getId(), fileName, mainImage);
        }

        if (extraImages != null) {
//            FileUploadUtil.cleanDir(PRODUCT_UPLOAD_DIR + "/" + product.getId() + "/extras");
            for(MultipartFile extraImage : extraImages) {
                if (!extraImage.isEmpty() && extraImage.getOriginalFilename() != null) {
                    String fileName = org.springframework.util.StringUtils.cleanPath(extraImage.getOriginalFilename());
                    FileUploadUtil.saveFile(PRODUCT_UPLOAD_DIR + "/" + product.getId() + "/extras", fileName, extraImage);
                }
            }
        }
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
    public String editProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product productById = productService.findProductById(id);
            List<Brand> brands = brandService.listAllSorted();

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
