package com.mahim.shopme.common.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "reviews")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Review extends ParentEntity {
    private String headline;
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
