package com.mahim.shopme.common.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "states")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class State extends ParentEntity {

    @Column(nullable = false, length = 45)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
