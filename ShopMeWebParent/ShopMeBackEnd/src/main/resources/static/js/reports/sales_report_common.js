// Sales report common scripts

let data;
let chartOptions;
let totalGrossSales;
let totalNetSales;
let totalItems;

function setupEventHandlers(reportType, callback) {
    $(".button_sales_by" + reportType).on("click", function () {
        if ($(this).hasClass("btn-light")) {
            $(".button_sales_by" + reportType).each(function (index, element) {
                $(element).removeClass("btn-primary").addClass("btn-light");
            });

            $(this).removeClass("btn-light").addClass("btn-primary");

            let divCustomDateRange = $("#divCustomDateRange" + reportType);
            let period = $(this).attr("period");
            if (period) {
                divCustomDateRange.addClass('d-none')
                callback(period);
            } else {
                divCustomDateRange.removeClass('d-none');
            }
        }
    });

    initCustomDateRange(reportType);
    $("#btnViewReportByDateRange" + reportType).on("click", function (e) {
        e.preventDefault();
        if (validateDateRange(reportType)) {
            callback("custom");
        }
    });
}

function initCustomDateRange(reportType) {
    let startDateField = $("#startDate" + reportType);
    let endDateField = $("#endDate" + reportType);

    let endDate = new Date();
    endDateField.val(endDate.toISOString().split('T')[0]);

    let startDate = new Date();
    startDate.setDate(endDate.getDate() - 30);
    startDateField.val(startDate.toISOString().split('T')[0]);
}

function getNumberOfDays(reportType) {
    let startDateField = $("#startDate" + reportType);
    let endDateField = $("#endDate" + reportType);

    let startDate = new Date(startDateField.val());
    let endDate = new Date(endDateField.val());

    let differenceInMillis = endDate.getTime() - startDate.getTime();
    return differenceInMillis / (1000 * 3600 * 24); // difference in days
}

function validateDateRange(reportType) {
    let startDateField = $("#startDate" + reportType);
    let endDateField = $("#endDate" + reportType);
    let differenceInDays = getNumberOfDays(reportType);

    if (differenceInDays >= 1 && differenceInDays <= 30) {
        startDateField[0].setCustomValidity("");
        return true;
    } else {
        startDateField[0].setCustomValidity("Date range must be between 1 and 30 days");
        startDateField[0].reportValidity();
        return false;
    }
}

function formatCurrency(amount) {
    let formattedAmount = $.number(amount, decimalDigits, decimalPointType, thousandPointType);
    return prefixCurrencySymbol + formattedAmount + suffixCurrencySymbol;
}

function getChartTitle(period) {
    switch (period) {
        case "last_7_days": return "Sales in Last 7 Days";
        case "last_28_days": return "Sales in Last 28 Days";
        case "last_6_months": return "Sales in Last 6 Moths";
        case "last_12_months": return "Sales in Last 12 Months";
        case "custom": return "Sales in Custom Date Range";
    }

    return "";
}

function getDenominator(period, reportType) {
    switch (period) {
        case "last_7_days": return 7;
        case "last_28_days": return 28;
        case "last_6_months": return 6;
        case "last_12_months": return 12;
        case "custom": return getNumberOfDays(reportType);
        default: return 1;
    }
}

function setSalesAmount(period, reportType, labelTotalItems) {
    $("#textTotalGrossSales" + reportType).text(formatCurrency(totalGrossSales));
    $("#textTotalNetSales" + reportType).text(formatCurrency(totalNetSales));
    $("#textAvgGrossSales" + reportType).text(formatCurrency(totalGrossSales / getDenominator(period, reportType)));
    $("#textAvgNetSales" + reportType).text(formatCurrency(totalNetSales / getDenominator(period, reportType)));
    $("#labelTotalItems" + reportType).text(labelTotalItems);
    $("#textTotalItems" + reportType).text($.number(totalItems));
}

function formatChartData(data, colIndex1, colIndex2) {
    let formatter = new google.visualization.NumberFormat({
        prefix: prefixCurrencySymbol,
        suffix: suffixCurrencySymbol,
        decimalSymbol: decimalPointType,
        groupingSymbol: thousandPointType,
        fractionDigits: decimalDigits
    });

    formatter.format(data, colIndex1); // format currency for gross sales column
    formatter.format(data, colIndex2); // format currency for net sales column
}

function getUrlSuffix(period, reportType) {
    if (period === "custom") {
        let startDateField = $("#startDate" + reportType);
        let endDateField = $("#endDate" + reportType);
        return startDateField.val() + "/" + endDateField.val();
    }

    return period;
}