package com.mahim.shopme.admin.currency;

import com.mahim.shopme.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

    List<Currency> findAllByOrderByNameAsc();
}
