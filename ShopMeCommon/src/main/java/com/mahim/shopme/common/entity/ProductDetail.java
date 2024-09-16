package com.mahim.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product_details")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductDetail extends ParentEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetail(Integer id, String name, String value, Product product) {
        super(id);
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
