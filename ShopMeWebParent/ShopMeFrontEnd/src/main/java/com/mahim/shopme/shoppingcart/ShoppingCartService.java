package com.mahim.shopme.shoppingcart;

import com.mahim.shopme.common.entity.CartItem;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.Product;
import com.mahim.shopme.product.ProductRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public ShoppingCartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
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

    @Transactional
    public float updateQuantity(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        Optional<Product> productOptional = productRepository.findById(productId);
        return productOptional.map(product -> product.getDiscountPrice() * quantity)
                .orElseThrow(() -> new ShoppingCartException("Product with id " + productId + " not found"));
    }

    @Transactional
    public void removeProduct(Integer productId, Customer customer) throws ShoppingCartException {
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    @Transactional
    public void deleteByCustomer(Customer customer) {
        cartItemRepository.deleteByCustomer(customer);
    }
}
