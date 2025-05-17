package com.mahim.shopme.order;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.common.exception.OrderNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/orders")
public class OrderRestController {
    private final OrderService orderService;
    private final CustomerService customerService;

    public OrderRestController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @PostMapping("/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                      HttpServletRequest request) {

        System.out.println("/orders/return, returnRequest: " + returnRequest);

        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            orderService.setOrderReturnRequested(returnRequest, customer);
            return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>("Authentication Required!", HttpStatus.BAD_REQUEST);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>("Order not found!", HttpStatus.NOT_FOUND);
        }
    }
}
