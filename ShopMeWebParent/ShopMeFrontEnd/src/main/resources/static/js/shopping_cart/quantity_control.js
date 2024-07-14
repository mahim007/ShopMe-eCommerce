$(document).ready(function () {
    $(".link-minus").on("click", function (e) {
        e.preventDefault();
        let productId = $(this).attr("pid");
        let quantityInput = $('#quantity' + productId);
        let newVal = parseInt(quantityInput.val()) - 1;

        if (newVal > 0) {
            quantityInput.val(newVal);
        } else {
            showModalDialog("Warning", "Minimum quantity number is 1.");
        }
    });

    $(".link-plus").on("click", function (e) {
        e.preventDefault();
        let productId = $(this).attr("pid");
        let quantityInput = $('#quantity' + productId);
        let newVal = parseInt(quantityInput.val()) + 1;

        if (newVal <= 5) {
            quantityInput.val(newVal);
        } else {
            showModalDialog("Warning", "Maximum quantity number is 5.");
        }
    });
});