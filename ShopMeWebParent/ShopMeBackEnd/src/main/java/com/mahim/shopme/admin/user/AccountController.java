package com.mahim.shopme.admin.user;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.security.ShopmeUserDetails;
import com.mahim.shopme.common.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import static com.mahim.shopme.admin.utils.StaticPathUtils.UPLOAD_DIR;

@Controller
@RequestMapping("/account")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedInUser, Model model) {
        try {
            String email = loggedInUser.getUsername();
            User userByEmail = userService.findUserByEmail(email);
            model.addAttribute("user", userByEmail);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            model.addAttribute("user", null);
        } finally {
            return "account_form";
        }
    }

    @PostMapping("/update")
    public String updateUser(User user, RedirectAttributes redirectAttributes,
                             @RequestParam("image") MultipartFile multipartFile) throws IOException {

        User savedUser;

        try {
            if (!multipartFile.isEmpty() && multipartFile.getOriginalFilename() != null) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                user.setPhotos(fileName);
                savedUser = userService.updateAccount(user);
                FileUploadUtil.cleanDir(UPLOAD_DIR + "/" + savedUser.getId());
                FileUploadUtil.saveFile(UPLOAD_DIR + "/" + savedUser.getId(), fileName, multipartFile);
            } else {
                savedUser = userService.updateAccount(user);
            }
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", e.getMessage());
            e.printStackTrace();
        } finally {
            redirectAttributes.addFlashAttribute("message",
                    "Your account details have been updated successfully.");
            return "redirect:/account";
        }

    }
}
