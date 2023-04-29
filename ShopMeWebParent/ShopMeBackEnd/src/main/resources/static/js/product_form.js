$(document).ready(function () {
    let categoryDropdown = $("#category");
    let brandDropdown = $("#brand");

    $("#shortDescription").richText();
    $("#fullDescription").richText();

    brandDropdown.change(function (e) {
        $(this).children("option:selected").each(function () {
            categoryDropdown.empty();
            fetchCategories($(this).val(), categoryDropdown);
        });
    });

    if (brandDropdown.val()) {
        fetchCategories(brandDropdown.val(), categoryDropdown);
    }

    $("input.extraImage").change(function (e) {
        fileOnChangeHandler(e, this);
    });
});

function fileOnChangeHandler(e, inputObject) {
    let name = inputObject.id;
    let num = name ? name[name.length - 1] : "1"
    let thumbnailId = "extraThumbnail" + num;
    let defaultThumbnail = $(thumbnailId).attr("src");
    showExtraThumbnail(inputObject, defaultThumbnail, thumbnailId);
}

function showExtraThumbnail(fileInput, userDefaultImage, thumbnailId) {
    let file = fileInput.files[0];
    let fileReader = new FileReader();

    fileReader.onload = function (e) {
        $(`#${thumbnailId}`).attr("src", e.target.result);
    };

    try {
        let fileSize = fileInput.files[0].size;
        if (fileSize > (1024 * 1024)) {
            fileInput.setCustomValidity("Image must be less than 1 MB");
            fileInput.reportValidity();
        } else {
            fileReader.readAsDataURL(file);

            if (extraImageNo === Number(thumbnailId[thumbnailId.length - 1])) {
                addCrossBtn(extraImageNo);
                showExtraImageSection();
            }
        }
    } catch (err) {
        $(`#${thumbnailId}`).attr("src", userDefaultImage);
    }
}

function addCrossBtn(id) {
    let crossBtn = `<a class="btn fas fa-times-circle fa-2x icon-dark float-end" 
                            title="remove this image"
                            href="javascript:removeExtraImageSection(${id})"
                            ></a>`;
    $("#extraImageHeader" + id).append(crossBtn);
}

function showExtraImageSection() {
    extraImageNo++;
    let imagesDiv = $("#images-div");
    let length = imagesDiv.children("div").length;
    let html = `
        <div class="col border m-3 p-2">
            <div id="extraImageHeader${extraImageNo}">
                <label>Extra Image #${length}</label>
            </div>
            <div class="m-2">
                <img id="extraThumbnail${extraImageNo}" alt="Extra image #${extraImageNo} preview" class="img-fluid" 
                    style="max-width: 400px"
                    src="${defaultProductThumbnailSrc}"/>
            </div>
            <div>
                <input type="file" class="mb-2 form-check-inline extraImage" 
                    id="extraImage${extraImageNo}" 
                    name="extraImage"
                    onchange="fileOnChangeHandler(event, this)"
                    accept="image/png, image/jpeg"/>
            </div>
        </div>
    `;

    imagesDiv.append(html);
}

function removeExtraImageSection(id) {
    $("#extraImageHeader" + id).parent().remove();
    decreaseCountForExtraImages(id);
}

function decreaseCountForExtraImages(id) {
    $("#images-div").children("div").each((index, item) => {
        $(item).find("label").text("Extra Image #" + (index));
    });
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