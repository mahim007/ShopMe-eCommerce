package com.mahim.shopme.address;

import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.AddressNotFoundException;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/address_book")
public class AddressController {

    private final AddressService addressService;
    private final CustomerService customerService;
    public final String moduleURL = "/address_book";

    public AddressController(AddressService addressService, CustomerService customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }

    @GetMapping("")
    public String listAllAddresses(Model model, HttpServletRequest request, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            List<Address> addresses = addressService.listAddressBook(customer);

            boolean usePrimaryAddressAsDefault = true;
            for (Address address : addresses) {
                if (address.isDefaultForShipping()) {
                    usePrimaryAddressAsDefault = false;
                    break;
                }
            }

            model.addAttribute("addresses", addresses);
            model.addAttribute("customer", customer);
            model.addAttribute("moduleURL", moduleURL);
            model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
        }
        return "address_book/addresses";
    }

    @GetMapping("/new")
    public String newAddress(Model model) {
        List<Country> countries = customerService.listAllCountries();
        Address address = new Address();
        address.setDefaultForShipping(false);

        model.addAttribute("address", address);
        model.addAttribute("countries", countries);
        model.addAttribute("moduleURL", moduleURL);
        model.addAttribute("pageTitle", "New Address");
        return "address_book/address_form";
    }

    @PostMapping("/create_address")
    public String createAddress(@ModelAttribute("address") Address address, HttpServletRequest request, RedirectAttributes ra) {

        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            address.setCustomer(customer);
            addressService.save(address);
            ra.addFlashAttribute("message", "Address saved successfully");
        } catch (AddressNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Address could not be updated");
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
        }

        return "redirect:/address_book";
    }

    @GetMapping("/edit/{id}")
    public String editAddress(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Address address = addressService.getAddress(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("address", address);
            model.addAttribute("countries", countries);
            model.addAttribute("moduleURL", moduleURL);
            model.addAttribute("pageTitle", "Update Address");
            return "address_book/address_form";
        } catch (AddressNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Address not found with id " + id);
            return "address_book/addresses";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer id, RedirectAttributes ra, Model model) {
        try {
            addressService.delete(id);
            model.addAttribute("moduleURL", moduleURL);
            ra.addFlashAttribute("message", "Address deleted successfully");
        } catch (AddressNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Address could not be deleted");
        }

        return "redirect:/address_book";
    }

    @GetMapping("/update_default/{id}")
    public String updateDefaultAddress(@PathVariable(name = "id") String id,HttpServletRequest request,  RedirectAttributes ra, Model model) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            addressService.setDefaultShippingAddress(id.equals("primary") ? null : Integer.valueOf(id), customer);
            model.addAttribute("moduleURL", moduleURL);
            ra.addFlashAttribute("message", "Address with id: " + id + " set to default.");
        } catch (AddressNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Address not found with id " + id);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
        }

        return "redirect:/address_book";
    }

}
