package com.mahim.shopme.admin.user.controller;

import com.mahim.shopme.admin.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/check_email")
    public String checkDuplicateEmail(@RequestParam("id") Integer id,
                                      @RequestParam("email") String email) {
        return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
