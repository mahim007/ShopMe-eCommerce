package com.mahim.shopme.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reviews")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Review extends ParentEntity {
    @Column(length = 100)
    private String headline;
    @Column(length = 512)
    private String comment;
    private int rating;
    private Date reviewTime;
    private boolean isModerated;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Review(Customer customer, Product product) {
        this.customer = customer;
        this.product = product;
    }
}
