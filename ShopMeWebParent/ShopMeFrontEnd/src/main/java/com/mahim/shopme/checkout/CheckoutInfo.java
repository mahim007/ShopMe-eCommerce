package com.mahim.shopme.checkout;

import lombok.*;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class CheckoutInfo {
    private float productCost;
    private float productTotal;
    private float shippingCostTotal;
    private float paymentTotal;
    private int deliverDays;
    private Date deliverDate;
    private boolean codSupported;

    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);
        return calendar.getTime();
    }

    public String getPaymentTotal4PayPal() {
        DecimalFormat df = new DecimalFormat("###,###.##");
        return df.format(paymentTotal);
    }
}
