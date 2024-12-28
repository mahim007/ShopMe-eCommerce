let trackRecordCount;

$(document).ready(function(){
    let trackElm = $("#track");
    trackRecordCount = $("#trackList .hiddenTrackId").length;

    trackElm.on("click", "#linkAddTrack", function(e){
        e.preventDefault();
        addTrack();
    });

    trackElm.on("change", ".dropdownStatus", function(e){
        let dropDownList = $(this);
        let rowNumber = dropDownList.attr("rowNumber");
        let selectedOption = dropDownList.find("option:selected");
        let defaultNote = selectedOption.attr("defaultDescription");
        console.log("row: " +  rowNumber + " defaultNote: " + defaultNote);
        $("#trackNotes-" + rowNumber).text(defaultNote);
    });
});

function addTrack() {
    let htmlCode = generateTrackCode();
    $("#trackList").append(htmlCode);
}

function generateTrackCode() {
    let nextCount = trackRecordCount + 1;
    trackRecordCount++;

    let rowId = "rowTrack-" + nextCount;
    let trackNoteId = "trackNotes-" + nextCount;
    let blankLineId = "blankline-" + nextCount;
    let currentDateTime = formatCurrentDateTime();

    return `
        <div class="border rounded p-1" id="${rowId}">
            <div class="row">
                <input class="hiddenTrackId" type="hidden" name="trackId" value="0" />
                <div class="col-1 col-md-1 col-lg-1">
                    <div class="divCount">${nextCount}</div>
                    <div><a class="fas fa-trash icon-danger linkRemove" href="" rowNumber="${nextCount}"></a></div>
                </div>
            </div>
            <div class="row">
                <div class="col-10 col-md-7 col-lg-8">
                    <div class="form-group row">
                        <label class="col-form-label">Time: </label>
                        <div class="col">
                            <input type="datetime-local" name="trackDate" value="${currentDateTime}" required 
                            class="form-control m-1" />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-form-label">Status: </label>
                        <div>
                            <select name="trackStatus" class="form-control dropdownStatus" required 
                            rowNumber="${nextCount}"
                            style="max-width: 150px">` +
                                $("#trackStatusOptions").clone().html() +
                            `</select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-form-label">Notes: </label>
                        <div class="col">
                             <textarea rows="2" cols="10" name="trackNotes" id="${trackNoteId}" class="form-control m-1" required></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row m-1" id="${blankLineId}">&nbsp;</div>
    `;
}

function formatCurrentDateTime() {
    let date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();
    let hour = date.getHours();
    let minute = date.getMinutes();
    let second = date.getSeconds();

    if (month < 10) month = "0" + month;
    if (day < 10) day = "0" + day;
    if (hour < 10) hour = "0" + hour;
    if (minute < 10) minute = "0" + minute;
    if (second < 10) second = "0" + second;

    return `${year}-${month}-${day}T${hour}:${minute}:${second}`;
}