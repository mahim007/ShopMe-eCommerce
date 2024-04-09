let loadCountriesBtnInState;
let loadCountriesSelectInState;
let loadStatesSelect;
let addStateBtn;
let updateStateBtn;
let deleteStateBtn;
let labelStateName;
let fieldStateName;

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

function loadCountriesInStates() {
    let url = contextPath + "countries/list";

    getOrDeleteDataInStates(url, "GET", function (data) {
        loadCountriesSelectInState.empty();
        data.forEach(item => {
            let optionValue = item.id + "-" + item.code;
            $("<option>").val(optionValue).text(item.name).appendTo(loadCountriesSelectInState);
        });
        loadCountriesBtnInState.val("Refresh Country List");
        changeFormToAddInStates();
        showToastMessageInStates("All countries have been loaded.");
    }, function () {
        showToastMessageInStates("Error occurred while refreshing countries.")
    });
}

function loadStates() {
    let selected = $("#loadCountriesSelectInState option:selected");
    let countryId = selected.val().split("-")[0];
    let url = contextPath + "states/list_by_country/" + countryId;

    getOrDeleteDataInStates(url, "GET", function (data) {
        loadStatesSelect.empty();
        data.forEach(item => {
            let optionValue = item.id;
            $("<option>").val(optionValue).text(item.name).appendTo(loadStatesSelect);
        });
        changeFormToAddInStates();
        showToastMessageInStates(`All states of ${selected.text()} have been loaded.`);
    }, function () {
        showToastMessageInStates("Error occurred while refreshing states.")
    })
}

function showToastMessageInStates(message) {
    $("#toastMessage").text(message);
    $(".toast").toast("show");
}

function changeFormToSelectedInStates() {
    addStateBtn.prop("value", "New");
    updateStateBtn.prop("disabled", false);
    deleteStateBtn.prop("disabled", false);

    let selected = $("#loadStatesSelect option:selected");
    fieldStateName.val(selected.text()).focus();
    labelStateName.text("Selected State");
}

function changeFormToNewInStates() {
    addStateBtn.val("Add");
    labelStateName.text("State Name");
    updateStateBtn.prop("disabled", true);
    deleteStateBtn.prop("disabled", true);
    fieldStateName.val("").focus();
}

function changeFormToAddInStates() {
    addStateBtn.val("New");
    labelStateName.text("State Name");
    updateStateBtn.prop("disabled", true);
    deleteStateBtn.prop("disabled", true);
    fieldStateName.val("");
}

function selectNewlyAddedState(id, name) {
    $("<option>").val(id).text(name).appendTo(loadStatesSelect);
    $(`#loadStatesSelect option[value='${id}']`).prop("selected", true);
    changeFormToSelectedInStates();
}

function postDataInStates(url, requestObject, successCallback, errorCallback) {
    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        },
        body: JSON.stringify(requestObject)
    })
        .then(res => res.json())
        .then(data => {
            successCallback(data);
        })
        .catch(error => errorCallback())
}

function validateFormState() {
    let formState = document.getElementById("formState");
    if (!formState.checkValidity()) {
        formState.reportValidity();
        return false;
    }

    return true;
}

function addState() {
    if (!validateFormState()) return;

    let selected = $("#loadCountriesSelectInState option:selected");
    let country = {
        id: selected.val().split("-")[0],
        name: selected.text()
    };
    let url = contextPath + "states/save";
    let state = {
        name: fieldStateName.val(),
        country: country
    };

    postDataInStates(url, state, function (data) {
        selectNewlyAddedState(data, state.name);
        showToastMessageInStates("Successfully saved state.");
    }, function () {
        showToastMessageInStates("Could not save state.");
    });
}

function updateState() {
    if (!validateFormState()) return;

    let url = contextPath + "states/save";
    let selectedCountry = $("#loadCountriesSelectInState option:selected");
    let selectedState = $("#loadStatesSelect option:selected");

    let country = {
        id: selectedCountry.val().split("-")[0],
        name: selectedCountry.text(),
    };

    let state = {
        id: selectedState.val(),
        name: fieldStateName.val(),
        country
    }

    postDataInStates(url, state, function (data) {
        selectedState.val(state.id);
        selectedState.text(state.name);
        changeFormToSelectedInStates();
        showToastMessageInStates("Successfully updated state");
    }, function () {
        showToastMessageInStates("Could not update state.");
    });
}

function deleteState() {
    let selectedState = $("#loadStatesSelect option:selected");
    let id = selectedState.val();
    let url= contextPath + "states/delete/" + id;

    getOrDeleteDataInStates(url, "DELETE", function () {
        $(`#loadStatesSelect option[value='${id}']`).remove();
        showToastMessageInStates("The state has been deleted.");
        changeFormToAddInStates();
    }, function () {
        showToastMessageInStates("Could not delete state.");
    })
}

$(document).ready(function () {
    loadCountriesBtnInState = $("#loadCountriesBtnInState");
    loadCountriesSelectInState = $("#loadCountriesSelectInState");
    loadStatesSelect = $("#loadStatesSelect");
    addStateBtn = $("#addStateBtn");
    updateStateBtn = $("#updateStateBtn");
    deleteStateBtn = $("#deleteStateBtn");
    labelStateName = $("#labelStateName");
    fieldStateName = $("#fieldStateName");

    loadCountriesBtnInState.click(function () {
        loadCountriesInStates();
    });

    loadCountriesSelectInState.on("change", function (event) {
        loadStates();
    });

    loadStatesSelect.on("change", function (event) {
        changeFormToSelectedInStates();
    });

    addStateBtn.click(function (event) {
        if (addStateBtn.val() === "New") {
            changeFormToNewInStates();
        } else {
            addState();
        }
    });

    updateStateBtn.click(function (event) {
        updateState();
    });

    deleteStateBtn.click(function (event) {
        deleteState();
    })
});