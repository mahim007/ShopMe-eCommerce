let addProductModal;
let productDetailCount;

$(document).ready(function(){
    productDetailCount = $(".hiddenProductId").length;
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
            getProductInfo(productId, data);
        })
        .catch(error => {
            showModalDialog("Warning", error.message);
            getProductInfo(productId, 0.0);
        })
        .finally(() => {
            $("#addProductModal").modal("hide");
        })
}

function getProductInfo(productId, shippingCost) {
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
        .then(data => {
            let productName = data.name;
            let mainImagePath = contextPath.substring(0, contextPath.length - 1) + data.imagePath;
            let productCost = $.number(data.cost, 2);
            let productPrice = $.number(data.price, 2);
            let htmlCode = generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost);
            $("#productList").append(htmlCode);
        })
        .catch(error => showModalDialog("Warning", error.message));
}

function generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost) {
    let nextCount = productDetailCount + 1;
    productDetailCount++;

    let quantityId = "quantity-" + nextCount;
    let costId = "cost-" + nextCount;
    let priceId = "price-" + nextCount;
    let subtotalId = "subtotal-" + nextCount;
    let shipId = "ship-" + nextCount;
    let blankLineId = "blankline-" + nextCount;

    return `
        <div class="border rounded p-1" id="row-${nextCount}">
            <div class="row">
                <input type="hidden" name="detailId" value="0" />
                <input class="hiddenProductId" type="hidden" name="productId" value="${productId}" />
                <div class="col-1 col-md-1 col-lg-1">
                    <div class="divCount">${nextCount}</div>
                    <div><a class="fas fa-trash icon-danger linkRemove" href="" rowNumber="${nextCount}"></a></div>
                </div>
                <div class="col-6 col-md-4 col-lg-3">
                    <img src="${mainImagePath}" class="img-fluid"/>
                </div>
            </div>
            <div class="row">
                <div class="col-10 col-md-7 col-lg-8">
                    <div>
                        <b>${productName}</b>
                    </div>
                    <table>
                        <tr>
                            <td>Product Cost: </td>
                            <td>
                                <input type="text" required class="form-control m-1 cost-input"
                                    name="productDetailCost"
                                    rowNumber="${nextCount}"
                                    id="${costId}"
                                    value="${productCost}" />
                            </td>
                        </tr>
                        <tr>
                            <td>Quantity: </td>
                            <td>
                                <input type="number" min="0" max="5" step="1" required class="form-control m-1 quantity-input"
                                    name="quantity"
                                    rowNumber="${nextCount}"
                                    id="${quantityId}"
                                    value="1"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td>Unit price: </td>
                            <td>
                                <input type="text" required class="form-control m-1 price-input"
                                    name="productPrice"
                                    rowNumber="${nextCount}"
                                    id="${priceId}"
                                    value="${productPrice}"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td>Subtotal: </td>
                            <td>
                                <input type="text" required class="form-control m-1 subtotal-output"
                                    name="productSubtotal"
                                     rowNumber="${nextCount}"
                                     id="${subtotalId}"
                                     value="${productPrice}"
                                     disabled />
                            </td>
                        </tr>
                        <tr>
                            <td>Shipping Cost: </td>
                            <td>
                                <input type="text" required class="form-control m-1 ship-input"
                                    name="productShipCost"
                                    rowNumber="${nextCount}"
                                    id="${shipId}"
                                    value="${shippingCost}" />
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <div class="row m-1" id="${blankLineId}">&nbsp;</div>
    `;
}