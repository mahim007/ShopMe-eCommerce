$(document).ready(function () {
    $("[name='keyword']").attr("placeholder", "Type #number to search by order id");
    $(".link-detail").on("click", handleClickOnLinkDetails);
    $(".icon-dark").on("click", handleUpdateStatus);
});

function clearFilter(){
    window.location = "[[@{/orders}]]";
}

function handleClickOnLinkDetails(e){
    e.preventDefault();
    let linkDetailURL = $(this).attr("href");
    $("#customerDetailModal").modal("show").find(".modal-content").load(linkDetailURL);
}

function handleUpdateStatus(e) {
    let statusIcon = $(this);
    let data = statusIcon.attr("value");
    let status = data.split("--")[0];
    let orderId = data.split("--")[1];
    let url = contextPath + `orders/update/${orderId}/${status}`;

    showModalDialog("Update Confirmation", `Do you want to update the status for this order ID #${orderId} to ${status}?`);
    $("#modalYesBtn").click(function (e) {
        e.preventDefault();
        $("#shopmeModal").modal('toggle');
        updateStatus(url, statusIcon);
    });
}

function updateStatus(url, element) {
    fetch(url, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        }
    })
        .then(res => res.json())
        .then(data => {
            showInfoModalDialog("Acknowledgement", "Successfully updated the status.");
            element.addClass("icon-green");
            element.removeClass("icon-dark");
            element.off("click");
        })
        .catch(error => {
            showInfoModalDialog("Error!", "Failed to update the status.")
        });
}

function showModalDialog(title, message) {
    $("#shopmeModalTitle").text(title);
    $("#shopmeModalBody").html(message);
    $("#shopmeModal").modal('toggle');
}

function showInfoModalDialog(title, message) {
    $("#shopmeInfoModalTitle").text(title);
    $("#shopmeInfoModalBody").html(message);
    $("#shopmeInfoModal").modal('toggle');
}