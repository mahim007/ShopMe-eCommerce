package com.mahim.shopme.shipping;

import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.ShippingRate;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {

    private final ShippingRateRepository shippingRateRepository;

    public ShippingRateService(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }

        return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }

        return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
    }
}
