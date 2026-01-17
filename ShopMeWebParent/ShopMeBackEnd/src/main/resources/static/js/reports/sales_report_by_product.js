// Sales report by product

$(document).ready(function () {
    setupEventHandlers("_product", loadSalesReportByDateForProduct);
});

function loadSalesReportByDateForProduct(period = "last_7_days") {
    let requestURL = contextPath + "reports/product/" + getUrlSuffix(period, "_product");
    fetchData(requestURL, "GET", res => {
        prepareChartDataForSalesReportByProduct(res);
        customizeChartForSalesReportByProduct(period);
        formatChartData(data, 2, 3);
        drawChartForSalesReportByProduct(period);
        setSalesAmount(period, "_product", "Total Products");
    }, error => console.log(error))
}

function prepareChartDataForSalesReportByProduct(res) {
    data = new google.visualization.DataTable();
    data.addColumn("string", "Product");
    data.addColumn("number", "Quantity");
    data.addColumn("number", "Gross Sales");
    data.addColumn("number", "Net Sales");

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    res.forEach(item => {
        data.addRows([[item.identifier, item.productCount, item.grossSales, item.netSales]]);
        totalGrossSales += parseFloat(item.grossSales);
        totalNetSales += parseFloat(item.netSales);
        totalItems += parseInt(item.productCount);
    });
}

function customizeChartForSalesReportByProduct(period) {
    chartOptions = {
        height: 360,
        width: '80%',
        showRowNumber: true,
        page: 'enable',
        sortColumn: 2,
        sortAscending: false,
    };
}

function drawChartForSalesReportByProduct() {
    const container = document.getElementById("chart_sales_by_product");
    const salesChart = new google.visualization.Table(container);

    const observer = new ResizeObserver(entries => {
        salesChart.draw(data, chartOptions);
    });

    observer.observe(container);
}