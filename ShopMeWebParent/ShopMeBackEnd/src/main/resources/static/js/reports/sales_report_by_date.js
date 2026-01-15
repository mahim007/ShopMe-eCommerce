// Sales report by date

let data;
let chartOptions;
let totalGrossSales;
let totalNetSales;
let totalOrders;
let startDateField;
let endDateField;

function initCustomDateRange() {
    startDateField = $("#startDate");
    endDateField = $("#endDate");

    let endDate = new Date();
    endDateField.val(endDate.toISOString().split('T')[0]);

    let startDate = new Date();
    startDate.setDate(endDate.getDate() - 30);
    startDateField.val(startDate.toISOString().split('T')[0]);
}

function getNumberOfDays() {
    let startDate = new Date(startDateField.val());
    let endDate = new Date(endDateField.val());

    let differenceInMillis = endDate.getTime() - startDate.getTime();
    return differenceInMillis / (1000 * 3600 * 24);
}

function validateDateRange() {
    let differenceInDays = getNumberOfDays();
    if (differenceInDays >= 1 && differenceInDays <= 30) {
        startDateField[0].setCustomValidity("");
        return true;
    } else {
        startDateField[0].setCustomValidity("Date range must be between 1 and 30 days");
        startDateField[0].reportValidity();
        return false;
    }
}

$(document).ready(function () {
    $(".button_sales_by_date").on("click", function () {
        if ($(this).hasClass("btn-light")) {
            $(".button_sales_by_date").each(function (index, element) {
                $(element).removeClass("btn-primary").addClass("btn-light");
            });

            $(this).removeClass("btn-light").addClass("btn-primary");

            let divCustomDateRange = $("#divCustomDateRange");
            let period = $(this).attr("period");
            if (period) {
                divCustomDateRange.addClass('d-none')
                loadSalesReportByDate(period);
            } else {
                divCustomDateRange.removeClass('d-none');
            }
        }
    });

    initCustomDateRange();
    $("#btnViewReportByDateRange").on("click", function (e) {
        e.preventDefault();
        if (validateDateRange()) {
            loadSalesReportByDate("custom")
        }
    });
});

function loadSalesReportByDate(period = "last_7_days") {
    if (period === "custom") {
        period = $("#startDate").val() + "/" + $("#endDate").val();
    }

    let requestURL = contextPath + "reports/sales_by_date/" + period;
    fetchData(requestURL, "GET", res => {
        prepareChartData(res);
        customizeChart(period);
        drawChart(period);
    }, error => console.log(error))
}

function prepareChartData(res) {
    data = new google.visualization.DataTable();
    data.addColumn("string", "Date");
    data.addColumn("number", "Gross Sales");
    data.addColumn("number", "Net Sales");
    data.addColumn("number", "Orders");

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalOrders = 0;

    res.forEach(item => {
        data.addRows([[item.identifier, item.grossSales, item.netSales, item.orderCount]]);
        totalGrossSales += parseFloat(item.grossSales);
        totalNetSales += parseFloat(item.netSales);
        totalOrders += parseInt(item.orderCount);
    });
}

function customizeChart(period) {
    chartOptions = {
        title: getChartTitle(period),
        height: 360,
        legend: {position: "top"},
        series: {
            0: {targetAxisIndex: 0},
            1: {targetAxisIndex: 0},
            2: {targetAxisIndex: 1},
        },
        vAxes: {
            0: {title: 'Sales Amount', format: 'currency'},
            1: {title: 'Number of Orders'},
        }
    };

    let formatter = new google.visualization.NumberFormat({
        prefix: prefixCurrencySymbol,
        suffix: suffixCurrencySymbol,
        decimalSymbol: decimalPointType,
        groupingSymbol: thousandPointType,
        fractionDigits: decimalDigits
    });

    formatter.format(data, 1); // format currency for gross sales column
    formatter.format(data, 2); // format currency for net sales column
}

function drawChart(period) {
    let salesChart = new google.visualization.ColumnChart(document.getElementById("chart_sales_by_date"));
    salesChart.draw(data, chartOptions);

    $("#textTotalGrossSales").text(formatCurrency(totalGrossSales));
    $("#textTotalNetSales").text(formatCurrency(totalNetSales));
    $("#textAvgGrossSales").text(formatCurrency(totalGrossSales / getDenominator(period)));
    $("#textAvgNetSales").text(formatCurrency(totalNetSales / getDenominator(period)));
    $("#textTotalOrders").text($.number(totalOrders));
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

function getDenominator(period) {
    switch (period) {
        case "last_7_days": return 7;
        case "last_28_days": return 28;
        case "last_6_months": return 6;
        case "last_12_months": return 12;
        case "custom": return getNumberOfDays();
        default: return 1;
    }
}