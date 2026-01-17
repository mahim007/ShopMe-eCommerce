// Sales report by product

let dataByProduct;
let chartOptionsByProduct;

$(document).ready(function () {
    setupEventHandlers("_product", loadSalesReportByDateForProduct);
});

function loadSalesReportByDateForProduct(period = "last_7_days") {
    let requestURL = contextPath + "reports/product/" + getUrlSuffix(period, "_product");
    fetchData(requestURL, "GET", res => {
        prepareChartDataForSalesReportByProduct(res);
        customizeChartForSalesReportByProduct(period);
        formatChartData(dataByProduct, 2, 3);
        drawChartForSalesReportByProduct(period);
        setSalesAmount(period, "_product", "Total Products");
    }, error => console.log(error))
}

function prepareChartDataForSalesReportByProduct(res) {
    dataByProduct = new google.visualization.DataTable();
    dataByProduct.addColumn("string", "Product");
    dataByProduct.addColumn("number", "Quantity");
    dataByProduct.addColumn("number", "Gross Sales");
    dataByProduct.addColumn("number", "Net Sales");

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    res.forEach(item => {
        dataByProduct.addRows([[item.identifier, item.productCount, item.grossSales, item.netSales]]);
        totalGrossSales += parseFloat(item.grossSales);
        totalNetSales += parseFloat(item.netSales);
        totalItems += parseInt(item.productCount);
    });
}

function customizeChartForSalesReportByProduct(period) {
    chartOptionsByProduct = {
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
        salesChart.draw(dataByProduct, chartOptionsByProduct);
    });

    observer.observe(container);
}