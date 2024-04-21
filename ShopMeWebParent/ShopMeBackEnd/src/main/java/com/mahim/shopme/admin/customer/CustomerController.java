package com.mahim.shopme.admin.customer;

import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.Customer;
import com.mahim.shopme.common.exception.CustomerNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.mahim.shopme.admin.customer.CustomerService.CUSTOMERS_PER_PAGE;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/customers/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(
            @PathVariable int pageNo,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        keyword = keyword.trim();

        Page<Customer> customerPage = keyword.isEmpty() && keyword.isBlank() ?
                customerService.listByPage(pageNo, sortField, sortDir) :
                customerService.listByKeyword(pageNo, sortField, sortDir, keyword);

        int totalPages = customerPage.getTotalPages();
        long totalElements = customerPage.getTotalElements();
        int currentPageSize = customerPage.getNumberOfElements();
        int startNo = ((pageNo - 1) * CUSTOMERS_PER_PAGE) + 1;
        int endNo = (startNo + currentPageSize) - 1;

        List<Customer> customers = customerPage.getContent();
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("customers", customers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "customers/customers";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("customer") Customer customer, RedirectAttributes ra) {
        Customer saved = customerService.save(customer);
        ra.addFlashAttribute("message", "Customer info updated successfully");
        return getRedirectUrlForAffectedCustomer(saved);
    }

    private String getRedirectUrlForAffectedCustomer(Customer customer) {
        return "redirect:/customers/page/1?sortField=id&sortDir=asc&keyword" + customer.getEmail();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, RedirectAttributes ra, Model model) {
        try {
            Customer customerFromDB = customerService.findById(id);
            List<Country> countries = customerService.listAllCountry();

            model.addAttribute("customer", customerFromDB);
            model.addAttribute("countries", countries);
            model.addAttribute("pageTitle", "Edit Customer (ID: " + customerFromDB.getId() + ")");
            return "customers/customer_form";
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found");
        }

        return "customers/customers";
    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateEnabledStatus(@PathVariable("id") Integer id, @PathVariable("enabled") boolean enabled,
                                      RedirectAttributes ra) {
        try {
            customerService.updateEnabledStatus(id, enabled);
            ra.addFlashAttribute("message", "Enabled status updated for Customer(ID: " + id + ")");
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found with ID: " + id + ")");
        }

        return listAll();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            customerService.delete(id);
            ra.addFlashAttribute("message", "Customer deleted with ID: " + id);
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found with ID: " + id);
        }
        return listAll();
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.findById(id);
            model.addAttribute("customer", customer);
            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException e) {
            ra.addFlashAttribute("exceptionMessage", "Customer not found with ID: " + id);
        }

        return listAll();
    }
}
