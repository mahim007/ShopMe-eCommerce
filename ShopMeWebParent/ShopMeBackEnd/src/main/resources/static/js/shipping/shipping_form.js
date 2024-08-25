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
    let url = contextPath + "states/list_by_country/" + countryId;

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