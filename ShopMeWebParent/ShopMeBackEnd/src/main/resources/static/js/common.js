$(document).ready(function () {
    $('#logoutLink').on('click', function (e) {
        e.preventDefault();
        document.LogoutForm.submit();
    });

    customizeTabs();
});

function showModalDialog(title, message) {
    $("#shopmeModalTitle").text(title);
    $("#shopmeModalBody").html(message);
    let myModal = new bootstrap.Modal($("#shopmeModal"), {
        keyboard: false
    });
    myModal.show();
}

function customizeTabs() {
    let url = window.location.href;
    if (url.match('#')) {
        let hash = url.split('#')[1];
        $(`.nav-tabs a[href='#${hash}']`).tab('show');
    }
}

function fetchData(url, method, successCallback, errorCallback) {
    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        }
    })
        .then(res => res.text())
        .then(text => text.length > 0 ? JSON.parse(text) : {})
        .then(data => {
            successCallback(data);
        })
        .catch(error => errorCallback());
}