package com.mahim.shopme.common.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String image) {
        this.photos = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    public String getAllParentIDs() {
        return allParentIDs;
    }

    public void setAllParentIDs(String allParentIDs) {
        this.allParentIDs = allParentIDs;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
