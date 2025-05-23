package com.mahim.shopme.order;

import com.mahim.shopme.checkout.CheckoutInfo;
import com.mahim.shopme.common.entity.*;
import com.mahim.shopme.common.enums.OrderStatus;
import com.mahim.shopme.common.enums.PaymentMethod;
import com.mahim.shopme.common.exception.OrderNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {

    public static final int ORDERS_PER_PAGE = 10;

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order order = getOrder(customer, paymentMethod, checkoutInfo);

        if (address == null) {
            order.copyAddressFromCustomer();
        } else {
            order.copyAddressFrom(address);
        }

        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = getOrderDetail(cartItem, order);
            orderDetails.add(orderDetail);
        }

        // adding order track for new order. status is OrderStatus.NEW when it is newly created
        order.getOrderTracks().add(new OrderTrack(OrderStatus.NEW.defaultDescription(), new Date(), OrderStatus.NEW, order));
        return orderRepository.save(order);

    }

    private static Order getOrder(Customer customer, PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order order = new Order();
        order.setOrderTime(new Date());
        if (PaymentMethod.PAYPAL.equals(paymentMethod)) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.NEW);
        }

        order.setCustomer(customer);
        order.setProductCost(checkoutInfo.getProductCost());
        order.setSubtotal(checkoutInfo.getProductTotal());
        order.setShippingCost(checkoutInfo.getShippingCostTotal());
        order.setTax(0.0f);
        order.setTotal(checkoutInfo.getPaymentTotal());
        order.setPaymentMethod(paymentMethod);
        order.setDeliverDays(checkoutInfo.getDeliverDays());
        order.setDeliverDate(checkoutInfo.getDeliverDate());
        return order;
    }

    private static OrderDetail getOrderDetail(CartItem cartItem, Order order) {
        Product product = cartItem.getProduct();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(cartItem.getQuantity());
        orderDetail.setUnitPrice(product.getDiscountPrice());
        orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
        orderDetail.setSubtotal(cartItem.getSubTotal());
        orderDetail.setShippingCost(cartItem.getShippingCost());
        return orderDetail;
    }

    public Page<Order> listForCustomersByPage(Customer customer, int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageRequest = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (StringUtils.isNotEmpty(keyword) && StringUtils.isNotBlank(keyword) && keyword.length() > 2) {
            return orderRepository.searchAllOrders(keyword, customer.getId(), pageRequest);
        }

        return orderRepository.searchAllOrders(customer.getId(), pageRequest);
    }

    public Order getOrder(Integer orderId, Customer customer) throws OrderNotFoundException {
        Optional<Order> orderOptional = orderRepository.findByIdAndCustomer(orderId, customer);
        orderOptional.orElseThrow(() -> new OrderNotFoundException("No Order found with id: " + orderId));
        return orderOptional.get();
    }

    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
        Optional<Order> orderOptional = orderRepository.findByIdAndCustomer(request.getOrderId(), customer);
        orderOptional.orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + request.getOrderId()));
        Order order = orderOptional.get();

        if (order.getStatus() == OrderStatus.RETURN_REQUESTED) return;

        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setOrder(order);
        orderTrack.setUpdatedTime(new Date());
        orderTrack.setStatus(OrderStatus.RETURN_REQUESTED);

        String notes = "Reason: " + request.getReason().substring(0, Math.min(request.getReason().length(), 256));
        if (!request.getNote().isEmpty() && !request.getNote().isBlank()) {
            notes += ". " + request.getNote();
        }

        orderTrack.setNotes(notes);
        order.getOrderTracks().add(orderTrack);
        order.setStatus(OrderStatus.RETURN_REQUESTED);
        orderRepository.save(order);
    }
}
