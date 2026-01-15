// Sales report by category

$(document).ready(function () {
    setupEventHandlers("_category", loadSalesReportByDateForCategory);
});

function loadSalesReportByDateForCategory(period = "last_7_days") {
    console.log('hello from category report')
    let requestURL = contextPath + "reports/category/" + getUrlSuffix(period, "_category");
    fetchData(requestURL, "GET", res => {
        prepareChartDataForSalesReportByCategory(res);
        customizeChartForSalesReportByCategory(period);
        formatChartData(data, 1, 2);
        drawChartForSalesReportByCategory(period);
        setSalesAmount(period, "_category", "Total Products");
    }, error => console.log(error))
}

function prepareChartDataForSalesReportByCategory(res) {
    data = new google.visualization.DataTable();
    data.addColumn("string", "Category");
    data.addColumn("number", "Gross Sales");
    data.addColumn("number", "Net Sales");

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    res.forEach(item => {
        data.addRows([[item.identifier, item.grossSales, item.netSales]]);
        totalGrossSales += parseFloat(item.grossSales);
        totalNetSales += parseFloat(item.netSales);
        totalItems += parseInt(item.productCount);
    });
}

function customizeChartForSalesReportByCategory(period) {
    chartOptions = {
        height: 360,
        legend: {position: "right"},
    };
}

function drawChartForSalesReportByCategory() {
    let salesChart = new google.visualization.PieChart(document.getElementById("chart_sales_by_category"));
    salesChart.draw(data, chartOptions);
}