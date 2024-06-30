package com.mahim.shopme.shoppingcart;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import com.mahim.shopme.utils.EmailUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class ShoppingCartRestController {

    private final ShoppingCartService shoppingCartService;
    private final CustomerService customerService;

    public ShoppingCartRestController(ShoppingCartService shoppingCartService, CustomerService customerService) {
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
    }

    @PostMapping("/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity,
                                   HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer added = shoppingCartService.addProductToCart(productId, quantity, customer);
            return added + " item" + (added <= 1 ? "" : "s") + " added to the cart.";
        } catch (CustomerNotFoundException e) {
            return e.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = EmailUtils.getEmailFromAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("Customer not logged in.");
        }

        Optional<Customer> customerByEmail = customerService.getCustomerByEmail(email);
        if (customerByEmail.isPresent()) {
            return customerByEmail.get();
        } else {
            throw new CustomerNotFoundException("Customer not found for email: " + email);
        }
    }
}
