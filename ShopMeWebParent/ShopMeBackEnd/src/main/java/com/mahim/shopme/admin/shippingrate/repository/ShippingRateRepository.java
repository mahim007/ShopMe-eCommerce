package com.mahim.shopme.admin.shippingrate.repository;

import com.mahim.shopme.admin.paging.SearchRepository;
import com.mahim.shopme.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer> {

    @Query("SELECT s FROM ShippingRate s WHERE "
            + " concat(s.country.name , ' ', s.state) LIKE %?1%"
    )
    Page<ShippingRate> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT s FROM ShippingRate s WHERE s.country.code = ?1 AND s.state = ?2")
    Optional<ShippingRate> findByCountryCodeAndState(String countryCode, String state);

    @Modifying
    @Query("UPDATE ShippingRate s SET s.codSupported = ?2 WHERE s.id = ?1")
    void updateCODSupport(Integer id, boolean enabled);

    @Query("SELECT s FROM ShippingRate s WHERE s.country.id = :countryId AND s.state = :state")
    Optional<ShippingRate> findByCountryIdAndState(Integer countryId, String state);
}
