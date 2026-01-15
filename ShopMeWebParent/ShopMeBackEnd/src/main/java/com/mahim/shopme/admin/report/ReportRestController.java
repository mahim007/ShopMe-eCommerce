package com.mahim.shopme.admin.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportRestController {
    private final MasterOrderReportService masterOrderReportService;
    private final OrderDetailsReportService orderDetailsReportService;

    public ReportRestController(MasterOrderReportService masterOrderReportService,
                                OrderDetailsReportService orderDetailsReportService) {
        this.masterOrderReportService = masterOrderReportService;
        this.orderDetailsReportService = orderDetailsReportService;
    }

    @GetMapping("/sales_by_date/{period}")
    public List<ReportItem> getSalesByDate(@PathVariable String period) {

        return switch (period) {
            case "last_7_days" -> masterOrderReportService.getReportDataLast7Days(ReportType.DATE);
            case "last_28_days" -> masterOrderReportService.getReportDataLast28Days(ReportType.DATE);
            case "last_6_months" -> masterOrderReportService.getReportDataLast6Months(ReportType.DATE);
            case "last_12_months" -> masterOrderReportService.getReportDataLast12Months(ReportType.DATE);
            default -> masterOrderReportService.getReportDataLast7Days(ReportType.DATE);
        };
    }

    @GetMapping("/sales_by_date/{startDate}/{endDate}")
    public List<ReportItem> getSalesByDate(@PathVariable String startDate, @PathVariable String endDate) throws ParseException {
        return masterOrderReportService.getReportDataByDateRange(startDate, endDate, ReportType.DATE);
    }

    @GetMapping("/{groupBy}/{period}")
    public List<ReportItem> getReportDataByCategoryOrProduct(@PathVariable String groupBy, @PathVariable String period) {
        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());

        return switch (period) {
            case "last_7_days" -> orderDetailsReportService.getReportDataLast7Days(reportType);
            case "last_28_days" -> orderDetailsReportService.getReportDataLast28Days(reportType);
            case "last_6_months" -> orderDetailsReportService.getReportDataLast6Months(reportType);
            case "last_12_months" -> orderDetailsReportService.getReportDataLast12Months(reportType);
            default -> orderDetailsReportService.getReportDataLast7Days(reportType);
        };
    }

    @GetMapping("/{groupBy}/{startDate}/{endDate}")
    public List<ReportItem> getSalesByDate(@PathVariable String groupBy, @PathVariable String startDate, @PathVariable String endDate) throws ParseException {
        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
        return orderDetailsReportService.getReportDataByDateRange(startDate, endDate, reportType);
    }
}
