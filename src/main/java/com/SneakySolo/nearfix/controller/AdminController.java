package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.AdminMessage;
import com.SneakySolo.nearfix.repository.UserRepository;
import com.SneakySolo.nearfix.service.AdminMessageService;
import com.SneakySolo.nearfix.service.SessionService;
import com.SneakySolo.nearfix.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AdminMessageService adminMessageService;
    private final SessionService sessionService;

    @GetMapping("/dashboard")
    public String getCustomerAndProvider (Model model){

        List<User> customers = userService.getAllByRole(Role.CUSTOMER);
        List<User> providers = userService.getAllByRole(Role.SERVICE_PROVIDER);

        model.addAttribute("customers", customers);
        model.addAttribute("providers", providers);

        return "admin/dashboard";
    }

    @PostMapping("/user/{userId}/toggle")
    public String toggleUserStatus(@PathVariable Integer userId,
                                   HttpSession session) {
        if (!sessionService.hasRole(session, Role.ADMIN)) {
            return "redirect:/auth/login";
        }

        userService.toggleUserStatus(userId);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/chat/{targetUserId}")
    public String getMessages (@PathVariable Integer targetUserId,
                               HttpSession session, Model model) {

        if (!sessionService.hasRole(session, Role.ADMIN)) {
            return "redirect:/auth/login";
        }

        List<AdminMessage> messages = adminMessageService.getMessages(targetUserId);
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("error"));

        model.addAttribute("targetUser", user);
        model.addAttribute("targetUserId", targetUserId);
        model.addAttribute("messages", messages);
        model.addAttribute("adminId", sessionService.getUserId(session));

        return "admin/chat";
    }

    @PostMapping("/chat/{targetUserId}/send")
    public String sendMessages (@PathVariable Integer targetUserId,
                                @RequestParam String message,
                                HttpSession session) {

        if (!sessionService.hasRole(session, Role.ADMIN)) {
            return "redirect:/auth/login";
        }

        Integer adminId = sessionService.getUserId(session);
        adminMessageService.sendMessage(adminId, targetUserId, message);
        return "redirect:/admin/chat/" + targetUserId;
    }

    @GetMapping("/chat/{targetUserId}/messages")
    public String getMessageFragments (@PathVariable Integer targetUserId,
                                       HttpSession session, Model model) {

        if (!sessionService.hasRole(session, Role.ADMIN)) {
            return "redirect:/auth/login";
        }

        List<AdminMessage> messages = adminMessageService.getMessages(targetUserId);
        Integer userId = sessionService.getUserId(session);

        model.addAttribute("messages", messages);
        model.addAttribute("userId", userId);

        return "admin/chat-fragment";
    }
}
