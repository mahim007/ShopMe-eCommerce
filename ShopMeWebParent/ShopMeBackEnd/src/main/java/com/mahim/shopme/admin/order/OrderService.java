package com.mahim.shopme.admin.order;

import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.common.entity.Order;
import com.mahim.shopme.common.entity.OrderTrack;
import com.mahim.shopme.common.enums.OrderStatus;
import com.mahim.shopme.common.exception.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    public void save(Order orderInForm) {
        Order orderInDb = orderRepository.findById(orderInForm.getId()).get();
        orderInForm.setOrderTime(orderInDb.getOrderTime());
        orderInForm.setCustomer(orderInDb.getCustomer());

        orderRepository.save(orderInForm);
    }

    public void updateStatus(Integer orderId, String status) throws OrderNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        OrderStatus orderStatus = OrderStatus.valueOf(status);

        System.out.println("order-id: " + orderId + " status: " + orderStatus);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (!order.hasStatus(orderStatus)) {
                List<OrderTrack> orderTracks = order.getOrderTracks();

                OrderTrack newTrack = new OrderTrack();
                newTrack.setStatus(orderStatus);
                newTrack.setUpdatedTime(new Date());
                newTrack.setNotes(orderStatus.defaultDescription());
                newTrack.setOrder(order);

                orderTracks.add(newTrack);

                order.setStatus(OrderStatus.valueOf(status));
                orderRepository.save(order);
            }
        } else {
            throw new OrderNotFoundException("Order with id: " + orderId + " not found.");
        }
    }
}
