var returnModal;
var modalTitle;
var fieldNote;
var divReason;
var divMessage;
var firstButton;
var secondButton;

function handleReturnOrder(link) {
    const orderId = link.attr('orderId');
    returnModal.modal('show');
    modalTitle.text("Return Order ID #" + orderId);
    $("#returnOrderModalOrderId").val(orderId);
}

$(document).ready(function(){
    returnModal = $('#returnOrderModal');
    modalTitle = $('#returnOrderModalTitle');
    fieldNote = $('#returnNote');
    divReason = $('#divReason');
    divMessage = $('#divMessage');
    firstButton = $('#firstButton');
    secondButton = $('#secondButton');

    divReason.show();
    divMessage.hide();
    firstButton.show();
    secondButton.text("Cancel");
    fieldNote.val("");

    $('.return-order').on('click', function(e) {
        e.preventDefault();
        handleReturnOrder($(this));
    });
});

function submitReturnOrderForm() {
    const orderId = $("#returnOrderModalOrderId").val();
    const reason = $("input[name='returnReason']:checked").val();
    const note = $("#returnNote").val();

    if (orderId && reason && note) {
        postReturnRequested(orderId, reason, note);
    }
    return false;
}

function postReturnRequested(orderId, reason, note) {
    const url = contextPath + "orders/return";
    const orderReturnRequest = {
        orderId: orderId,
        reason: reason,
        note: note
    };

    fetch(url, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfHeaderValue
        },
        body: JSON.stringify(orderReturnRequest)
    })
        .then(res => res.json())
        .then(data => {
            showMessageModal("Return request has been sent");
            updateStatusTextAndHideReturnButton(data.orderId);
        })
        .catch(err => showMessageModal(err));
}

function showMessageModal(message) {
    divMessage.text(message);
    divMessage.show();
    divReason.hide();
    firstButton.hide();
    secondButton.text("Close");
}

function updateStatusTextAndHideReturnButton(orderId) {
    $(".textOrderStatus-" + orderId).text("RETURN_REQUESTED");
    $(".return-order-" + orderId).hide();
}