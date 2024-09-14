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

//    @Query("SELECT a FROM Address a where a.id = ?1 AND a.customer.id = :customer.id")
    Optional<Address> findByIdAndCustomer(Integer id, Customer customer);

    @Modifying
//    @Query("DELETE FROM Address a WHERE a.id = :id AND a.customer = :customer")
    void deleteByIdAndCustomer(Integer id, Customer customer);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = :defaultForShipping WHERE a.id = :id")
    void setDefaultShippingAddress(Integer id, boolean defaultForShipping);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id != :id AND a.customer.id = :customerId")
    void setNonDefaultShippingAddress(Integer id, Integer customerId);
}
