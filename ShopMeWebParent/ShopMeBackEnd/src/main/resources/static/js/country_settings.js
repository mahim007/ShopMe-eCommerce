let loadCountriesBtn;
let loadCountriesSelect;
let addCountryBtn;
let updateCountryBtn;
let deleteCountryBtn;
let labelCountryName;
let fieldCountryName;
let fieldCountryCode;

function getOrDeleteData(url, method, successCallback, errorCallback) {
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

function loadCountries() {
    let url = contextPath + "countries/list";

    getOrDeleteData(url, "GET", function (data) {
        loadCountriesSelect.empty();
        data.forEach(item => {
            let optionValue = item.id + "-" + item.code;
            $("<option>").val(optionValue).text(item.name).appendTo(loadCountriesSelect);
        });
        loadCountriesBtn.val("Refresh Country List");
        changeFormStateToAdd();
        showToastMessage("All countries have been loaded.");
    }, function () {
        showToastMessage("Error occurred while refreshing countries.")
    });
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast("show");
}

function changeFormStateToSelectedCountry() {
    addCountryBtn.prop("value", "New");
    updateCountryBtn.prop("disabled", false);
    deleteCountryBtn.prop("disabled", false);

    let selected = $("#loadCountriesSelect option:selected");
    fieldCountryName.val(selected.text()).focus();
    labelCountryName.text("Selected Country");
    fieldCountryCode.val(selected.val().split("-")[1]);
}

function changeFormStateToNew() {
    addCountryBtn.val("Add");
    labelCountryName.text("Country Name");
    updateCountryBtn.prop("disabled", true);
    deleteCountryBtn.prop("disabled", true);
    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}

function changeFormStateToAdd() {
    addCountryBtn.val("New");
    labelCountryName.text("Country Name");
    updateCountryBtn.prop("disabled", true);
    deleteCountryBtn.prop("disabled", true);
    fieldCountryName.val("");
    fieldCountryCode.val("");
}

function selectNewlyAddedCountry(id, name, code) {
    let value = id + "-" + code;
    $("<option>").val(value).text(name).appendTo(loadCountriesSelect);
    $(`#loadCountriesSelect option[value='${value}']`).prop("selected", true);
    changeFormStateToSelectedCountry();
}

function postData(url, country, successCallback, errorCallback) {
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        },
        body: JSON.stringify(country)
    })
        .then(res => res.json())
        .then(data => {
            successCallback(data);
        })
        .catch(error => errorCallback())
}

function validateFormCountry() {
    let formCountry = document.getElementById("formCountry");
    if (!formCountry.checkValidity()) {
        formCountry.reportValidity();
        return false;
    }

    return true;
}

function addCountry() {
    if (!validateFormCountry()) return;
    let url = contextPath + "countries/save";
    let country = {
        name: fieldCountryName.val(),
        code: fieldCountryCode.val()
    };

    postData(url, country, function (data) {
        selectNewlyAddedCountry(data, country.name, country.code);
        showToastMessage("Successfully saved country.");
    }, function () {
        showToastMessage("Could not save country.");
    });
}

function updateCountry() {
    if (!validateFormCountry()) return;

    let url = contextPath + "countries/save";
    let selected = $("#loadCountriesSelect option:selected");

    let country = {
        id: selected.val().split("-")[0],
        name: fieldCountryName.val(),
        code: fieldCountryCode.val()
    };

    postData(url, country, function (data) {
        let selected = $(`#loadCountriesSelect option:selected`);
        selected.val(country.id + "-" + country.code);
        selected.text(country.name);
        changeFormStateToSelectedCountry();
        showToastMessage("Successfully updated country");
    }, function () {
        showToastMessage("Could not update country.");
    });
}

function deleteCountry() {
    let selected = $("#loadCountriesSelect option:selected");
    let id = selected.val().split("-")[0];
    let url= contextPath + "countries/delete/" + id;

    getOrDeleteData(url, "DELETE", function () {
        $(`#loadCountriesSelect option[value='${selected.val()}']`).remove();
        showToastMessage("The country has been deleted.");
        changeFormStateToNew();
    }, function () {
        showToastMessage("Could not delete country.");
    })
}

$(document).ready(function () {
    loadCountriesBtn = $("#loadCountriesBtn");
    loadCountriesSelect = $("#loadCountriesSelect");
    addCountryBtn = $("#addCountryBtn");
    updateCountryBtn = $("#updateCountryBtn");
    deleteCountryBtn = $("#deleteCountryBtn");
    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");

    loadCountriesBtn.click(function () {
        loadCountries();
    });

    loadCountriesSelect.on("change", function (event) {
        changeFormStateToSelectedCountry();
    });

    addCountryBtn.click(function (event) {
        if (addCountryBtn.val() === "New") {
            changeFormStateToNew();
        } else {
            addCountry();
        }
    });

    updateCountryBtn.click(function (event) {
        updateCountry();
    });

    deleteCountryBtn.click(function (event) {
        deleteCountry();
    })
});