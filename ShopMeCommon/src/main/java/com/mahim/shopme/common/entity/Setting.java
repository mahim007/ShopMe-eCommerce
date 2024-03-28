package com.mahim.shopme.common.entity;

import com.mahim.shopme.common.enums.SettingCategory;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "settings")
@Data
public class Setting {

    @Id
    @Column(name = "`key`",nullable = false, length = 128)
    private String key;

    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private SettingCategory category;
}
