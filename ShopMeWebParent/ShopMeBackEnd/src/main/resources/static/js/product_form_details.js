$(document).ready(function () {
    // todo
});

function addProductDetail(id) {
    addCrossBtn(id - 1);

    console.log("add product detail: " + id);
    let html = `
        <div id="detailItem${id}" class="row mb-3 form-inline">
            <label class="col-form-label col-sm-1">Name</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" name="detailNames" required minlength="3" maxlength="255"/>
            </div>
            <label class="col-form-label col-sm-1">Value</label>
            <div class="col-sm-5">
                <input type="text" class="form-control" name="detailValues" required minlength="3" maxlength="255"/>
            </div>
             <div class="col-sm-1 cross-btn-container"></div>
        </div>
    `;

    $("#product-details").append(html);
    $("input[name='detailNames']").last().focus();
    detailNo++;
}

function removeProductDetail(id) {
    $("#detailItem" + id).remove();
}

function addCrossBtn(id) {
    let crossBtn = `<a class="btn fas fa-times-circle fa-2x icon-dark" 
                            title="remove this detail"
                            href="javascript:removeProductDetail(${id})"
                            ></a>`;
    $("#detailItem" + id).find("div.cross-btn-container").append(crossBtn);
}