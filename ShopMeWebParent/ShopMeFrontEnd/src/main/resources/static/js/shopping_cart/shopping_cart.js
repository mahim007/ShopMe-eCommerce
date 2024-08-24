$(document).ready(function () {
    $(".link-minus").on("click", function (e) {
        e.preventDefault();
        decreaseQuantity($(this));
    });

    $(".link-plus").on("click", function (e) {
        e.preventDefault();
        increaseQuantity($(this));
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
    const formattedSubTotal = $.number(updatedSubtotal, 2);
    $("#subtotal-" + productId).data('number', updatedSubtotal);
    $("#subtotal-" + productId).find("span.h4").text(formattedSubTotal);
}

function updateTotal() {
    let total = 0.0;
    $("div.subtotal").each((index, item) => {
        total += parseFloat($(item).data('number'));
    });

    $("#estimatedTotal").data('number', total);
    let formattedTotal = $.number(total, 2, '.', ',');
    $("#estimatedTotal").find("span.h4").text(formattedTotal);
}