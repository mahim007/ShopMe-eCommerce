let addProductModal;

$(document).ready(function(){
    $("#products").on("click", "#linkAddProduct", function(e){
        e.preventDefault();
        let link = $(this);
        let url = link.attr("href");

        $("#addProductModal").on("shown.bs.modal", function(){
            $(this).find("iframe").attr("src", url);
        });
        addProductModal = new bootstrap.Modal($("#addProductModal")).show();
    });
});

function productAlreadyAdded(productId) {
    let productExists = false;
    $(".hiddenProductId").each(function () {
        let pId = $(this).val();
        if (pId === productId) {
            productExists = true
            return;
        }
    });

    return productExists;
}

function addProduct(productId, productName) {
    $("#addProductModal").modal("hide");
    getShippingCost(productId);
}

function getShippingCost(productId) {
    let selectedCountry = $("#country option:selected").val();
    let state = $("#state").val();
    if (state.length === 0) {
        state = $("#city").val();
    }

    let requestUrl = contextPath + "shipping_rates/shipping_cost?productId=" + productId + "&countryId=" + selectedCountry + "&state=" + state;
    fetch(requestUrl, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        }
    })
        .then(async res => {
            if (res.ok) {
                return res.text();
            }
            throw new Error(await res.text());
        })
        .then(data => {
            console.log("success: ", data);
            getProductInfo(productId);
        })
        .catch(error => {
            showModalDialog("Warning", error.message);
            getProductInfo(productId);
        })
        .finally(() => {
            $("#addProductModal").modal("hide");
        })
}

function getProductInfo(productId) {
    let requestUrl = contextPath + "products/get/" + productId;
    fetch(requestUrl, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        }
    })
        .then(async res => {
            if (res.ok) {
                return res.json();
            }
            throw new Error("Product not found");
        })
        .then(data => console.log("success: ", data))
        .catch(error => showModalDialog("Warning", error.message));
}