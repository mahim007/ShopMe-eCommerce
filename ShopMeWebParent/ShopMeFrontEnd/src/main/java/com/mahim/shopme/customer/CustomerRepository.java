package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.enums.AuthenticationType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Customer findByEmail(String email);
    Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null  WHERE c.id = ?1")
    @Modifying
    void enable(Integer id);

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    void updateAuthenticationType(Integer id, AuthenticationType type);

    Customer findByResetPasswordToken(String token);
}
