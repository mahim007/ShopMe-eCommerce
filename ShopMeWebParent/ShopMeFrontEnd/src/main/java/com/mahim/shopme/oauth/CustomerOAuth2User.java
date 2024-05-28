package com.mahim.shopme.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomerOAuth2User implements OAuth2User {

    private final String clientName;
    private String fullName;
    private final OAuth2User oAuth2User;

    public CustomerOAuth2User(String clientName, OAuth2User user) {
        this.clientName = clientName;
        this.oAuth2User = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getFullName() {
        return fullName != null ? fullName : oAuth2User.getAttribute("name");
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public String getClientName() {
        return clientName;
    }
}
