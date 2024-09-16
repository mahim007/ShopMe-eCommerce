package com.mahim.shopme.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter @Setter
public abstract class AbstractAddress extends ParentEntity {

    @Column(name = "first_name" ,nullable = false, length = 45)
    protected String firstName;

    @Column(name = "last_name" ,nullable = false, length = 45)
    protected String lastName;

    @Column(name = "phone_number" ,nullable = false, length = 45)
    protected String phoneNumber;

    @Column(name = "address_line1", nullable = false, length = 64)
    protected String addressLine1;

    @Column(name = "address_line2", nullable = false, length = 64)
    protected String addressLine2;

    @Column(nullable = false, length = 45)
    protected String city;

    @Column(nullable = false, length = 45)
    protected String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    protected String postalCode;
}
