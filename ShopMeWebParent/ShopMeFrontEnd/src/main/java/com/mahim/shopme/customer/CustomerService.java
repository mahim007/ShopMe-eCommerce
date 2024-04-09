package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.setting.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CountryRepository countryRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CountryRepository countryRepository, CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder) {
        this.countryRepository = countryRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;

    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());
        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);
        customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepository.enable(customer.getId());
            return true;
        }
    }
}
