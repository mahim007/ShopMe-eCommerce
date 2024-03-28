package com.mahim.shopme.admin.currency;

import com.mahim.shopme.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
}
