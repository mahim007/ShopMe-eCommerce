package com.mahim.shopme.admin.customer;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.setting.country.CountryRepository;
import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.enums.AuthenticationType;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    public static final int CUSTOMERS_PER_PAGE = 10;

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> listAll() {
        return (List<Customer>) customerRepository.findAll();
    }

     public Page<Customer> listByKeyword(int pageNum, PagingAndSortingHelper helper) {
         return (Page<Customer>) helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepository);
     }

     public Customer save(Customer customer) {
         Customer customerFromDB = customerRepository.findById(customer.getId()).get();

         customerFromDB.setEmail(customer.getEmail());
         if (!customer.getPassword().isEmpty()) {
             encodePassword(customer);
             customerFromDB.setPassword(customer.getPassword());
         }
         customerFromDB.setFirstName(customer.getFirstName());
         customerFromDB.setLastName(customer.getLastName());
         customerFromDB.setPhoneNumber(customer.getPhoneNumber());
         customerFromDB.setAddressLine1(customer.getAddressLine1());
         customerFromDB.setAddressLine2(customer.getAddressLine2());
         customerFromDB.setCity(customer.getCity());
         customerFromDB.setState(customer.getState());
         customerFromDB.setCountry(customer.getCountry());
         customerFromDB.setPostalCode(customer.getPostalCode());
         customerFromDB.setEnabled(customerFromDB.isEnabled() || customer.isEnabled());

         return customerRepository.save(customerFromDB);

     }

     private void encodePassword(Customer customer) {
         String encoded = passwordEncoder.encode(customer.getPassword());
         customer.setPassword(encoded);
     }

     public Customer findByEmail(String email) throws CustomerNotFoundException {
         Optional<Customer> optional = customerRepository.findByEmail(email);
         if (optional.isPresent()) {
             return optional.get();
         } else {
             throw new CustomerNotFoundException("No Customer found with email: " + email);
         }
     }

     public Customer findById(Integer id) throws CustomerNotFoundException {
         Optional<Customer> optional = customerRepository.findById(id);
         if (optional.isPresent()) {
             return optional.get();
         } else {
             throw new CustomerNotFoundException("No Customer found with id: " + id);
         }
     }

     public void delete(Integer id) throws CustomerNotFoundException {
         Customer customer = findById(id);
         customerRepository.delete(customer);
     }

     public void updateEnabledStatus(Integer id, boolean enabled) throws CustomerNotFoundException {
        Customer customer = findById(id);
        customerRepository.updateEnabledStatus(customer.getId(), !enabled);
     }

     public boolean isEmailUnique(Integer id, String email) {
         Optional<Customer> optional = customerRepository.findByEmail(email);
         if (optional.isEmpty()) return true;

         Customer customer = optional.get();
         if (id == null) {
             return false;
         } else {
             return id.equals(customer.getId());
         }
     }

     public List<Country> listAllCountry() {
        return countryRepository.findAllByOrderByNameAsc();
     }

     public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
        if (!customer.getAuthenticationType().equals(authenticationType)) {
            customerRepository.updateAuthenticationType(authenticationType, customer.getId());
        }
     }

}
