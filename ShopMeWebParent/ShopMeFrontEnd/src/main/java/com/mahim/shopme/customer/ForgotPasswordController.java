package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.setting.EmailSettingBag;
import com.mahim.shopme.setting.SettingService;
import com.mahim.shopme.utils.EmailUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    private final CustomerService customerService;
    private final SettingService settingService;

    public ForgotPasswordController(CustomerService customerService, SettingService settingService) {
        this.customerService = customerService;
        this.settingService = settingService;
    }

    @GetMapping("/forgot_password")
    public String forgotPassword() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String forgotPasswordSubmit(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = EmailUtils.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, link);
            model.addAttribute("message", "We have sent a reset password link to your email.");
        } catch (CustomerNotFoundException | MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }

        return "customer/forgot_password_form";
    }

    private void sendEmail(String email, String link) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = EmailUtils.prepareMailSender(emailSettings);

        String emailSubject = "Customer Reset Password";
        String emailContent = "<p>Hello,</p>" +
                "<p>You have requested to reset your password.</p>" +
                "<p>Click the link below to reset your password.</p>" +
                "<p><a href=\"" + link + "\">Change my password</a></p>" +
                "<br>" +
                "<p>Ignore this email if you do remember your password, " +
                "or you have not made the request.</p>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(emailSubject);
        mimeMessageHelper.setText(emailContent, true);

        mailSender.send(mimeMessage);
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Customer customer = customerService.getByResetPasswordToken(token);
        if (customer != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("exceptionMessage", "Invalid Token.");
        }
        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        try {
            customerService.updatePassword(token, password);

            model.addAttribute("pageTitle", "Reset your password");
            model.addAttribute("title", "Reset your password");
            model.addAttribute("message", "You have successfully changed your password.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("exceptionMessage", "Invalid token");
            model.addAttribute("pageTitle", "Invalid token");
        }

        return "message";
    }
}
