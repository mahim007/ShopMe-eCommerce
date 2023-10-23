$(document).ready(function () {
    $("input.extraImage").change(function (e) {
        fileOnChangeHandler(e, this);
    });

    $("a[name='removeExtraImage']").click(function (e) {
        let id = $(e.target).data("id");
        removeExtraImageSection(id);
    });
});

function fileOnChangeHandler(e, inputObject) {
    let name = inputObject.id;
    let num = name ? name[name.length - 1] : "1"
    let thumbnailId = "extraThumbnail" + num;
    let defaultThumbnail = $(thumbnailId).attr("src");
    showExtraThumbnailImage(inputObject, defaultThumbnail, thumbnailId);
}

function showExtraThumbnailImage(fileInput, userDefaultImage, thumbnailId) {
    let file = fileInput.files[0];
    let fileReader = new FileReader();

    let fileName = file.name;
    let imageNameHiddenField = $("#imageName" + thumbnailId[thumbnailId.length - 1]);
    if (imageNameHiddenField.length) {
        imageNameHiddenField.val(fileName);
    }

    fileReader.onload = function (e) {
        $(`#${thumbnailId}`).attr("src", e.target.result);
    };

    try {
        let fileSize = fileInput.files[0].size;
        if (fileSize > IMAGE_MAX_SIZE) {
            fileInput.setCustomValidity("Image must be less than 1 MB");
            fileInput.reportValidity();
        } else {
            fileReader.readAsDataURL(file);

            if (extraImageNo === Number(thumbnailId[thumbnailId.length - 1])) {
                addCrossBtnForImages(extraImageNo);
                showExtraImageSection();
            }
        }
    } catch (err) {
        $(`#${thumbnailId}`).attr("src", userDefaultImage);
    }
}

function addCrossBtnForImages(id) {
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
        if (index > 0) {
            $(item).find("label").text("Extra Image #" + (index));
        }
    });
}