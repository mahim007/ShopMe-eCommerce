package com.mahim.shopme.common.entity;

import com.mahim.shopme.common.enums.AuthenticationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "first_name" ,nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 45)
    private String phoneNumber;

    @Column(name = "address_line1", nullable = false, length = 64)
    private String addressLine1;

    @Column(name = "address_line2", nullable = true, length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 45)
    private String state;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type")
    private AuthenticationType authenticationType;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "created_time")
    private Date createdTime;

    @Column
    private boolean enabled;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
