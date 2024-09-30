package com.mahim.shopme.setting;

import com.mahim.shopme.common.entity.Currency;
import com.mahim.shopme.common.entity.Setting;
import com.mahim.shopme.common.enums.SettingCategory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingService {

    private final SettingRepository settingRepository;
    private final CurrencyRepository currencyRepository;

    public SettingService(SettingRepository settingRepository, CurrencyRepository currencyRepository) {
        this.settingRepository = settingRepository;
        this.currencyRepository = currencyRepository;
    }

    public List<Setting> getGeneralSetting() {
        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    public EmailSettingBag getEmailSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));
        return new EmailSettingBag(settings);
    }

    public CurrencySettingBag getCurrencySettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        return new CurrencySettingBag(settings);
    }

    public PaymentSettingBag getPaymentSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
        return new PaymentSettingBag(settings);
    }

    public String getCurrencyCode() {
        Optional<Setting> optional = settingRepository.findByKey("CURRENCY_ID");
        if (optional.isPresent()) {
            Setting setting = optional.get();
            Integer currencyId = Integer.parseInt(setting.getValue());
            Currency currency = currencyRepository.findById(currencyId).get();
            return currency.getCode();
        } else {
            return null;
        }
    }
}
