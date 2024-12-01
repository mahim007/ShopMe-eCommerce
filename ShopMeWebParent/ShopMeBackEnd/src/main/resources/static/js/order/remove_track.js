function removeTrack(link) {
    let rowNumber = link.attr("rowNumber");
    $("#trackList #rowTrack-" + rowNumber).remove();
    $("#trackList #blankline-" + rowNumber).remove();

    $("#trackList .divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function isOnlyTrack() {
    let productCount = $("#trackList .hiddenTrackId").length;
    return productCount === 1;
}

$(document).ready(function () {
    $("#trackList").on("click", ".linkRemove", function (e) {
        e.preventDefault();
        if (isOnlyTrack()) {
            showModalDialog("Warning", "Could not remove Track. Must have at least one track.");
        } else {
            removeTrack($(this));
            updateOrderAmounts();
        }
    })
});