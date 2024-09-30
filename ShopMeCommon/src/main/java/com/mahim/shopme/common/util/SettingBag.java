package com.mahim.shopme.common.util;

import com.mahim.shopme.common.entity.Setting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingBag {
    private List<Setting> settings;

    private Setting get(String key) {
        int index = settings.indexOf(new Setting(key));
        return index >= 0 ? settings.get(index) : null;
    }

    public String getValue(String key) {
        int index = settings.indexOf(new Setting(key));
        return index >= 0 ? settings.get(index).getValue() : null;
    }

    public void update(String key, String value) {
        Setting setting = get(key);
        if (setting != null && setting.getValue() != null) {
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return this.settings;
    }
}
