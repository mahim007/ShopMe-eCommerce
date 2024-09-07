$(document).ready(function () {
    let userDefaultImage = $("#thumbnail").attr("src");

    $("#cancelForm").on("click", function () {
        window.location = moduleURL;
    });

    $("#fileImage").change(function (e) {
        showImageThumbnail(this, userDefaultImage);
    });
});

function showImageThumbnail(fileInput, userDefaultImage) {
    let file = fileInput.files[0];
    let fileReader = new FileReader();

    fileReader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    };

    try {
        let fileSize = fileInput.files[0].size;
        if (fileSize > (1024 * 1024)) {
            fileInput.setCustomValidity("Image must be less than 1 MB");
            fileInput.reportValidity();
        } else {
            fileReader.readAsDataURL(file);
        }
    } catch (err) {
        $("#thumbnail").attr("src", userDefaultImage);
    }
}

function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value !== $('#password').val()) {
        confirmPassword.setCustomValidity('Password do not match');
    } else {
        confirmPassword.setCustomValidity('');
    }
}