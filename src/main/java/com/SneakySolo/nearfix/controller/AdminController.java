package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String getCustomerAndProvider (Model model){
        List<User> customers = userService.getAllByRole(Role.CUSTOMER);
        List<User> providers = userService.getAllByRole(Role.SERVICE_PROVIDER);

        model.addAttribute("customers", customers);
        model.addAttribute("providers", providers);

        return "admin/dashboard";
    }

    @PostMapping("/admin/user/{userId}/toggle")
    public String toggleUserStatus(@PathVariable Integer id) {
        userService.toggleUserStatus(id);
        return "/dashboard";
    }
}
