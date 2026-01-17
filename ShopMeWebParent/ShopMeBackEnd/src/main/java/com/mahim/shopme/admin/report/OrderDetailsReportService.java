package com.mahim.shopme.admin.report;

import com.mahim.shopme.admin.order.OrderDetailsRepository;
import com.mahim.shopme.common.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderDetailsReportService extends AbstractReportService {

    private final OrderDetailsRepository orderDetailsRepository;

    public OrderDetailsReportService(OrderDetailsRepository orderDetailsRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
    }

    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType, PeriodType periodType) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        if (reportType.equals(ReportType.CATEGORY)) {
            orderDetails = orderDetailsRepository.findByCategoryAndTimeBetween(startTime, endTime);

        } else if ( reportType.equals(ReportType.PRODUCT)) {
            orderDetails = orderDetailsRepository.findByProductAndTimeBetween(startTime, endTime);
        }

        List<ReportItem> reportItems = new ArrayList<>();
        orderDetails.forEach(item -> {
            String identifier = "";

            if (reportType.equals(ReportType.CATEGORY)) {
                identifier = item.getProduct().getCategory().getName();
            } else if (reportType.equals(ReportType.PRODUCT)) {
                identifier = item.getProduct().getShortName();
            }

            ReportItem reportItem = new ReportItem(identifier);
            float grossSales = item.getSubtotal() + item.getShippingCost();
            float netSales = item.getSubtotal() - item.getProductCost();

            int indexOf = reportItems.indexOf(reportItem);
            if (indexOf >= 0) {
                ReportItem found = reportItems.get(indexOf);
                found.addGrossSales(grossSales);
                found.addNetSales(netSales);
                found.increaseProductCount(item.getQuantity());

            } else {
                reportItems.add(new ReportItem(identifier, grossSales, netSales, item.getQuantity()));
            }
        });

        return reportItems;
    }
}
