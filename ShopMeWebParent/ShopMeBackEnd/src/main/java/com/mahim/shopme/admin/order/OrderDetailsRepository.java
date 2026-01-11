package com.mahim.shopme.admin.order;

import com.mahim.shopme.common.entity.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OrderDetailsRepository extends CrudRepository<OrderDetail, Integer> {

    @Query("SELECT NEW com.mahim.shopme.common.entity.OrderDetail(" +
            "od.product.category.name, od.quantity, od.productCost, od.shippingCost, od.subtotal) " +
            "FROM OrderDetail od " +
            "WHERE od.order.orderTime BETWEEN :startTime AND :endTime")
    public List<OrderDetail> findByCategoryAndTimeBetween(Date startTime, Date endTime);

    @Query("SELECT NEW com.mahim.shopme.common.entity.OrderDetail(" +
            "od.quantity, od.product.name, od.productCost, od.shippingCost, od.subtotal) " +
            "FROM OrderDetail od " +
            "WHERE od.order.orderTime BETWEEN :startTime AND :endTime")
    public List<OrderDetail> findByProductAndTimeBetween(Date startTime, Date endTime);
}
