package com.mahim.shopme.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter @Setter
public class Category extends ParentEntity {

    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 64, nullable = false, unique = true)
    private String alias;

    @Column(length = 128, nullable = false)
    private String photos;

    private boolean enabled;

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIDs;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();

    public Category() {
    }

    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(Integer id, String name, String alias, String photos, boolean enabled,
                    Category parent, Set<Category> children) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.photos = photos;
        this.enabled = enabled;
        this.parent = parent;
        this.children = children;
    }

    @Transient
    public String getImagePath() {
        if (this.id == null || this.photos == null) return "/images/default-category.png";
        return "/category-photos/" + this.id + "/" + this.photos;
    }

    @Transient
    public boolean hasChildren() {
        return !this.getChildren().isEmpty();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
