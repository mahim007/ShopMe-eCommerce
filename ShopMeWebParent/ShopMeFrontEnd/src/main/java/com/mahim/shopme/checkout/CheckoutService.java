package com.mahim.shopme.checkout;

import com.mahim.shopme.common.entity.CartItem;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.common.entity.ShippingRate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    private static final int DIM_DIVISOR = 139;

    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();
        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);

        return checkoutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = Math.max(product.getWeight(), dimWeight);
            float shippingCost = finalWeight * cartItem.getQuantity() * shippingRate.getRate();
            cartItem.setShippingCost(shippingCost);
            shippingCostTotal += shippingCost;
        }

        return shippingCostTotal;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0f;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getSubTotal();
        }

        return total;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0f;
        for (CartItem cartItem : cartItems) {
            cost += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        return cost;
    }
}
