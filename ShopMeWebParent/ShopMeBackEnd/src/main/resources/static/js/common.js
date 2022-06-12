$(document).ready(function () {
    $('#logoutLink').on('click', function (e) {
        e.preventDefault();
        document.LogoutForm.submit();
    })
});

function showModalDialog(title, message) {
    $("#shopmeModalTitle").text(title);
    $("#shopmeModalBody").html(message);
    let myModal = new bootstrap.Modal($("#shopmeModal"), {
        keyboard: false
    });
    myModal.show();
}