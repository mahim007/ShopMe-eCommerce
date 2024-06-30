let dropdownCountry;
let listStates;

$(document).ready(function (event) {
    dropdownCountry = $("#country");
    listStates = $("#listStates");

    dropdownCountry.on("change", function (event) {
        loadStatesForCountry();
    })
});

function loadStatesForCountry() {
    let selected = $("#country option:selected");
    let countryId = selected.val();
    let url = contextPath + "settings/list_states_by_country/" + countryId;

    getOrDeleteDataInStates(url, "GET", function (data) {
        listStates.empty();
        $("#state").val("").focus();

        data.forEach(item => {
            $("<option>").val(item.name).text(item.name).appendTo(listStates);
        });
    }, function () {
        console.log("Error occurred!");
    })
}

function getOrDeleteDataInStates(url, method, successCallback, errorCallback) {
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

function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value !== $('#password').val()) {
        confirmPassword.setCustomValidity('Password do not match');
    } else {
        confirmPassword.setCustomValidity('');
    }
}

function checkEmailUnique(form) {
    let url = contextPath + "customers/check_unique_email";
    let userEmail = $("#email").val();
    let csrf = $("input[name='_csrf']").val();

    let params = {email: userEmail, _csrf: csrf};

    $.post(url, params, function (response) {
        if (response === "OK") {
            form.submit();
        } else if (response === "Duplicated") {
            showModalDialog("Warning", "The Customer Email: <b>" + userEmail + "</b> has been registered already.");
        } else {
            show("Error", "An Error occurred.");
        }
    }).fail(function () {
        showModalDialog("Error", "Could not connect to server.")
    });
    return false;
}