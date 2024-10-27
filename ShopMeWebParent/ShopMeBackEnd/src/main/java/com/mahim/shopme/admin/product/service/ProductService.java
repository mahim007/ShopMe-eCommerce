package com.mahim.shopme.admin.product.service;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.admin.product.repository.ProductRepository;
import com.mahim.shopme.common.entity.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mahim.shopme.common.util.StaticPathUtils.PRODUCT_UPLOAD_DIR;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public Page<Product> listByKeyword(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
        Sort sort = Sort.by(helper.getSortField());
        sort = StringUtils.equals(helper.getSortDir(), "asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
        String categoryIdMatch = "-" + categoryId + "-";

        return isValidKeyword(helper.getKeyword()) && isValidCategoryId(categoryId) ?
                productRepository.searchInCategory(categoryId, categoryIdMatch, helper.getKeyword(), pageable) :
                isValidKeyword(helper.getKeyword()) ? productRepository.findAllByKeyword(helper.getKeyword(), pageable) :
                        isValidCategoryId(categoryId) ?
                                productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable) :
                                productRepository.findAll(pageable);
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Sort sort = Sort.by(helper.getSortField());
        sort = StringUtils.equals(helper.getSortDir(), "asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        Page<Product> productPage = productRepository.searchProductsByName(helper.getKeyword(), pageable);
        helper.updateModelAttributes(pageNum, productPage);
    }

    private static boolean isValidCategoryId(Integer categoryId) {
        return categoryId != null && categoryId > 0;
    }

    private static boolean isValidKeyword(String keyword) {
        return StringUtils.isNotEmpty(keyword) && StringUtils.isNotBlank(keyword) && StringUtils.length(keyword) >= 3;
    }

    public Product save(Product product) {
        boolean isUpdating = product.getId() != null;
        Product productToBeSaved;

        if (isUpdating) {
            Optional<Product> optionalProduct = productRepository.findById(product.getId());
            if (optionalProduct.isPresent()) {
                productToBeSaved = optionalProduct.get();
                productToBeSaved.setName(product.getName());
                productToBeSaved.setAlias(product.getAlias());
                productToBeSaved.setShortDescription(product.getShortDescription());
                productToBeSaved.setFullDescription(product.getFullDescription());
                productToBeSaved.setUpdatedTime(new Date());
                productToBeSaved.setEnabled(product.isEnabled());
                productToBeSaved.setInStock(product.isInStock());
                productToBeSaved.setCost(product.getCost());
                productToBeSaved.setPrice(product.getPrice());
                productToBeSaved.setDiscountPercent(product.getDiscountPercent());
                productToBeSaved.setLength(product.getLength());
                productToBeSaved.setWidth(product.getWidth());
                productToBeSaved.setHeight(product.getHeight());
                productToBeSaved.setWeight(product.getWeight());
                productToBeSaved.getImages().retainAll(product.getImages());
                productToBeSaved.getImages().addAll(product.getImages());
                productToBeSaved.getDetails().retainAll(product.getDetails());
                productToBeSaved.getDetails().addAll(product.getDetails());
            } else {
                productToBeSaved = product;
            }
        } else {
            product.setCreatedTime(new Date());
            product.setUpdatedTime(new Date());
            product.setAlias(StringUtils.isEmpty(product.getAlias()) || StringUtils.isBlank(product.getAlias()) ?
                    product.getName().toLowerCase().replaceAll(" ", "-") :
                            product.getAlias().toLowerCase().replaceAll(" ", "-")
                    );

            productToBeSaved = product;
        }

        return productRepository.save(productToBeSaved);
    }

    public void saveProductPrice(Product product) {
        Product productInDB = productRepository.findById(product.getId()).get();
        productInDB.setCost(product.getCost());
        productInDB.setPrice(product.getPrice());
        productInDB.setDiscountPercent(product.getDiscountPercent());

        productRepository.save(productInDB);
    }

    public Product findProductById(Integer id) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            throw new ProductNotFoundException("Product with id: " + id + " not found!");
        }
    }

    public void delete(Integer id) throws ProductNotFoundException {
        int count = productRepository.countById(id);
        if (count == 0) {
            throw new ProductNotFoundException("Product with id: " + id + " not found!");
        }

        productRepository.deleteById(id);
        FileUploadUtil.removeDir(PRODUCT_UPLOAD_DIR + "/" + id + "/extras");
        FileUploadUtil.removeDir(PRODUCT_UPLOAD_DIR + "/" + id);
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = id == null || id == 0;

        Product productByName = productRepository.findByName(name);

        String calculatedAlias = StringUtils.isEmpty(alias) || StringUtils.isBlank(alias) ?
                name.toLowerCase().replaceAll(" ", "-") :
                alias.toLowerCase().replaceAll(" ", "-");
        Product productByAlias = productRepository.findByAlias(calculatedAlias);

        if (isCreatingNew) {
            if (productByName != null) {
                return "DuplicatedName";
            } else if (productByAlias != null) {
                return "DuplicatedAlias";
            }
        } else {
            if (productByName != null && !productByName.getId().equals(id)) {
                return "DuplicatedName";
            } else if (productByAlias != null && !productByAlias.getId().equals(id)) {
                return "DuplicatedAlias";
            }
        }

        return "OK";
    }

    public boolean updateProductStatus(Integer id, boolean status) {
        try {
            productRepository.updateEnableStatus(id, !status);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
