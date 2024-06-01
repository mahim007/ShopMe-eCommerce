package com.mahim.shopme.oauth;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.enums.AuthenticationType;
import com.mahim.shopme.customer.CustomerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final CustomerService customerService;

    public OAuth2LoginSuccessHandler(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        Optional<Customer> customerByEmail = customerService.getCustomerByEmail(oAuth2User.getEmail());
        customerByEmail.ifPresent(customer -> {
            customerService.updateAuthentication(customer, getAuthenticationType(oAuth2User.getClientName()));
            oAuth2User.setFullName(customer.getFullName());
        });

        if (customerByEmail.isEmpty()) {
            String countryCode = request.getLocale().getCountry();
            customerService.addNewCustomerUponAuthLogin(oAuth2User.getName(), oAuth2User.getEmail(), countryCode, getAuthenticationType(oAuth2User.getClientName()));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        if (clientName.equalsIgnoreCase("google")) {
            return AuthenticationType.GOOGLE;
        } else if (clientName.equalsIgnoreCase("facebook")) {
            return AuthenticationType.FACEBOOK;
        } else {
            return AuthenticationType.DATABASE;
        }
    }
}
