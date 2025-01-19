package com.mahim.shopme.admin.order;

import com.mahim.shopme.common.exception.OrderNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/update/{id}/{status}")
    public ResponseEntity<Response> updateOrder(@PathVariable("id") Integer orderId, @PathVariable String status) {
        try {
            System.out.println(orderId + " " + status);
            orderService.updateStatus(orderId, status);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(new Response(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Response(orderId, status), HttpStatus.OK);
    }
}

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
class Response {
    private Integer orderId;
    private String status;
}