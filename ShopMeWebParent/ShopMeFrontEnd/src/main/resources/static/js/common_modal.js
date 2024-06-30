function showModalDialog(title, message) {
    $("#shopmeModalTitle").text(title);
    $("#shopmeModalBody").html(message);
    let myModal = new bootstrap.Modal($("#shopmeModal"), {
        keyboard: false
    });
    myModal.show();
}