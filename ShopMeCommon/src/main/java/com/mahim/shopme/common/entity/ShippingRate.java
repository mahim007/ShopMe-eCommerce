package com.mahim.shopme.common.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "shipping_rates")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class ShippingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private float rate;

    @Column(nullable = false)
    private int days;

    @Column(name = "cod_supported", nullable = false)
    private boolean codSupported;

    @OneToOne
    private Country country;

    @Column(nullable = false)
    private String state;
}
