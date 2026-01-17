package com.mahim.shopme.admin.report;

import com.mahim.shopme.admin.order.OrderRepository;
import com.mahim.shopme.common.entity.Order;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MasterOrderReportService extends AbstractReportService {
    private final OrderRepository orderRepository;

    public MasterOrderReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType, PeriodType periodType) {
        List<Order> orders = orderRepository.findByOrderTimeBetween(startTime, endTime);

        List<ReportItem> reportItems = createReportData(startTime, endTime, reportType, periodType);
        calculateSalesForReportData(orders, reportItems, reportType, periodType);
        return reportItems;
    }

    private void calculateSalesForReportData(List<Order> orders, List<ReportItem> reportItems, ReportType reportType, PeriodType periodType) {
        DateFormat dateFormat = getDateFormatter(periodType);
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

    private List<ReportItem> createReportData(Date startTime, Date endTime, ReportType reportType, PeriodType periodType) {
        DateFormat dateFormat = getDateFormatter(periodType);
        List<ReportItem> reportItems = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        while (startDate.before(endDate) || startDate.equals(endDate)) {
            Date currentDate = startDate.getTime();
            String dateString = dateFormat.format(currentDate);
            reportItems.add(new ReportItem(dateString));

            if ( periodType == PeriodType.DAYS) {
                startDate.add(Calendar.DATE, 1);
            } else {
                startDate.add(Calendar.MONTH, 1);
            }
        }

        return reportItems;
    }
}
