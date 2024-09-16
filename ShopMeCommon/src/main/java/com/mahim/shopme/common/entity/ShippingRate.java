package com.mahim.shopme.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "shipping_rates")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class ShippingRate extends ParentEntity {

    @Column(nullable = false)
    private float rate;

    @Column(nullable = false)
    private int days;

    @Column(name = "cod_supported", nullable = false)
    private boolean codSupported;

    @OneToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false)
    private String state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShippingRate that = (ShippingRate) o;
        return Objects.equals(id, that.id) && Objects.equals(country, that.country) && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(country);
        result = 31 * result + Objects.hashCode(state);
        return result;
    }
}
