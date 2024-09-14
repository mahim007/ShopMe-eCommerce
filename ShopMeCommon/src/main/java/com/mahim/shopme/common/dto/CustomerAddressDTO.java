package com.mahim.shopme.common.dto;

import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerAddressDTO {
    private Customer customer;
    private Address address;
    
    public CustomerAddressDTO(Customer customer) {
        this.customer = customer;
        initAddress();
    }

    private void initAddress() {
        address = new Address();
        address.setFirstName(customer.getFirstName());
        address.setLastName(customer.getLastName());
        address.setPhoneNumber(customer.getPhoneNumber());
        address.setAddressLine1(customer.getAddressLine1());
        address.setAddressLine2(customer.getAddressLine2());
        address.setCity(customer.getCity());
        address.setState(customer.getState());
        address.setPostalCode(customer.getPostalCode());
        address.setCountry(customer.getCountry());
        address.setDefaultForShipping(true);
    }
}
