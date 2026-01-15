package com.mahim.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_details")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderDetail extends ParentEntity {

    private int quantity;
    private float productCost;
    private float shippingCost;
    private float unitPrice;
    private float subtotal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderDetail(String categoryName, int quantity, float productCost, float shippingCost, float subtotal) {
        this.product = new Product();
        this.product.setCategory(new Category(categoryName));
        this.quantity = quantity;
        this.productCost = productCost;
        this.shippingCost = shippingCost;
        this.subtotal = subtotal;
    }

    public OrderDetail(int quantity, String productName, float productCost, float shippingCost, float subtotal) {
        this.product = new Product(productName);
        this.quantity = quantity;
        this.productCost = productCost;
        this.shippingCost = shippingCost;
        this.subtotal = subtotal;
    }
}