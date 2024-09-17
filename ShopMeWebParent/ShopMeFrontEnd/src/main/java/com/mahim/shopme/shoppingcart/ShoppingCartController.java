package com.mahim.shopme.shoppingcart;

import com.mahim.shopme.address.AddressService;
import com.mahim.shopme.common.entity.Address;
import com.mahim.shopme.common.entity.CartItem;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.ShippingRate;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import com.mahim.shopme.shipping.ShippingRateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, CustomerService customerService,
                                  AddressService addressService, ShippingRateService shippingRateService) {
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
    }

    @GetMapping("")
    public String viewCart(HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            List<CartItem> cartItems = shoppingCartService.listCartItems(customer);

            double total = 0;
            for (CartItem cartItem : cartItems) {
                total += cartItem.getQuantity() * cartItem.getProduct().getDiscountPrice();
            }

            Address defaultShippingAddress = addressService.getDefaultShippingAddress(customer);
            boolean usePrimaryAddressAsDefault = false;
            ShippingRate shippingRate;

            if (defaultShippingAddress != null) {
                shippingRate = shippingRateService.getShippingRateForAddress(defaultShippingAddress);
            } else {
                usePrimaryAddressAsDefault = true;
                shippingRate = shippingRateService.getShippingRateForCustomer(customer);
            }


            model.addAttribute("cartItems", cartItems);
            model.addAttribute("estimatedTotal", total);
            model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
            model.addAttribute("shippingSupported", shippingRate != null);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
            model.addAttribute("cartItems", Collections.EMPTY_LIST);
        }
        return "cart/shopping_cart";
    }
}
