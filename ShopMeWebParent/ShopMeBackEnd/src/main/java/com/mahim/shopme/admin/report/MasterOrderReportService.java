package com.mahim.shopme.admin.report;

import com.mahim.shopme.admin.order.OrderRepository;
import com.mahim.shopme.common.entity.Order;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MasterOrderReportService {
    private final OrderRepository orderRepository;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public MasterOrderReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<ReportItem> getReportDataLast7Days() {
        return getReportDataLastXDays(7);
    }

    public List<ReportItem> getReportDataLast28Days() {
        return getReportDataLastXDays(28);
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
        List<ReportItem> reportItems = createReportData(startTime, endTime);
        calculateSalesForReportData(orders, reportItems);
        return reportItems;
    }

    private void calculateSalesForReportData(List<Order> orders, List<ReportItem> reportItems) {
        orders.forEach(order -> {
            String orderDateString = dateFormat.format(order.getOrderTime());
            ReportItem reportItem = new ReportItem(orderDateString);
            int indexOf = reportItems.indexOf(reportItem);
            if (indexOf >= 0) {
                reportItem = reportItems.get(indexOf);
                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseOrdersCount();
            }
        });
    }

    private void printRawData(List<Order> orders) {
        orders.forEach(order -> System.out.printf("%-3d | %s | %10.2f | %10.2f\n",
                order.getId(), order.getOrderTime(), order.getProductCost(), order.getSubtotal()));
    }

    private List<ReportItem> createReportData(Date startTime, Date endTime) {
        List<ReportItem> reportItems = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        while (startDate.before(endDate)) {
            Date currentDate = startDate.getTime();
            String dateString = dateFormat.format(currentDate);
            reportItems.add(new ReportItem(dateString));
            startDate.add(Calendar.DATE, 1);
        }

        return reportItems;
    }
}
