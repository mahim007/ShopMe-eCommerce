package com.mahim.shopme.common.entity;

import com.mahim.shopme.common.enums.AuthenticationType;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Customer extends AbstractAddress {

    @Column(unique = true, nullable = false, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type")
    private AuthenticationType authenticationType;

    @Column(name = "created_time")
    private Date createdTime;

    @Column
    private boolean enabled;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "reset_password_token", length = 30, nullable = true)
    private String resetPasswordToken;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getAddress() {
        String address = firstName;
        if (lastName != null && !lastName.isEmpty()) address += " " + lastName;
        if (addressLine1 != null && !addressLine1.isEmpty()) address += ", " + addressLine1;
        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
        if (city != null && !city.isEmpty()) address += ", " + city;
        if (state != null && !state.isEmpty()) address += ", " + state;
        address += ", " + country.getName();
        if (postalCode != null && !postalCode.isEmpty()) address += ", Postal Code: " + postalCode;
        if (phoneNumber != null && !phoneNumber.isEmpty()) address += ", Phone Number: " + phoneNumber;
        return address;
    }
}
