package com.mahim.shopme.shipping;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.ShippingRate;
import org.springframework.data.repository.CrudRepository;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {

    ShippingRate findByCountryAndState(Country country, String state);
}
