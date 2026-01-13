package com.mahim.shopme.admin.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportRestController {
    private final MasterOrderReportService masterOrderReportService;

    public ReportRestController(MasterOrderReportService masterOrderReportService) {
        this.masterOrderReportService = masterOrderReportService;
    }

    @GetMapping("/sales_by_date/{period}")
    public List<ReportItem> getSalesByDate(@PathVariable String period) {

        return switch (period) {
            case "last_7_days" -> masterOrderReportService.getReportDataLast7Days();
            case "last_28_days" -> masterOrderReportService.getReportDataLast28Days();
            case "last_6_months" -> masterOrderReportService.getReportDataLast6Months();
            case "last_12_months" -> masterOrderReportService.getReportDataLast12Months();
            default -> masterOrderReportService.getReportDataLast7Days();
        };
    }
}
