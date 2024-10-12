package com.mahim.shopme.checkout;

import com.mahim.shopme.address.AddressService;
import com.mahim.shopme.checkout.paypal.PayPalService;
import com.mahim.shopme.common.entity.*;
import com.mahim.shopme.common.enums.PaymentMethod;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import com.mahim.shopme.common.exception.PayPalApiException;
import com.mahim.shopme.customer.CustomerService;
import com.mahim.shopme.order.OrderService;
import com.mahim.shopme.setting.CurrencySettingBag;
import com.mahim.shopme.setting.EmailSettingBag;
import com.mahim.shopme.setting.PaymentSettingBag;
import com.mahim.shopme.setting.SettingService;
import com.mahim.shopme.shipping.ShippingRateService;
import com.mahim.shopme.shoppingcart.ShoppingCartService;
import com.mahim.shopme.utils.CurrencyUtils;
import com.mahim.shopme.utils.EmailUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final  CheckoutService checkoutService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final ShoppingCartService shoppingCartService;
    private final OrderService orderService;
    private final SettingService settingService;
    private final PayPalService payPalService;

    public CheckoutController(CheckoutService checkoutService, CustomerService customerService, AddressService addressService,
                              ShippingRateService shippingRateService, ShoppingCartService shoppingCartService,
                              OrderService orderService, SettingService settingService, PayPalService payPalService) {
        this.checkoutService = checkoutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.settingService = settingService;
        this.payPalService = payPalService;
    }

    @GetMapping("")
    public String showCheckoutPage(HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getAuthenticatedCustomer(request);
            Address address = addressService.getDefaultShippingAddress(customer);

            ShippingRate shippingRate;

            if (address != null) {
                model.addAttribute("shippingAddress", address.toString());
                shippingRate = shippingRateService.getShippingRateForAddress(address);
            } else {
                model.addAttribute("shippingAddress", customer.getAddress());
                shippingRate = shippingRateService.getShippingRateForCustomer(customer);
            }

            if (shippingRate == null) {
                return "redirect:/cart";
            }

            List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
            CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
            String currencyCode = settingService.getCurrencyCode();
            PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
            String paypalClientID = paymentSettings.getClientID();

            model.addAttribute("customer", customer);
            model.addAttribute("checkoutInfo", checkoutInfo);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("currencyCode", currencyCode);
            model.addAttribute("paypalClientID", paypalClientID);

        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
        }

        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request, RedirectAttributes ra) {
        try {
            String paymentType = request.getParameter("paymentMethod");
            PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

            Customer customer = customerService.getAuthenticatedCustomer(request);
            Address address = addressService.getDefaultShippingAddress(customer);

            ShippingRate shippingRate;

            if (address != null) {
                shippingRate = shippingRateService.getShippingRateForAddress(address);
            } else {
                shippingRate = shippingRateService.getShippingRateForCustomer(customer);
            }

            if (shippingRate == null) {
                return "redirect:/cart";
            }

            List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
            CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

            Order createdOrder = orderService.createOrder(customer, address, cartItems, paymentMethod, checkoutInfo);
            shoppingCartService.deleteByCustomer(customer);
            sendOrderConfirmationEmail(createdOrder, request);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
            return "error/404";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("exceptionMessage", "Something went wrong while placing your order");
            return "error/500";
        } catch (MessagingException | UnsupportedEncodingException e) {
            ra.addFlashAttribute("exceptionMessage", "Something went wrong while sending order confirmation email");
            return "error/500";
        }

        return "checkout/order_completed";
    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model, RedirectAttributes ra) {
        try {
            String orderId = request.getParameter("orderId");
            if (payPalService.validateOrder(orderId)) {
                return placeOrder(request, ra);
            } else {
                ra.addFlashAttribute("exceptionMessage", "Invalid order id: " + orderId);
                model.addAttribute("pageTitle", "Checkout Failure");
                model.addAttribute("title", "Checkout Failure");
                model.addAttribute("exceptionMessage", "ERROR: Transaction could not be completed because order information is invalid");
            }
        } catch (PayPalApiException e) {
            model.addAttribute("pageTitle", "Checkout Failure");
            model.addAttribute("title", "Checkout Failure");
            model.addAttribute("exceptionMessage", "ERROR: Transaction failed due to error: "  + e.getMessage());
        }

        return "message";
    }

    private void sendOrderConfirmationEmail(Order order, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        CurrencySettingBag currencySettings = settingService.getCurrencySettings();

        String toAddress = order.getCustomer().getEmail();
        String emailSubject = emailSettings.getOrderConfirmationSubject();
        String emailContent = emailSettings.getOrderConfirmationContent();

        emailSubject = emailSubject.replace("[[orderId]]", String.valueOf(order.getId()));
        emailContent = emailContent.replace("[[name]]", order.getCustomer().getFullName());
        emailContent = emailContent.replace("[[orderId]]", String.valueOf(order.getId()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy  HH:mm:ss");
        String formatted = dateFormat.format(order.getOrderTime());
        emailContent = emailContent.replace("[[orderTime]]", formatted);
        emailContent = emailContent.replace("[[shippingAddress]]", order.getShippingAddress());

        String formattedCurrency = CurrencyUtils.formatCurrency(order.getTotal(), currencySettings);
        emailContent = emailContent.replace("[[total]]", formattedCurrency);
        emailContent = emailContent.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

//        String verifyURL = EmailUtils.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
//        emailContent = emailContent.replace("[[URL]]", verifyURL);

        JavaMailSenderImpl mailSender = EmailUtils.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("UTF-8");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());

        mimeMessageHelper.setTo(toAddress);
        mimeMessageHelper.setSubject(emailSubject);
        mimeMessageHelper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }
}
