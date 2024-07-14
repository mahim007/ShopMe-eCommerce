$(document).ready(function () {
    $("#addToCart").on("click", function (e) {
        addToCart();
    })
});

function addToCart() {
    let quantity = $("#quantity" + productId).val();
    let url = contextPath + "cart/add/" + productId + "/" + quantity;

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        }
    }).then(response => response.text())
        .then(data => {
            showModalDialog("Shopping Cart", data);
        })
        .catch(err => {
            showModalDialog("Shopping Cart", err);
        });
}