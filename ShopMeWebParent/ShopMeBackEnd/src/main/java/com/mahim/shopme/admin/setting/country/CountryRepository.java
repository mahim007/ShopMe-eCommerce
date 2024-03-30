package com.mahim.shopme.admin.setting.country;

import com.mahim.shopme.common.entity.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends CrudRepository<Country, Integer> {

    List<Country> findAllByOrderByNameAsc();
}
