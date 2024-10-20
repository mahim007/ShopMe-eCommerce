package com.mahim.shopme.admin.shippingrate.service;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.product.repository.ProductRepository;
import com.mahim.shopme.admin.setting.country.CountryRepository;
import com.mahim.shopme.admin.shippingrate.repository.ShippingRateRepository;
import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.entity.ShippingRate;
import com.mahim.shopme.common.exception.ProductNotFoundException;
import com.mahim.shopme.common.exception.ShippingRateNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingRateService {
    public static final int SHIPPING_RATE_PER_PAGE = 10;
    private static final int DIM_DIVISOR = 139;

    private final ShippingRateRepository shippingRateRepository;
    private final CountryRepository countryRepository;
    private final ProductRepository productRepository;

    public ShippingRateService(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository,
                               ProductRepository productRepository) {
        this.shippingRateRepository = shippingRateRepository;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
    }

    public List<ShippingRate> listAll() {
        return (List<ShippingRate>) shippingRateRepository.findAll();
    }

    public Page<ShippingRate> listByKeyword(int pageNum, PagingAndSortingHelper helper) {
        return (Page<ShippingRate>) helper.listEntities(pageNum, SHIPPING_RATE_PER_PAGE, shippingRateRepository);
    }

    public ShippingRate save(ShippingRate shippingRateInForm) throws ShippingRateNotFoundException {
        boolean isUpdating = shippingRateInForm.getId() != null;
        ShippingRate shippingRateToBeSaved;

        if (isUpdating) {
            shippingRateToBeSaved = findById(shippingRateInForm.getId());
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

    public String checkUnique(Integer id, String countryCode, String state) throws ShippingRateNotFoundException {
        Optional<ShippingRate> optionalById = shippingRateRepository.findById(id);
        Optional<ShippingRate> optionalByCountryAndState = shippingRateRepository.findByCountryCodeAndState(countryCode, state);

        if (optionalById.isPresent() && optionalByCountryAndState.isPresent()) {
            ShippingRate byId = optionalById.get();
            ShippingRate byCountryAndState = optionalByCountryAndState.get();

            if (byId.getId() == byCountryAndState.getId()) {
                return "OK";
            } else {
                return "Duplicate: " + countryCode + "/" + state;
            }

        } else if (optionalById.isPresent()){
            return "OK";
        } else if (optionalByCountryAndState.isPresent()) {
            return "Duplicate: " + countryCode + "/" + state;
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

    public float calculateShippingCost(Integer productId, Integer countryId, String state)
            throws ShippingRateNotFoundException, ProductNotFoundException {

        Optional<ShippingRate> shippingRateOptional = shippingRateRepository.findByCountryIdAndState(countryId, state);
        Optional<Product> productOptional = productRepository.findById(productId);
        float finalWeight = 0.0f;

        if (shippingRateOptional.isPresent() && productOptional.isPresent()) {
            ShippingRate shippingRate = shippingRateOptional.get();
            Product product = productOptional.get();

            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            finalWeight = Math.max(product.getWeight(), dimWeight);
            return finalWeight * shippingRate.getRate();
        } else if (shippingRateOptional.isEmpty()){
            throw new ShippingRateNotFoundException("No shipping rate found for given destination. " +
                    "You have to enter shipping cost manually");
        } else {
            throw new ProductNotFoundException("No shipping rate found for given destination. " +
                    "You have to enter shipping cost manually");
        }
    }
}
