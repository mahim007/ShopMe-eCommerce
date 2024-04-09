package com.mahim.shopme.setting;

import com.mahim.shopme.common.entity.Setting;
import com.mahim.shopme.common.enums.SettingCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public List<Setting> getGeneralSetting() {
        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    public EmailSettingBag getEmailSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));
        return new EmailSettingBag(settings);
    }
}
