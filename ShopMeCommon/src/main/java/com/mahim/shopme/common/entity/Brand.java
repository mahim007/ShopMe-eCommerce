package com.mahim.shopme.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@Getter @Setter
public class Brand extends ParentEntity {

    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    private String logo;

    private boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @Transient
    public String getImagePath() {
        if (this.id == null || this.logo == null) return "/images/default-category.png";
        return "/brand-photos/" + this.id + "/" + this.logo;
    }

    public Brand() {
    }

    public Brand(Integer id, String name, String logo, boolean enabled, Set<Category> categories) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.enabled = enabled;
        this.categories = categories;
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
