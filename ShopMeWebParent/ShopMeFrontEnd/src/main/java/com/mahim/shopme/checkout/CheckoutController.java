package com.mahim.shopme.checkout;

import com.mahim.shopme.address.AddressService;
import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.CartItem;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.ShippingRate;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import com.mahim.shopme.shipping.ShippingRateService;
import com.mahim.shopme.shoppingcart.ShoppingCartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final  CheckoutService checkoutService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final ShoppingCartService shoppingCartService;

    public CheckoutController(CheckoutService checkoutService, CustomerService customerService, AddressService addressService,
                              ShippingRateService shippingRateService, ShoppingCartService shoppingCartService) {
        this.checkoutService = checkoutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("")
    public String showCheckoutPage(HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Address address = addressService.getDefaultShippingAddress(customer);

            ShippingRate shippingRate;

            if (address != null) {
                model.addAttribute("shippingAddress", address.toString());
                shippingRate = shippingRateService.getShippingRateForAddress(address);
            } else {
                model.addAttribute("shippingAddress", customer.getAddress());
                shippingRate = shippingRateService.getShippingRateForCustomer(customer);
            }

            if (shippingRate == null) {
                return "redirect:/cart";
            }

            List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
            CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

            model.addAttribute("checkoutInfo", checkoutInfo);
            model.addAttribute("cartItems", cartItems);

        } catch (CustomerNotFoundException e) {
            throw new RuntimeException(e);
        }

        return "checkout/checkout";
    }
}
