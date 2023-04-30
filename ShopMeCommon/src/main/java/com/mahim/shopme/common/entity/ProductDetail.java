package com.mahim.shopme.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product_details")
@Getter @Setter @NoArgsConstructor
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetail(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
