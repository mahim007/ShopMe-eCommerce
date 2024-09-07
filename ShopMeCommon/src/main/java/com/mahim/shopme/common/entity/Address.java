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
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name" ,nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name" ,nullable = false, length = 45)
    private String lastName;

    @Column(name = "phone_number" ,nullable = false, length = 45)
    private String phoneNumber;

    @Column(name = "address_line1", nullable = false, length = 64)
    private String addressLine1;

    @Column(name = "address_line2", nullable = false, length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 45)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

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
        return "Address{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", customer=" + customer.getEmail() +
                ", country=" + country.getName() +
                ", defaultForShipping=" + defaultForShipping +
                '}';
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
