package com.mahim.shopme.admin.setting;

import com.mahim.shopme.common.entity.Setting;
import com.mahim.shopme.common.util.SettingBag;

import java.util.List;

public class GeneralSettingBag extends SettingBag {

    public GeneralSettingBag(List<Setting> settings) {
        super(settings);
    }

    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }

    public void updateSiteLogo(String value) {
        super.update("SITE_LOGO", value);
    }
}
