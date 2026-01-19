package com.mahim.shopme.order;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.entity.Order;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.common.exception.OrderNotFoundException;
import com.mahim.shopme.customer.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.mahim.shopme.product.ProductService.PRODUCTS_PER_PAGE;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;

    public OrderController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @GetMapping("")
    public String listFirstPage(Model model, HttpServletRequest request) {
        return listOrderByPage(model, request, 1, "orderTime", "desc", null);
    }

    @GetMapping("/page/{pageNum}")
    public String listOrderByPage(Model model, HttpServletRequest request, @PathVariable(name = "pageNum") int pageNum,
                                  @RequestParam(name = "sortField", defaultValue = "orderTime") String sortField,
                                  @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
                                  @RequestParam(name = "orderKeyword", defaultValue = "") String orderKeyword) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Page<Order> page = orderService.listForCustomersByPage(customer, pageNum, sortField, sortDir, orderKeyword);
            long totalItems = page.getTotalElements();
            List<Order> orders = page.getContent();
            int startNo = ((pageNum - 1) * PRODUCTS_PER_PAGE) + 1;
            int endNo = pageNum * PRODUCTS_PER_PAGE;

            model.addAttribute("pageTitle", "My Orders");
            model.addAttribute("totalPageNo", page.getTotalPages());
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("currentPageNo", pageNum);
            model.addAttribute("orders", orders);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("orderKeyword", orderKeyword);
            model.addAttribute("moduleURL", "/orders");
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
            model.addAttribute("startNo", startNo);
            model.addAttribute("endNo", endNo < totalItems ? endNo : totalItems);

            return "order/orders_customer";

        } catch (CustomerNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/details/{orderId}")
    public String getDetails(
            @PathVariable Integer orderId, HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Order order = orderService.getOrder(orderId, customer);
            model.addAttribute("order", order);
            model.addAttribute("showLessInfo", false);
            return "order/order_details_modal";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("exceptionMessage", "Customer is not logged in");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Order with ID: " + orderId + " not found.");
        }

        return listFirstPage(model, request);
    }
}
