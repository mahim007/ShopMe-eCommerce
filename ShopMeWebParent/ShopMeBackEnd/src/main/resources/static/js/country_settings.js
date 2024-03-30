let loadCountriesBtn;
let loadCountriesSelect;

$(document).ready(function () {
    loadCountriesBtn = $("#loadCountriesBtn");
    loadCountriesSelect = $("#loadCountriesSelect");

    loadCountriesBtn.click(function () {
        loadCountries();
    });
});

function loadCountries() {
    console.log("loading countries...");
    let url = contextPath + "countries/list";

    fetch(url)
        .then(res => res.json())
        .then(data => {
            loadCountriesSelect.empty();
            try {
                data.forEach(item => {
                    let optionValue = item.id + "-" + item.code;
                    $("<option>").val(optionValue).text(item.name).appendTo(loadCountriesSelect);
                });
                loadCountriesBtn.val("Refresh Country List");
                return true;
            } catch (e) {
                return false;
            }
        })
        .then(status => {
            if (status) {
                showToastMessage("All countries have been loaded.");
            } else {
                showToastMessage("Error occurred while loading countries.");
            }
        })
        .catch(error => showToastMessage("Error occurred while refreshing countries."));
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast("show");
}