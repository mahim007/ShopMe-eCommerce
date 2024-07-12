package com.mahim.shopme.shoppingcart;

import com.mahim.shopme.common.entity.CartItem;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {

    private final CartItemRepository cartItemRepository;

    public ShoppingCartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public Integer addProductToCart(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Product product = new Product(productId);
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            if ((cartItem.getQuantity() + quantity) > 5) {
                throw new ShoppingCartException("Could not add " + quantity + " items, " +
                        "because there's already " + cartItem.getQuantity() + " items in your cart. " +
                        "Maximum allowed quantity is 5.");
            }
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCustomer(customer);
        }

        cartItemRepository.save(cartItem);
        return cartItem.getQuantity();
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }
}
