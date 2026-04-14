package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.dto.LoginDTO;
import com.SneakySolo.nearfix.dto.RegisterDTO;
import com.SneakySolo.nearfix.service.SessionService;
import com.SneakySolo.nearfix.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/register")
    public String showRegister (Model model) {
        model.addAttribute("registerDTO", new RegisterDTO("", "", "", "", null));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register (@Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
                           BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "/auth/register";
        }

        try {
            userService.registerCustomer(registerDTO);
            return "redirect:/auth/login?registered=true";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLogin (Model model) {
        model.addAttribute("loginDTO", new LoginDTO("", ""));
        return "auth/login";
    }

    @PostMapping("/login")
    public String login (@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                         BindingResult result, Model model, HttpSession session) {

        if (result.hasErrors()) {
            return "auth/login";
        }

        User user = userService.login(loginDTO.getEmail(), loginDTO.getPassword());

        if (user == null) {
            model.addAttribute("error", "Invalid email or password");
            return "auth/login";
        }

        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USER_ROLE", user.getRole());
        session.setAttribute("USER_NAME", user.getFullname());

        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        if (user.getRole() == Role.CUSTOMER) {
            return "redirect:/customer/dashboard";
        }
        if (user.getRole() == Role.SERVICE_PROVIDER) {
            return "redirect:/provider/dashboard";
        }

        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
