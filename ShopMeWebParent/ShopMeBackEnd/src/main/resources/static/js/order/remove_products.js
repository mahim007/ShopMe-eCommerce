function removeProduct(link) {
    let rowNumber = link.attr("rowNumber");
    $("#row-" + rowNumber).remove();
    $("#blankline-" + rowNumber).remove();

    $(".divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function isOnlyProduct() {
    let productCount = $(".hiddenProductId").length;
    return productCount === 1;
}

$(document).ready(function () {
    $("#productList").on("click", ".linkRemove", function (e) {
        e.preventDefault();
        if (isOnlyProduct()) {
            showModalDialog("Warning", "Could not remove product. The order must have at least one product.");
        } else {
            removeProduct($(this));
            updateOrderAmounts();
        }

        console.log("could not return from scope");
    })
});