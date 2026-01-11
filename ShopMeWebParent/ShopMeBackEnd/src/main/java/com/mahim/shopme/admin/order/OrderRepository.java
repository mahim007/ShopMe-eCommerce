package com.mahim.shopme.admin.order;

import com.mahim.shopme.admin.paging.SearchRepository;
import com.mahim.shopme.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends SearchRepository<Order, Integer> {

    @Query("SELECT o FROM Order o where concat('#', o.id) like %:keyword% OR" +
            " concat(o.firstName, ' ', o.lastName, ' ', " +
            "o.phoneNumber, ' ', o.addressLine1, ' ', o.addressLine2, ' ', o.postalCode," +
            "o.country, ' ', o.state, ' ', o.city, ' ', o.paymentMethod, ' ', o.status) like %:keyword%")
    Page<Order> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT NEW com.mahim.shopme.common.entity.Order(o.id, o.orderTime, o.productCost, o.subtotal, o.total) " +
            "FROM Order o " +
            "WHERE o.orderTime BETWEEN :startTime AND :endTime " +
            "ORDER BY o.orderTime ASC")
    List<Order> findByOrderTimeBetween(Date startTime, Date endTime);
}
