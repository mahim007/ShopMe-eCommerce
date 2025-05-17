var returnModal;
var modalTitle;

function handleReturnOrder(link) {
    const orderId = link.attr('orderId');
    returnModal.modal('show');
    modalTitle.text("Return Order ID #" + orderId);
    $("#returnOrderModalOrderId").val(orderId);
}

$(document).ready(function(){
    returnModal = $('#returnOrderModal');
    modalTitle = $('#returnOrderModalTitle');

    $('.return-order').on('click', function(e) {
        e.preventDefault();
        handleReturnOrder($(this));
    });
});

function submitReturnOrderForm() {
    const orderId = $("#returnOrderModalOrderId").val();
    const reason = $("input[name='returnReason']:checked").val();
    const note = $("#returnNote").val();

    console.log(orderId, reason , note);
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
            console.log(data);
        })
        .catch(err => console.error(err));
}