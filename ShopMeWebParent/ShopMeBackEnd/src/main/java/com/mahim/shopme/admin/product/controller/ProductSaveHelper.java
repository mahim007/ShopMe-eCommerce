package com.mahim.shopme.admin.product.controller;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.entity.ProductDetail;
import com.mahim.shopme.common.entity.ProductImage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static com.mahim.shopme.common.util.StaticPathUtils.PRODUCT_UPLOAD_DIR;

public class ProductSaveHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    static void deleteUnusedImages(Product product) {
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

    static void setMainImageName(MultipartFile mainImage, Product product) {
        if (mainImage != null && !mainImage.isEmpty() && mainImage.getOriginalFilename() != null) {
            String fileName = org.springframework.util.StringUtils.cleanPath(mainImage.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
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

    static void setNewExtraImageNames(MultipartFile[] extraImages, Product product) {
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

    static void setProductDetails(String[] detailNames, String[] detailValues, String[] detailIDs, Product product) {
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

    static void saveUploadedImages(MultipartFile mainImage, MultipartFile[] extraImages, Product product)
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
}
