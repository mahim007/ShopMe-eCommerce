console.log("hello from add track.js");

$(document).ready(function(){
    $("#track").on("click", "#linkAddTrack", function(e){
        e.preventDefault();
        addTrack();
    });
});

function addTrack() {
    console.log("inside add track method");
    let htmlCode = generateTrackCode();
    $("#trackList").append(htmlCode);
}

function generateTrackCode() {
    let nextCount = $("#trackList .hiddenTrackId").length + 1;
    let blankLineId = "blankline-" + nextCount;

    return `
        <div class="border rounded p-1" id="row-${nextCount}">
            <div class="row">
                <input class="hiddenTrackId" type="hidden" name="trackId" value="${nextCount}" />
                <div class="col-1 col-md-1 col-lg-1">
                    <div class="divCount">${nextCount}</div>
                    <div><a class="fas fa-trash icon-danger linkRemove" href="" rowNumber="${nextCount}"></a></div>
                </div>
            </div>
            <div class="row">
                <div class="col-10 col-md-7 col-lg-8">
                    <table>
                        <tr>
                            <td>Time: </td>
                            <td>
                                <input type="datetime-local" required class="form-control m-1" rowNumber="${nextCount}" />
                            </td>
                        </tr>
                        <tr>
                            <td>Status: </td>
                            <td>
                                <select class="form-select m-1" required="" id="status" name="status">
                                    <option value="NEW" selected="selected">NEW</option>
                                    <option value="CANCELLED">CANCELLED</option>
                                    <option value="PROCESSING">PROCESSING</option>
                                    <option value="PACKAGED">PACKAGED</option>
                                    <option value="PICKED">PICKED</option>
                                    <option value="SHIPPING">SHIPPING</option>
                                    <option value="DELIVERED">DELIVERED</option>
                                    <option value="RETURNED">RETURNED</option>
                                    <option value="PAID">PAID</option>
                                    <option value="REFUNDED">REFUNDED</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Notes: </td>
                            <td>
                                 <textarea class="form-control m-1" required rows="3"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <div class="row m-1" id="${blankLineId}">&nbsp;</div>
    `;
}