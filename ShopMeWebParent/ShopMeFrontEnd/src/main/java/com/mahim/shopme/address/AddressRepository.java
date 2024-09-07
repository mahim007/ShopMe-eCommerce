package com.mahim.shopme.address;

import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Long> {

    List<Address> findByCustomer(Customer customer);

//    @Query("SELECT a FROM Address a where a.id = ?1 AND a.customer.id = :customer.id")
    Address findByIdAndCustomer(Integer id, Customer customer);

    @Modifying
//    @Query("DELETE FROM Address a WHERE a.id = :id AND a.customer = :customer")
    void deleteByIdAndCustomer(Integer id, Customer customer);
}
