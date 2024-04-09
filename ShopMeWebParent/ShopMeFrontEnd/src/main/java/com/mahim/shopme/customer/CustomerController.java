package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.setting.EmailSettingBag;
import com.mahim.shopme.setting.SettingService;
import com.mahim.shopme.utils.EmailUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final SettingService settingService;

    public CustomerController(CustomerService customerService, SettingService settingService) {
        this.customerService = customerService;
        this.settingService = settingService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> countries = customerService.listAllCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(@ModelAttribute("customer") Customer customer, Model model, HttpServletRequest request) {
        try {
            customerService.registerCustomer(customer);
            sendVerificationEmail(request, customer);

            model.addAttribute("pageTitle", "Registration Successful");
            return "register/register_success";
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("pageTitle", "Registration Failed");
            return "register/register_success";
        }
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = EmailUtils.prepareMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String emailSubject = emailSettings.getCustomerVerifySubject();
        String emailContent = emailSettings.getCustomerVerifyContent();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        mimeMessageHelper.setTo(toAddress);
        mimeMessageHelper.setSubject(emailSubject);

        String verifyURL = EmailUtils.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
        emailContent = emailContent.replace("[[URL]]", verifyURL);
        emailContent = emailContent.replace("[[name]]", customer.getFullName());
        mimeMessageHelper.setText(emailContent, true);

        mailSender.send(mimeMessage);

        System.out.println("Username " + customer.getFullName());
        System.out.println("to address " + toAddress);
        System.out.println("from address " + emailSettings.getFromAddress());
        System.out.println("verification url: " + verifyURL);
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {
        boolean verified = customerService.verify(code);
        if (verified) {
            return "register/verify_success";
        } else {
            return "register/verify_failed";
        }
    }
}
