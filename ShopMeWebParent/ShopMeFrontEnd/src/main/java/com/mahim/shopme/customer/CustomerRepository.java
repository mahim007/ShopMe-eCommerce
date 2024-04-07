package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Customer findByEmail(String email);
    Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c SET c.enabled = true WHERE c.id = ?1")
    @Modifying
    void enable(Integer id);
}
