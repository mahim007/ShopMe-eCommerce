package com.mahim.shopme.shoppingcart;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Integer added = shoppingCartService.addProductToCart(productId, quantity, customer);
            return added + " item" + (added <= 1 ? "" : "s") + " fo this product added to the cart.";
        } catch (CustomerNotFoundException | ShoppingCartException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/update/{productId}/{quantity}")
        public String updateQuantity(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity,
                                   HttpServletRequest request) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            float subTotal = shoppingCartService.updateQuantity(productId, quantity, customer);
            return String.valueOf(subTotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of product.";
        } catch (ShoppingCartException | RuntimeException e) {
            return "Error while updating quantity of product.";
        }
    }

    @DeleteMapping("/remove/{productId}")
    public String removeProduct(@PathVariable(name = "productId") Integer productId, HttpServletRequest request) throws CustomerNotFoundException, ShoppingCartException {
        Customer customer = customerService.getAuthenticatedCustomer(request);
        shoppingCartService.removeProduct(productId, customer);
        return "Removed product from shopping cart.";
    }
}
