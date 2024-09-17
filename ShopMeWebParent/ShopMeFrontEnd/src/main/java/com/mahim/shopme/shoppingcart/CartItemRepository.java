package com.mahim.shopme.shoppingcart;

import com.mahim.shopme.common.entity.CartItem;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartItemRepository extends CrudRepository<CartItem, Integer> {

    List<CartItem> findByCustomer(Customer customer);

    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Modifying
    @Query("UPDATE CartItem c set c.quantity = ?1 WHERE c.customer.id = ?2 AND c.product.id = ?3")
    void updateQuantity(Integer quantity, Integer customerId, Integer productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
    void deleteByCustomerAndProduct(Integer customerId, Integer productId);

    @Modifying
    void deleteByCustomer(Customer customer);
}
