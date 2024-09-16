package com.mahim.shopme.common.entity;

import com.mahim.shopme.common.enums.OrderStatus;
import com.mahim.shopme.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Order extends AbstractAddress {

    @Column(nullable = false, length = 45)
    private String country;

    private Date orderTime;

    private float shippingCost;
    private float productCost;
    private float tax;
    private float subtotal;
    private float total;

    private int deliverDays;
    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @Transient
    public String getDestination() {
        String destination = "";
        destination += StringUtils.hasText(city) ? city + ", " : "";
        destination += StringUtils.hasText(state) ? state + ", " : "";
        destination += StringUtils.hasText(country) ? country : "";
        return destination;
    }
}
