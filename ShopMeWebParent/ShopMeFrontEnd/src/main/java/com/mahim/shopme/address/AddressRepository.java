package com.mahim.shopme.address;

import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends CrudRepository<Address, Integer> {

    List<Address> findByCustomer(Customer customer);

    Optional<Address> findByIdAndCustomer(Integer id, Customer customer);

    @Modifying
    void deleteByIdAndCustomer(Integer id, Customer customer);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = :defaultForShipping WHERE a.id = :id")
    void setDefaultShippingAddress(Integer id, boolean defaultForShipping);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id != :id AND a.customer.id = :customerId")
    void setNonDefaultShippingAddress(Integer id, Integer customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId and a.defaultForShipping = true")
    Address findDefaultByCustomer(Integer customerId);
}
