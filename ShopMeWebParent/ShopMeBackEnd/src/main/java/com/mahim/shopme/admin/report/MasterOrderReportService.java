package com.mahim.shopme.admin.report;

import com.mahim.shopme.admin.order.OrderRepository;
import com.mahim.shopme.common.entity.Order;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MasterOrderReportService {
    private final OrderRepository orderRepository;

    public MasterOrderReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<ReportItem> getReportDataLast7Days() {
        return getReportDataLastXDays(7);
    }

    private List<ReportItem> getReportDataLastXDays(int days) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        Date startTime = calendar.getTime();
        return getReportDataByDateRange(startTime, endTime);
    }

    private List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime) {
        List<Order> orders = orderRepository.findByOrderTimeBetween(startTime, endTime);
        printRawData(orders);
        return null;
    }

    private void printRawData(List<Order> orders) {
        orders.forEach(order -> System.out.printf("%-3d | %s | %10.2f | %10.2f\n",
                order.getId(), order.getOrderTime(), order.getProductCost(), order.getSubtotal()));
    }
}
