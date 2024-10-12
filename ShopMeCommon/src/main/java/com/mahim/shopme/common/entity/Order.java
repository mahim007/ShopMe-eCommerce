package com.mahim.shopme.common.entity;

import com.mahim.shopme.common.dto.CustomerAddressDTO;
import com.mahim.shopme.common.enums.OrderStatus;
import com.mahim.shopme.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.*;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderTrack> orderTracks = new ArrayList<>();

    @Transient
    public String getDestination() {
        String destination = "";
        destination += StringUtils.hasText(city) ? city + ", " : "";
        destination += StringUtils.hasText(state) ? state + ", " : "";
        destination += StringUtils.hasText(country) ? country : "";
        return destination;
    }

    public void copyAddressFromCustomer() {
        CustomerAddressDTO customerAddressDTO = new CustomerAddressDTO(customer);
        copyAddressFrom(customerAddressDTO.getAddress());
    }

    public void copyAddressFrom(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setCountry(address.getCountry().getName());
        setPostalCode(address.getPostalCode());
        setState(address.getState());
    }

    public String getShippingAddress() {
        String address = firstName;
        if (lastName != null && !lastName.isEmpty()) address += " " + lastName;
        if (addressLine1 != null && !addressLine1.isEmpty()) address += ", " + addressLine1;
        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
        if (city != null && !city.isEmpty()) address += ", " + city;
        if (state != null && !state.isEmpty()) address += ", " + state;
        address += ", " + country;
        if (postalCode != null && !postalCode.isEmpty()) address += ", Postal Code: " + postalCode;
        if (phoneNumber != null && !phoneNumber.isEmpty()) address += ", Phone Number: " + phoneNumber;
        return address;
    }
}
