// Sales report by category

let dataByCategory;
let chartOptionsByCategory;

$(document).ready(function () {
    setupEventHandlers("_category", loadSalesReportByDateForCategory);
});

function loadSalesReportByDateForCategory(period = "last_7_days") {
    let requestURL = contextPath + "reports/category/" + getUrlSuffix(period, "_category");
    fetchData(requestURL, "GET", res => {
        prepareChartDataForSalesReportByCategory(res);
        customizeChartForSalesReportByCategory(period);
        formatChartData(dataByCategory, 1, 2);
        drawChartForSalesReportByCategory(period);
        setSalesAmount(period, "_category", "Total Products");
    }, error => console.log(error))
}

function prepareChartDataForSalesReportByCategory(res) {
    dataByCategory = new google.visualization.DataTable();
    dataByCategory.addColumn("string", "Category");
    dataByCategory.addColumn("number", "Gross Sales");
    dataByCategory.addColumn("number", "Net Sales");

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    res.forEach(item => {
        dataByCategory.addRows([[item.identifier, item.grossSales, item.netSales]]);
        totalGrossSales += parseFloat(item.grossSales);
        totalNetSales += parseFloat(item.netSales);
        totalItems += parseInt(item.productCount);
    });
}

function customizeChartForSalesReportByCategory(period) {
    chartOptionsByCategory = {
        height: 360,
        legend: {position: "right"},
    };
}

function drawChartForSalesReportByCategory() {
    const container = document.getElementById("chart_sales_by_category");
    const salesChart = new google.visualization.PieChart(container);

    const observer = new ResizeObserver(entries => {
        salesChart.draw(dataByCategory, chartOptionsByCategory);
    });

    observer.observe(container);
}