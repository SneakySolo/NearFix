package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.message.Message;
import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.AdminMessage;
import com.SneakySolo.nearfix.repository.AdminMessageRepository;
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
    public String toggleUserStatus(@PathVariable Integer id) {
        userService.toggleUserStatus(id);
        return "/dashboard";
    }

    @GetMapping("/chat/{targetUserId}")
    public String getMessages (@PathVariable Integer id,
                               HttpSession session, Model model) {
        List<AdminMessage> messages = adminMessageService.getMessages(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("error"));

        model.addAttribute("user", user);
        model.addAttribute("userId", user.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("adminId",sessionService.getUserId(session));

        return "admin/chat";
    }

    @PostMapping("/chat/{targetUserId}/send")
    public String sendMessages (@PathVariable Integer id,
                                @RequestParam String message,
                                HttpSession session) {
        adminMessageService.sendMessage(id,
                sessionService.getUserId(session),
                id, message);

        return "redirect:/admin/chat/{targetUserId}" + id;
    }

    @GetMapping("/admin/chat/{targetUserId}/messages")
    public String getMessageFragments (@PathVariable Integer requestId,
                                       HttpSession session, Model model) {

        List<AdminMessage> messages = adminMessageService.getMessages(requestId);
        Integer userId = sessionService.getUserId(session);

        model.addAttribute("messages", messages);
        model.addAttribute("userId", userId);

        return "/chat-fragment";
    }
}
