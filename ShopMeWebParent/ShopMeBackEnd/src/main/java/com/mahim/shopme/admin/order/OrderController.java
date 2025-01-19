package com.mahim.shopme.admin.order;

import com.mahim.shopme.admin.customer.CustomerService;
import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.admin.paging.PagingAndSortingParam;
import com.mahim.shopme.admin.security.ShopmeUserDetails;
import com.mahim.shopme.admin.setting.SettingService;
import com.mahim.shopme.common.entity.*;
import com.mahim.shopme.common.enums.OrderStatus;
import com.mahim.shopme.common.exception.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final SettingService settingService;
    private final CustomerService customerService;

    public OrderController(OrderService orderService, SettingService settingService, CustomerService customerService) {
        this.orderService = orderService;
        this.settingService = settingService;
        this.customerService = customerService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(
            @PagingAndSortingParam(listName = "orders", moduleURL = "/orders") PagingAndSortingHelper helper,
            @PathVariable("pageNo") int pageNo,
            HttpServletRequest request,
            @AuthenticationPrincipal ShopmeUserDetails loggedInUser) {

        Page<Order> orders = orderService.listByKeyword(pageNo, helper);
        helper.updateModelAttributes(pageNo, orders);
        loadCurrencySetting(request);
        return "orders/orders_shipper";
//        if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Salesperson") && loggedInUser.hasRole("Shipper")) {
//            return "orders/orders_shipper";
//        }
//        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/details/{id}")
    public String get(@PathVariable("id") Integer id,HttpServletRequest request, Model model, RedirectAttributes ra,
                      @AuthenticationPrincipal ShopmeUserDetails loggedInUser) {
        try {
            Order order = orderService.findById(id);
            loadCurrencySetting(request);
            boolean showLessInfo = !loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Salesperson") && loggedInUser.hasRole("Shipper");

            model.addAttribute("order", order);
            model.addAttribute("showLessInfo", showLessInfo);
            return "orders/order_details_modal";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Order not found with ID: " + id);
        }

        return listAll();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "Order ID:" + id + " deleted successfully");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Order ID: " + id + " not found");
        }

        return listAll();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            Order order = orderService.findById(id);
            model.addAttribute("order", order);
            model.addAttribute("countries", customerService.listAllCountry());
            model.addAttribute("pageTitle", "Edit Order");
            loadCurrencySetting(request);
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("exceptionMessage", "Order not found with ID: " + id);
            return listAll();
        }

        return "orders/order_form";
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute("order") Order order, HttpServletRequest request, RedirectAttributes ra) {
        try {
            order.setCountry(request.getParameter("countryName"));
            updateProductDetails(order, request);
            updateOrderTracks(order, request);
            orderService.save(order);
            ra.addFlashAttribute("message", "Order saved successfully");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("message", "Failed to update order.");
        }

        return listAll();
    }

    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        if (trackIds == null) trackIds = new String[0];
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        for (int i = 0; i < trackIds.length; i++) {
            int trackId = Integer.parseInt(trackIds[i]);
            OrderTrack orderTrack = new OrderTrack();

            if (trackId > 0) orderTrack.setId(trackId);
            orderTrack.setUpdatedTimeOnForm(trackDates[i]);
            orderTrack.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            orderTrack.setNotes(trackNotes[i]);
            orderTrack.setOrder(order);

            orderTracks.add(orderTrack);
        }

    }

    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        doSomething(detailIds);
        doSomething(productIds);
        doSomething(productDetailCosts);
        doSomething(productSubtotals);
        doSomething(productShipCosts);

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (int i = 0; i < detailIds.length; i++) {
            OrderDetail orderDetail = new OrderDetail();
            int detailId = Integer.parseInt(detailIds[i]);
            if (detailId > 0 ) {
                orderDetail.setId(detailId);
            }

            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));

            orderDetails.add(orderDetail);
        }


    }

    private void doSomething(String[] items) {
        for (String item : items) {
            System.out.println(item);
        }

    }
}
