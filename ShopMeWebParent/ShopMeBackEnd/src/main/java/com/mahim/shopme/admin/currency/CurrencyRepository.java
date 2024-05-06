package com.mahim.shopme.admin.currency;

import com.mahim.shopme.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

    List<Currency> findAllByOrderByNameAsc();
}
