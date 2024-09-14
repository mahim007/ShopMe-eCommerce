package com.mahim.shopme.address;

import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/address_book")
public class AddressController {

    private final AddressService addressService;
    private final CustomerService customerService;

    public AddressController(AddressService addressService, CustomerService customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }

    @GetMapping("")
    public String showAddressBook(Model model, HttpServletRequest request, RedirectAttributes ra) {
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
            model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
        }
        return "address_book/addresses";
    }

}
