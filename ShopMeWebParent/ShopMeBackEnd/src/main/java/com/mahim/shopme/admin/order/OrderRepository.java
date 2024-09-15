package com.mahim.shopme.admin.order;

import com.mahim.shopme.common.entity.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {
}
