package com.mahim.shopme.customer;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.oauth.CustomerOAuth2User;
import com.mahim.shopme.security.CustomerUserDetails;
import com.mahim.shopme.setting.EmailSettingBag;
import com.mahim.shopme.setting.SettingService;
import com.mahim.shopme.utils.EmailUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.mahim.shopme.utils.EmailUtils.getEmailFromAuthenticatedCustomer;

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

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request) {
        String email = getEmailFromAuthenticatedCustomer(request);
        Optional<Customer> customerOptional = customerService.getCustomerByEmail(email);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            model.addAttribute("customer", customer);
            model.addAttribute("countries", customerService.listAllCountries());
            model.addAttribute("pageTitle", "Your Account Details");
        }
        return "customer/account_form";
    }



    @PostMapping("/update_account_details")
    public String updateAccountDetails(@ModelAttribute("customer") Customer customer, HttpServletRequest request, RedirectAttributes ra) {
        customerService.update(customer);
        ra.addFlashAttribute("message", "Your account details have been updated");
        updateNameForAuthenticatedCustomer(customer, request);
        String redirectURL = "redirect:/account_details";

        String optionalRedirect = request.getParameter("redirect");
        if ("address_book".equals(optionalRedirect)) {
            redirectURL =  "redirect:/address_book";
        } else if ("cart".equals(optionalRedirect)) {
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(optionalRedirect)) {
            redirectURL =  "redirect:/address_book?redirect=checkout";
        }

        return redirectURL;
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String fullName = customer.getFirstName() + " " + customer.getLastName();
        getCustomerUserDetails(principal);

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails userDetails = getCustomerUserDetails(principal);
            Customer authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User)oAuth2Token.getPrincipal();
            oAuth2User.setFullName(fullName);
        }
    }

    private CustomerUserDetails getCustomerUserDetails(Principal principal) {
        CustomerUserDetails customerUserDetails = null;

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
            customerUserDetails = (CustomerUserDetails) authenticationToken.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken authenticationToken = (RememberMeAuthenticationToken) principal;
            customerUserDetails = (CustomerUserDetails) authenticationToken.getPrincipal();
        }

        return customerUserDetails;
    }
}
