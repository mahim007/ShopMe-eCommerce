package com.mahim.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "addresses")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Address extends AbstractAddress {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "default_for_shipping")
    private boolean defaultForShipping;

    @Override
    public String toString() {
        String address = firstName;
        if (lastName != null && !lastName.isEmpty()) address += " " + lastName;
        if (addressLine1 != null && !addressLine1.isEmpty()) address += ", " + addressLine1;
        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
        if (city != null && !city.isEmpty()) address += ", " + city;
        if (state != null && !state.isEmpty()) address += ", " + state;
        address += ", " + country.getName();
        if (postalCode != null && !postalCode.isEmpty()) address += ", Postal Code: " + postalCode;
        if (phoneNumber != null && !phoneNumber.isEmpty()) address += ", Phone Number: " + phoneNumber;
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;
        return defaultForShipping == address.defaultForShipping && Objects.equals(id, address.id) && Objects.equals(firstName, address.firstName)
                && Objects.equals(lastName, address.lastName) && Objects.equals(phoneNumber, address.phoneNumber) &&
                Objects.equals(addressLine1, address.addressLine1) && Objects.equals(addressLine2, address.addressLine2)
                && city.equals(address.city) && state.equals(address.state) && Objects.equals(postalCode, address.postalCode)
                && customer.getId().equals(address.customer.getId()) && country.getId().equals(address.country.getId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(firstName);
        result = 31 * result + Objects.hashCode(lastName);
        result = 31 * result + Objects.hashCode(phoneNumber);
        result = 31 * result + Objects.hashCode(addressLine1);
        result = 31 * result + Objects.hashCode(addressLine2);
        result = 31 * result + city.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + Objects.hashCode(postalCode);
        result = 31 * result + customer.getId().hashCode();
        result = 31 * result + country.getId().hashCode();
        result = 31 * result + Boolean.hashCode(defaultForShipping);
        return result;
    }
}
