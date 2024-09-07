package com.mahim.shopme.address;

import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Customer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }
}
