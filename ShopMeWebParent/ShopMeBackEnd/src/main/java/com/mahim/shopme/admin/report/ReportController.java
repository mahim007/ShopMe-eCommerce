package com.mahim.shopme.admin.report;

import com.mahim.shopme.admin.setting.SettingService;
import com.mahim.shopme.common.entity.Setting;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final SettingService settingService;

    public ReportController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("")
    public String viewSalesReportHome(HttpServletRequest request) {
        loadCurrencySetting(request);
        return "reports/reports";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
}
