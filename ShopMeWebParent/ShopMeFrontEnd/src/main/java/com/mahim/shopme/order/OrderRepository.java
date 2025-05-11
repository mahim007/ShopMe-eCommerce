package com.mahim.shopme.order;

import com.mahim.shopme.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {

    @Query("select o from Order o " +
            "where o.customer.id = ?1")
    Page<Order> searchAllOrders(Integer customerId, Pageable pageable);

    @Query("select distinct o from Order o " +
            "join o.orderDetails od " +
            "join od.product p " +
            "where o.customer.id = ?2 " +
            "and (p.name like %?1% or o.status like %?1%)")
    Page<Order> searchAllOrders(String keyword, Integer customerId, Pageable pageable);
}
