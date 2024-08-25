const decimalSeparator = decimalPointType === 'COMMA' ? ',' : '.';
const thousandsSeparator = thousandsPointType === 'COMMA' ? ',' : '.';

$(document).ready(function () {
    $(".link-minus").on("click", function (e) {
        e.preventDefault();
        decreaseQuantity($(this));
    });

    $(".link-plus").on("click", function (e) {
        e.preventDefault();
        increaseQuantity($(this));
    });

    $(".link-remove").on("click", function (e) {
        e.preventDefault();
        removeProduct($(this))
    });
});

function increaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $('#quantity' + productId);
    let newVal = parseInt(quantityInput.val()) + 1;

    if (newVal <= 5) {
        quantityInput.val(newVal);
        updateQuantity(productId, newVal);
    } else {
        showModalDialog("Warning", "Maximum quantity number is 5.");
    }
}

function decreaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $('#quantity' + productId);
    let newVal = parseInt(quantityInput.val()) - 1;

    if (newVal > 0) {
        quantityInput.val(newVal);
        updateQuantity(productId, newVal);
    } else {
        showModalDialog("Warning", "Minimum quantity number is 1.");
    }
}

function updateQuantity(productId, quantity) {
    let url = contextPath + "cart/update/" + productId + "/" + quantity;

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        }
    })
        .then(response => response.text())
        .then(data => {
            updateSubTotal(productId, data);
            updateTotal();
        })
        .catch(err => {
            showModalDialog("Shopping Cart", err);
        });
}

function updateSubTotal(productId, updatedSubtotal) {
    $("#subtotal-" + productId).data('number', updatedSubtotal);
    $("#subtotal-" + productId).find("span.h4").text(formatCurrency(updatedSubtotal));
}

function showEmptyShoppingCart() {
    $("#sectionTotal").hide();
    $("#emptyCartMsg").removeClass('d-none');
}

function updateTotal() {
    let total = 0.0;
    let productCount = 0;

    $("div.subtotal").each((index, item) => {
        total += parseFloat($(item).data('number'));
        productCount++;
    });

    if (productCount === 0) {
        showEmptyShoppingCart();
        return;
    }

    $("#estimatedTotal").data('number', total);
    $("#estimatedTotal").find("span.h4").text(formatCurrency(total));
}

function removeProduct(link) {
    let url = link.attr("href");
    fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        }
    })
        .then(response => response.text())
        .then(data => {
            removeProductHTML(link.attr("rowNumber"));
            showModalDialog("Shopping Cart", data);
            updateCountNumber();
            updateTotal();
        })
        .catch(err => {
            showModalDialog("Shopping Cart", err);
        });
}

function removeProductHTML(rowNumber) {
    $('#row' + rowNumber).remove();
    $('#blankline' + rowNumber).remove();
}

function updateCountNumber() {
    $(".divCount").each((index, item) => {
        item.innerHTML = "" + (index + 1);
    });
}

function formatCurrency(amount) {
    return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}