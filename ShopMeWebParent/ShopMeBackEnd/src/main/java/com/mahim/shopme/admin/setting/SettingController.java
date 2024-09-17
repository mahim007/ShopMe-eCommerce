package com.mahim.shopme.admin.setting;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.currency.CurrencyRepository;
import com.mahim.shopme.common.entity.Currency;
import com.mahim.shopme.common.entity.Setting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.mahim.shopme.common.util.StaticPathUtils.SITE_LOGO_DIR;

@Controller
@RequestMapping("/settings")
public class SettingController {

    private final SettingService settingService;
    private final CurrencyRepository currencyRepository;


    public SettingController(SettingService settingService, CurrencyRepository currencyRepository) {
        this.settingService = settingService;
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("")
    public String listAll(Model model) {
        List<Setting> settings = settingService.listAllSettings();
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

        for (Setting setting : settings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }
        model.addAttribute("currencies", currencies);

        return "settings/settings";
    }

    @PostMapping("/save_general")
    public String saveGeneralSetting(
            @RequestParam(name = "SITE_LOGO") MultipartFile multipartFile,
            HttpServletRequest request,
            RedirectAttributes ra
    ) {
        try {
            GeneralSettingBag settingBag = settingService.getGeneralSetting();
            saveSiteLogo(multipartFile, ra, settingBag);
            saveCurrencySymbol(request, settingBag);
            updateSettingValuesFromForm(request,settingBag.list());
            ra.addFlashAttribute("message", "General settings have been saved.");
        } catch (IOException e) {
            ra.addFlashAttribute("exceptionMessage", "General settings could not be saved.");
        }

        return "redirect:/settings";
    }

    private static void saveSiteLogo(MultipartFile multipartFile, RedirectAttributes ra, GeneralSettingBag generalSetting) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            String uploadDir = SITE_LOGO_DIR;
            generalSetting.updateSiteLogo(value);

            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag bag) {
        int currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> optionalCurrency = currencyRepository.findById(currencyId);

        if (optionalCurrency.isPresent()) {
            Currency currency = optionalCurrency.get();
            bag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> settings) {
        for (Setting setting : settings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }

        settingService.saveAll(settings);
    }

    @PostMapping("/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> mailServerSettings = settingService.getMailServerSettings();
        updateSettingValuesFromForm(request, mailServerSettings);
        ra.addFlashAttribute("message", "Mail server settings have been saved.");
        return "redirect:/settings";
    }

    @PostMapping("/save_mail_templates")
    public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
        updateSettingValuesFromForm(request, mailTemplateSettings);
        ra.addFlashAttribute("message", "Mail template settings have been saved.");
        return "redirect:/settings";
    }

    @PostMapping("/save_payment")
    public String savePaymentSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> paymentSettings = settingService.getPaymentSettings();
        updateSettingValuesFromForm(request, paymentSettings);
        ra.addFlashAttribute("message", "Payment settings have been saved.");
        return "redirect:/settings";
    }
}
