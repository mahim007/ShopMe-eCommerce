package com.mahim.shopme.setting;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends CrudRepository<State, Integer> {

    List<State> findAllByCountryOrderByNameAsc(Country country);
}
