package com.mahim.shopme.admin.customer;

import com.mahim.shopme.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

    Long countById(Integer id);

    Optional<Customer> findByEmail(String email);

    Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c SET c.enabled = true WHERE c.id = ?1")
    @Modifying
    void enable(Integer id);

    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    @Query("SELECT c FROM Customer c WHERE concat(c.firstName, ' ', c.lastName, ' ', c.email, ' ', c.addressLine1, ' ', " +
            "c.addressLine2, ' ', c.city, ' ', c.state, ' ', c.country.name, ' ', c.postalCode) like %?1%")
    Page<Customer> findAllByKeyword(String keyword, Pageable pageable);
}
