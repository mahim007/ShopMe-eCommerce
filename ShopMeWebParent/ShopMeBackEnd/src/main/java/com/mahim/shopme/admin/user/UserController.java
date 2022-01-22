package com.mahim.shopme.admin.user;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.common.entity.Role;
import com.mahim.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.mahim.shopme.admin.user.UserService.USERS_PER_PAGE;
import static com.mahim.shopme.admin.utils.StaticPathUtils.UPLOAD_DIR;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String listAll() {
        return "redirect:/users/page/1";
    }

    @GetMapping("/page/{pageNo}")
    public String listByPage(@PathVariable(name = "pageNo") int pageNo, @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir, Model model) {
        System.out.println("sort field: " + sortField);
        System.out.println("sort order: " + sortDir);
        Page<User> userPage = userService.listByPage(pageNo, sortField, sortDir);
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
        return "users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        List<Role> roles = userService.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("listRoles", roles);
        model.addAttribute("pageTitle", "Create New User");
        return "user_form";
    }

    @PostMapping("/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            FileUploadUtil.cleanDir(UPLOAD_DIR + "/" + savedUser.getId());
            FileUploadUtil.saveFile(UPLOAD_DIR + "/" + savedUser.getId(), fileName, multipartFile);
        } else {
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = userService.findUserById(id);
            List<Role> roles = userService.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("listRoles", roles);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", e.getMessage());
            return "redirect:/users";
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

        return "redirect:/users";
    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateEnabledStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "enabled") boolean enabled, RedirectAttributes redirectAttributes) {
        try {
            userService.updateEnabledStatus(id, enabled);
            redirectAttributes.addFlashAttribute("message", "Enabled status updated for user (ID: "+ id + ")");
            return "redirect:/users";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("exceptionMessage", "User not found (ID: " + id + ")");
            return "redirect:/users";
        }
    }
}
