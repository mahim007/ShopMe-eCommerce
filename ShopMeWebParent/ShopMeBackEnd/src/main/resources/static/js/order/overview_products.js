let fieldProductCost;
let fieldSubtotal;
let fieldShippingCost;
let fieldTax;
let fieldTotal;

function formatNumberForField(fieldRef) {
    fieldRef.val($.number(fieldRef.val(), 2));
}

function setAndFormatNumberForField(fieldRef, fieldVal) {
    fieldRef.val($.number(fieldVal, 2));
}

function formatOrderAmounts() {
    formatNumberForField(fieldProductCost);
    formatNumberForField(fieldSubtotal);
    formatNumberForField(fieldShippingCost);
    formatNumberForField(fieldTax);
    formatNumberForField(fieldTotal);
}

function formatProductAmounts() {
    $(".cost-input").each((index, item) => {
        formatNumberForField($(item));
    });

    $(".price-input").each((index, item) => {
        formatNumberForField($(item));
    });

    $(".subtotal-output").each((index, item) => {
        formatNumberForField($(item));
    });

    $(".ship-input").each((index, item) => {
        formatNumberForField($(item));
    });
}

function getValueWithoutThousandSeparator(fieldRef) {
    let fieldVal = fieldRef.val().replace(',', '');
    return parseFloat(fieldVal);
}

function updateSubtotalWhenQuantityChanged(element) {
    let quantityVal = getValueWithoutThousandSeparator(element);
    let rowNumber = element.attr('rowNumber');

    let priceField = $(`#price-${rowNumber}`);
    let priceValue = getValueWithoutThousandSeparator(priceField);
    let newSubtotal = quantityVal * priceValue;

    let subtotalField = $("#subtotal-" + rowNumber);
    setAndFormatNumberForField(subtotalField, newSubtotal);
}

function updateSubtotalWhenPriceChanged(element) {
    let unitPrice = getValueWithoutThousandSeparator(element);
    let rowNumber = element.attr('id').replace('price-', '');

    let quantity = $(`.quantity-input[rowNumber='${rowNumber}']`);
    let quantityVal = getValueWithoutThousandSeparator(quantity);

    let newSubtotal = quantityVal * unitPrice;
    let subtotalField = $("#subtotal-" + rowNumber);
    setAndFormatNumberForField(subtotalField, newSubtotal);
}

function updateOrderAmounts() {
    let totalCost = 0.0;
    $(".cost-input").each((index, item) => {
        let rowNumber = $(item).attr('rowNumber');
        let quantityVal = getValueWithoutThousandSeparator($("#quantity-" + rowNumber));
        let productCost = getValueWithoutThousandSeparator($(item));

        totalCost += quantityVal * productCost;
    });

    setAndFormatNumberForField($("#productCost"), totalCost);

    let orderSubtotal = 0.0;
    $(".subtotal-output").each((index, item) => {
        let subtotal = getValueWithoutThousandSeparator($(item));
        orderSubtotal += subtotal;
    });

    setAndFormatNumberForField($("#subtotal"), orderSubtotal);

    let shippingCost = 0.0;
    $(".ship-input").each((index, item) => {
        let cost = getValueWithoutThousandSeparator($(item));
        shippingCost += cost;
    });

    setAndFormatNumberForField($("#shippingCost"), shippingCost);

    let tax = getValueWithoutThousandSeparator($("#tax"));
    let orderTotal = orderSubtotal + shippingCost + tax;
    setAndFormatNumberForField($("#total"), orderTotal);
}

function setCountryName() {
    let countryName = $("#country option:selected").text();
    $("#countryName").val(countryName);
}

function processFormBeforeSubmit() {
    setCountryName();
    removeThousandSeparatorForField(fieldProductCost);
    removeThousandSeparatorForField(fieldSubtotal);
    removeThousandSeparatorForField(fieldShippingCost);
    removeThousandSeparatorForField(fieldTax);
    removeThousandSeparatorForField(fieldTotal);

    $(".cost-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".price-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".ship-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".subtotal-output").each(function (e) {
        removeThousandSeparatorForField($(this));
        $(this).attr("disabled", false);
    });
}

function removeThousandSeparatorForField(fieldRef) {
    fieldRef.val(fieldRef.val().replace(",", ""));
    console.log("after update: ", fieldRef);
}

$(document).ready(function () {
    fieldProductCost = $('#productCost');
    fieldSubtotal = $('#subtotal');
    fieldShippingCost = $('#shippingCost');
    fieldTax = $('#tax');
    fieldTotal = $('#total');

    formatOrderAmounts();
    formatProductAmounts();

    $("#productList").on("change", ".quantity-input", function (event) {
        updateSubtotalWhenQuantityChanged($(this));
        updateOrderAmounts();
    });

    $("#productList").on("change", ".price-input", function (event) {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmounts();
    });

    $("#productList").on("change", ".cost-input", function (event) {
        updateOrderAmounts();
    });

    $("#productList").on("change", ".ship-input", function (event) {
        updateOrderAmounts();
    });
});