package com.mahim.shopme.admin.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractReportService {
    public List<ReportItem> getReportDataLast7Days(ReportType reportType) {
        return getReportDataLastXDays(7, reportType);
    }

    public List<ReportItem> getReportDataLast28Days(ReportType reportType) {
        return getReportDataLastXDays(28, reportType);
    }

    protected List<ReportItem> getReportDataLastXDays(int days, ReportType reportType) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        Date startTime = calendar.getTime();
        return getReportDataByDateRangeInternal(startTime, endTime, reportType, PeriodType.DAYS);
    }

    public List<ReportItem> getReportDataLast6Months(ReportType reportType) {
        return getReportDataLastXMonths(6, reportType);
    }

    public List<ReportItem> getReportDataLast12Months(ReportType reportType) {
        return getReportDataLastXMonths(12, reportType);
    }

    protected List<ReportItem> getReportDataLastXMonths(int months, ReportType reportType) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        Date startTime = calendar.getTime();
        return getReportDataByDateRangeInternal(startTime, endTime, reportType, PeriodType.MONTHS);
    }

    public List<ReportItem> getReportDataByDateRange(String startDate, String endDate, ReportType reportType) throws ParseException {
        DateFormat formatter = getDateFormatter(PeriodType.DAYS);
        Date startTime = formatter.parse(startDate);
        Date endTime = formatter.parse(endDate);
        return getReportDataByDateRangeInternal(startTime, endTime, reportType, PeriodType.DAYS);
    }

    protected DateFormat getDateFormatter(PeriodType periodType) {
        return switch (periodType) {
            case DAYS -> new SimpleDateFormat("yyyy-MM-dd");
            case MONTHS -> new SimpleDateFormat("yyyy-MM");
            default -> null;
        };
    }

    protected abstract List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType, PeriodType periodType);
}
