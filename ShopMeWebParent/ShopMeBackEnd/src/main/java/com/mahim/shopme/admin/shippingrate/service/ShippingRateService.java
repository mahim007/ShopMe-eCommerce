package com.mahim.shopme.admin.shippingrate.service;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.setting.country.CountryRepository;
import com.mahim.shopme.admin.shippingrate.repository.ShippingRateRepository;
import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.ShippingRate;
import com.mahim.shopme.common.exception.ShippingRateNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingRateService {
    public static final int SHIPPING_RATE_PER_PAGE = 10;

    private final ShippingRateRepository shippingRateRepository;
    private final CountryRepository countryRepository;

    public ShippingRateService(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository) {
        this.shippingRateRepository = shippingRateRepository;
        this.countryRepository = countryRepository;
    }

    public List<ShippingRate> listAll() {
        return (List<ShippingRate>) shippingRateRepository.findAll();
    }

    public Page<ShippingRate> listByKeyword(int pageNum, PagingAndSortingHelper helper) {
        return (Page<ShippingRate>) helper.listEntities(pageNum, SHIPPING_RATE_PER_PAGE, shippingRateRepository);
    }

    public ShippingRate save(ShippingRate shippingRateInForm) {
        boolean isUpdating = shippingRateInForm.getId() != null;
        ShippingRate shippingRateToBeSaved;

        if (isUpdating) {
            shippingRateToBeSaved = shippingRateRepository.findById(shippingRateInForm.getId()).get();
            shippingRateToBeSaved.setRate(shippingRateInForm.getRate());
            shippingRateToBeSaved.setDays(shippingRateInForm.getDays());
            shippingRateToBeSaved.setCodSupported(shippingRateInForm.isCodSupported());
            shippingRateToBeSaved.setCountry(shippingRateInForm.getCountry());
            shippingRateToBeSaved.setState(shippingRateInForm.getState());
        } else {
            shippingRateToBeSaved = shippingRateInForm;
        }

        return shippingRateRepository.save(shippingRateToBeSaved);
    }

    public ShippingRate findById(Integer id) throws ShippingRateNotFoundException {
        Optional<ShippingRate> optionalShippingRate = shippingRateRepository.findById(id);
        if (optionalShippingRate.isPresent()) {
            return optionalShippingRate.get();
        } else {
            throw new ShippingRateNotFoundException("Shipping Rate with id: " + id + " not found!");
        }
    }

    public void delete(Integer id) throws ShippingRateNotFoundException {
        ShippingRate toBeDeleted = findById(id);
        shippingRateRepository.delete(toBeDeleted);
    }

    public String checkUnique(Integer id, String country, String state) throws ShippingRateNotFoundException {
        Optional<ShippingRate> optionalById = shippingRateRepository.findById(id);
        Optional<ShippingRate> optionalByCountryAndState = shippingRateRepository.findByCountryAndState(country, state);

        if (optionalById.isPresent() && optionalByCountryAndState.isPresent()) {
            ShippingRate byId = optionalById.get();
            ShippingRate byCountryAndState = optionalByCountryAndState.get();

            if (byId.getId() == byCountryAndState.getId()) {
                return "OK";
            } else {
                return "Duplicate: " + country + "/" + state;
            }

        } else if (optionalById.isPresent()){
            return "OK";
        } else if (optionalByCountryAndState.isPresent()) {
            return "Duplicate: " + country + "/" + state;
        } else {
            return "OK";
        }
    }

    public void updateCODSupported(Integer id, boolean enabled) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = findById(id);
        shippingRateRepository.updateCODSupport(shippingRate.getId(), !enabled);
    }

    public List<Country> listAllCountry() {
        return countryRepository.findAllByOrderByNameAsc();
    }
}
