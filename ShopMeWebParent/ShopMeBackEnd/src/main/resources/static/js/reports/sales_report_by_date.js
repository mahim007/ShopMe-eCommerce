// Sales report by date

$(document).ready(function () {
    setupEventHandlers("_date", loadSalesReportByDate);
});

function loadSalesReportByDate(period = "last_7_days") {
    console.log('hello from date report')
    let requestURL = contextPath + "reports/sales_by_date/" + getUrlSuffix(period, "_date");
    fetchData(requestURL, "GET", res => {
        prepareChartDataForSalesReportByDate(res);
        customizeChartForSalesReportByDate(period);
        formatChartData(data, 1, 2);
        drawChartForSalesReportByDate(period);
        setSalesAmount(period, "_date", "Total Orders");
    }, error => console.log(error))
}

function prepareChartDataForSalesReportByDate(res) {
    data = new google.visualization.DataTable();
    data.addColumn("string", "Date");
    data.addColumn("number", "Gross Sales");
    data.addColumn("number", "Net Sales");
    data.addColumn("number", "Orders");

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    res.forEach(item => {
        data.addRows([[item.identifier, item.grossSales, item.netSales, item.orderCount]]);
        totalGrossSales += parseFloat(item.grossSales);
        totalNetSales += parseFloat(item.netSales);
        totalItems += parseInt(item.orderCount);
    });
}

function customizeChartForSalesReportByDate(period) {
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
}

function drawChartForSalesReportByDate() {
    let salesChart = new google.visualization.ColumnChart(document.getElementById("chart_sales_by_date"));
    salesChart.draw(data, chartOptions);
}