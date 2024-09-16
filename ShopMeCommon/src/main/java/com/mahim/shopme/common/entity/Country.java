package com.mahim.shopme.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Country extends ParentEntity {

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 5)
    private String code;

    @OneToMany(mappedBy = "country")
    @JsonIgnore
    private Set<State> states;

    public Country(String name) {
        this.name = name;
    }

    public Country(Integer id) {
        this.id = id;
    }

    public Country(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
