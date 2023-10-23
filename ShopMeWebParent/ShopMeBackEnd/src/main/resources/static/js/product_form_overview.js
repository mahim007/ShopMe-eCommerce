$(document).ready(function () {
    let categoryDropdown = $("#category");
    let brandDropdown = $("#brand");

    brandDropdown.change(function (e) {
        $(this).children("option:selected").each(function () {
            categoryDropdown.empty();
            if ($(this).val()) {
                fetchCategories($(this).val(), categoryDropdown);
            } else {
                console.log("Default category selected from browser");
                $("<option>").val("").text("[No Category]").appendTo(categoryDropdown);
                $("#category").val("").change();
            }
        });
    });

    fetchCategoriesForNewForm(brandDropdown.val(), categoryDropdown);
});

function fetchCategoriesForNewForm(brandId, categoryDropdown) {
    let categoryId = $("#categoryId");
    let editMode = false;

    if (categoryId && categoryId.length > 0) {
        editMode = true;
    }

    if (!editMode && brandId !== "-1") fetchCategories(brandId, categoryDropdown);
}

function fetchCategories(brandId, categoryDropdown) {

    let url = brandModuleURL + "/" + brandId + "/categories";
    let _csrf = $("input[name='_csrf']").val();

    fetch(url + "?" + new URLSearchParams({_csrf: _csrf}))
        .then(res => res.json())
        .then(data => {
            data.forEach((item, index) => {
                $("<option>").val(item.id).text(item.name).appendTo(categoryDropdown);
            })
        })
        .catch(e => console.log("Error occurred due to: ", e))
}

function checkProductUnique(form) {
    let url = productCheckUniqueUrl;
    let productId = $("#id").val();
    let productName = $("#name").val();
    let productAlias = $("#alias").val();
    let csrf = $("input[name='_csrf']").val();

    let params = {id: productId, name: productName, alias: productAlias, _csrf: csrf};

    fetch(url + "?" + new URLSearchParams(params), {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(res => res.text())
        .then(data => {
            if (data === "OK") {
                form.submit();
            } else if(data === "DuplicatedName") {
                showModalDialog("Warning", "Product with name: <b>" + productName + "</b> has been registered already.");
            } else if (data === "DuplicatedAlias") {
                showModalDialog("Warning", "Product with alias: <b>" + productAlias + "</b> has been registered already.");
            }
        })
        .catch(e => {
            showModalDialog("Error", "Could not connect to server.")
        });

    return false;
}