// Sales report by date

let data;
let chartOptions;
let totalGrossSales;
let totalNetSales;
let totalOrders;

$(document).ready(function () {
    $(".button_sales_by_date").on("click", function () {
        if ($(this).hasClass("btn-light")) {
            $(".button_sales_by_date").each(function (index, element) {
                $(element).removeClass("btn-primary").addClass("btn-light");
            });

            $(this).removeClass("btn-light").addClass("btn-primary");

            let period = $(this).attr("period");
            loadSalesReportByDate(period);
        }
    });
});

function loadSalesReportByDate(period = "last_7_days") {
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
        prefix: '$'
    });

    formatter.format(data, 1); // format currency for gross sales column
    formatter.format(data, 2); // format currency for net sales column
}

function drawChart(period) {
    let salesChart = new google.visualization.ColumnChart(document.getElementById("chart_sales_by_date"));
    salesChart.draw(data, chartOptions);

    $("#textTotalGrossSales").text("$" + $.number(totalGrossSales, 2));
    $("#textTotalNetSales").text("$" + $.number(totalNetSales, 2));
    $("#textAvgGrossSales").text("$" + $.number(totalGrossSales / getDays(period), 2));
    $("#textAvgNetSales").text("$" + $.number(totalNetSales / getDays(period), 2));
    $("#textTotalOrders").text($.number(totalOrders));
}

function getChartTitle(period) {
    switch (period) {
        case "last_7_days": return "Sales in last 7 days";
        case "last_28_days": return "Sales in last 28 days";
    }

    return "";
}

function getDays(period) {
    switch (period) {
        case "last_7_days": return 7;
        case "last_28_days": return 28;
    }
}