package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.setting.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CountryRepository countryRepository;
    private final CustomerRepository customerRepository;

    public CustomerService(CountryRepository countryRepository, CustomerRepository customerRepository) {
        this.countryRepository = countryRepository;
        this.customerRepository = customerRepository;
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;

    }
}
