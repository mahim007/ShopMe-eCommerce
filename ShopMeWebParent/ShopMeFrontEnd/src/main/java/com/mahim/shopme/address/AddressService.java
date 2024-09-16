package com.mahim.shopme.address;

import com.mahim.shopme.common.dto.CustomerAddressDTO;
import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.AddressNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public Address findById(Integer id) throws AddressNotFoundException {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if (optionalAddress.isPresent()) {
            return optionalAddress.get();
        } else {
            throw new AddressNotFoundException("Address with id: " + id + " not found.");
        }
    }

    public Address findByIdAndCustomer(Integer id, Customer customer) throws AddressNotFoundException {
        Optional<Address> byIdAndCustomer = addressRepository.findByIdAndCustomer(id, customer);
        if (byIdAndCustomer.isPresent()) {
            return byIdAndCustomer.get();
        } else {
            throw new AddressNotFoundException("Address with id: " + id + " not found.");
        }
    }

    public Address save(Address addressInForm) throws AddressNotFoundException {
        boolean isUpdating = addressInForm.getId() != null;
        Address addressToBeSaved;

        if (isUpdating) {
            addressToBeSaved = findById(addressInForm.getId());
            addressToBeSaved.setFirstName(addressInForm.getFirstName());
            addressToBeSaved.setLastName(addressInForm.getLastName());
            addressToBeSaved.setPhoneNumber(addressInForm.getPhoneNumber());
            addressToBeSaved.setAddressLine1(addressInForm.getAddressLine1());
            addressToBeSaved.setAddressLine2(addressInForm.getAddressLine2());
            addressToBeSaved.setCity(addressInForm.getCity());
            addressToBeSaved.setState(addressInForm.getState());
            addressToBeSaved.setPostalCode(addressInForm.getPostalCode());
            addressToBeSaved.setCountry(addressInForm.getCountry());
            addressToBeSaved.setDefaultForShipping(addressInForm.isDefaultForShipping());
        } else {
            addressToBeSaved = addressInForm;
        }

        return addressRepository.save(addressToBeSaved);
    }

    public void deleteByIdAndCustomer(Integer id, Customer customer) throws AddressNotFoundException {
        Address address = findByIdAndCustomer(id, customer);
        addressRepository.deleteByIdAndCustomer(address.getId(), customer);
    }

    public Address getAddress(Integer id) throws AddressNotFoundException {
        return findById(id);
    }

    public Address getDefaultShippingAddress(Customer customer) {
        return addressRepository.findDefaultByCustomer(customer.getId());
    }

    public void setDefaultShippingAddress(Integer id, Customer customer) throws AddressNotFoundException {
        addressRepository.setNonDefaultShippingAddress(id, customer.getId());
        Optional<Address> optionalAddress = addressRepository.findByIdAndCustomer(id, customer);
        if (optionalAddress.isPresent()) {
            Address address = optionalAddress.get();
            addressRepository.setDefaultShippingAddress(address.getId(), true);
        }
    }
}
