package com.mahim.shopme.admin.user.controller;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.user.UserNotFoundException;
import com.mahim.shopme.admin.user.UserService;
import com.mahim.shopme.admin.user.exporter.UserCsvExporter;
import com.mahim.shopme.admin.user.exporter.UserExcelExporter;
import com.mahim.shopme.admin.user.exporter.UserPdfExporter;
import com.mahim.shopme.common.entity.Role;
import com.mahim.shopme.common.entity.User;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.mahim.shopme.admin.user.UserService.USERS_PER_PAGE;
import static com.mahim.shopme.admin.utils.StaticPathUtils.UPLOAD_DIR;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserCsvExporter csvExporter;
    private final UserExcelExporter excelExporter;
    private final UserPdfExporter pdfExporter;

    public UserController(UserService userService, UserCsvExporter csvExporter,
                          UserExcelExporter excelExporter, UserPdfExporter pdfExporter) {
        this.userService = userService;
        this.csvExporter = csvExporter;
        this.excelExporter = excelExporter;
        this.pdfExporter = pdfExporter;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/users/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(@PathVariable(name = "pageNo") int pageNo,
                             @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
                             @RequestParam(name = "keyword", defaultValue = "") String keyword,
                             Model model) {
        keyword = org.apache.commons.lang3.StringUtils.trim(keyword);
        if (org.apache.commons.lang3.StringUtils.isEmpty(keyword) || org.apache.commons.lang3.StringUtils.isBlank(keyword)) {
            keyword = null;
        }
        Page<User> userPage = keyword == null ? userService.listByPage(pageNo, sortField, sortDir) :
                userService.listByKeyword(pageNo, sortField, sortDir, keyword);

        int totalPages = userPage.getTotalPages();
        long totalElements = userPage.getTotalElements();
        int currentPageSize = userPage.getNumberOfElements();
        int startNo = ((pageNo - 1) * USERS_PER_PAGE) + 1;
        int endNo = (startNo + currentPageSize) - 1;

        List<User> userList = userPage.getContent();
        String reverseSortDir = org.apache.commons.lang3.StringUtils.equals(sortDir, "asc") ? "desc" : "asc";

        model.addAttribute("startNo", startNo);
        model.addAttribute("endNo", endNo);
        model.addAttribute("totalPageNo", totalPages);
        model.addAttribute("total", totalElements);
        model.addAttribute("currentPageNo", pageNo);
        model.addAttribute("listUsers", userList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        return "users/users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        List<Role> roles = userService.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("listRoles", roles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {

        User savedUser;
        if (!multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            savedUser = userService.save(user);
            FileUploadUtil.cleanDir(UPLOAD_DIR + "/" + savedUser.getId());
            FileUploadUtil.saveFile(UPLOAD_DIR + "/" + savedUser.getId(), fileName, multipartFile);
        } else {
            savedUser = userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return getRedirectUrlForAffectedUser(savedUser);
    }

    private String getRedirectUrlForAffectedUser(User savedUser) {
        String firstPartOfEmail = savedUser.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = userService.findUserById(id);
            List<Role> roles = userService.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("listRoles", roles);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            return "users/user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", e.getMessage());
            return listAll();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user ( id: " + id +" ) has been deleted");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "User with id: " + id + " not found.");
        }

        return listAll();
    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateEnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "enabled") boolean enabled, RedirectAttributes redirectAttributes) {
        try {
            userService.updateEnabledStatus(id, enabled);
            redirectAttributes.addFlashAttribute("message", "Enabled status updated for user (ID: "+ id + ")");
            return listAll();
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "User not found (ID: " + id + ")");
            return listAll();
        }
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        List<User> users = userService.listAll();
        csvExporter.export(users, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) {
        List<User> users = userService.listAll();
        excelExporter.export(response, users);
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) {
        List<User> users = userService.listAll();
        pdfExporter.export(response, users);
    }
}