package com.mahim.shopme.checkout.paypal;

import com.mahim.shopme.common.exception.PayPalApiException;
import com.mahim.shopme.setting.PaymentSettingBag;
import com.mahim.shopme.setting.SettingService;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PayPalService {

    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    private final SettingService settingService;
    private final RestTemplate restTemplate;

    public PayPalService(SettingService settingService, RestTemplate restTemplate) {
        this.settingService = settingService;
        this.restTemplate = restTemplate;
    }

    public boolean validateOrder(String orderId) throws PayPalApiException {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String baseURL = paymentSettings.getURL();
        String requestUrl = baseURL + GET_ORDER_API + orderId;

        String clientID = paymentSettings.getClientID();
        String clientSecret = paymentSettings.getClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en-US");
        headers.setBasicAuth(clientID, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
        HttpStatus statusCode = response.getStatusCode();

        switch (statusCode) {
            case OK: {
                PayPalOrderResponse orderResponse = response.getBody();
                return orderResponse.validate(orderId);
            }
            case NOT_FOUND: {
                throw new PayPalApiException("Order ID not found: " + orderId);
            }
            case BAD_REQUEST: {
                throw new PayPalApiException("Bad request: " + orderId);
            }
            case INTERNAL_SERVER_ERROR: {
                throw new PayPalApiException("Paypal server error: " + orderId);
            }
            default: {
                throw new PayPalApiException("PayPal returned non-OK status code: " + statusCode);
            }
        }
    }

}
