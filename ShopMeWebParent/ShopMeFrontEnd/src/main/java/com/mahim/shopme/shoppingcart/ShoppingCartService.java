package com.mahim.shopme.shoppingcart;

import com.mahim.shopme.common.entity.CartItem;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.Product;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {

    private final CartItemRepository cartItemRepository;

    public ShoppingCartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public Integer addProductToCart(Integer productId, Integer quantity, Customer customer) {
        Product product = new Product(productId);
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCustomer(customer);
        }

        cartItemRepository.save(cartItem);
        return quantity;
    }
}
