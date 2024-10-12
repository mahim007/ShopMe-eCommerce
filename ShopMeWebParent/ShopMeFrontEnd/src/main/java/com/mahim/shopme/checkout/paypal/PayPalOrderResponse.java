package com.mahim.shopme.checkout.paypal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class PayPalOrderResponse {
    private String id;
    private String status;

    public boolean validate(String orderId) {
        return id.equals(orderId) && status.equals("COMPLETED");
    }
}
