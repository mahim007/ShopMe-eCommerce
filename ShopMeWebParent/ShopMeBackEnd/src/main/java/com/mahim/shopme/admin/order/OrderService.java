package com.mahim.shopme.admin.order;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.common.entity.Order;
import com.mahim.shopme.common.exception.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    public static  final int ORDERS_PER_PAGE = 10;

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> listAll() {
        return (List<Order>) orderRepository.findAll();
    }

    public Page<Order> listByKeyword(int pageNo, PagingAndSortingHelper helper) {
        return (Page<Order>) helper.listEntities(pageNo, ORDERS_PER_PAGE, orderRepository);
    }

    public Order findById(Integer id) throws OrderNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            return optionalOrder.get();
        } else
            throw new OrderNotFoundException("Order with id: " + id + " not found.");
    }

    public void delete(Integer id) throws OrderNotFoundException {
        Order order = findById(id);
        orderRepository.delete(order);
    }
}
