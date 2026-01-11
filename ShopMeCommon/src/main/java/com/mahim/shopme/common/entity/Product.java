package com.mahim.shopme.common.entity;

import com.mahim.shopme.common.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor
@ToString
public class Product extends ParentEntity {

    @Column(unique = true, length = 256, nullable = false)
    private String name;

    @Column(unique = true, length = 256, nullable = false)
    private String alias;

    @Column(name = "short_description", length = 1024, nullable = false)
    private String shortDescription;

    @Column(name = "full_description", length = 4096, nullable = false)
    private String fullDescription;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    private boolean enabled;

    @Column(name = "in_stock")
    private boolean inStock;

    private float cost;
    private float price;

    @Column(name = "discount_percent")
    private float discountPercent;

    private float length;
    private float width;
    private float height;
    private float weight;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductDetail> details = new HashSet<>();

    public Product(Integer id) {
        this.id = id;
    }

    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    public void addExtraImage(Integer id, String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    public void addDetail(String name, String value) {
        this.details.add(new ProductDetail(name, value, this));
    }

    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null) {
            return "/images/default-category.png";
        }
        return Constants.S3_BASE_URL + "/product-photos/" + this.id + "/" + this.mainImage;
    }

    public boolean containsImageName(String fileName) {
        for (ProductImage image : this.images) {
            if (image.getName().equals(fileName)) {
                return true;
            }
        }

        return false;
    }

    @Transient
    public String getShortName() {
        if (this.name.length() > 56) {
            return this.name.substring(0, 56).concat("...");
        }

        return this.name;
    }

    @Transient
    public float getDiscountPrice() {
        if (this.discountPercent > 0) {
            return this.price * ((100 - this.discountPercent) / 100);
        }

        return this.price;
    }
}
