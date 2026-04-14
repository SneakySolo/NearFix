package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.message.Message;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.service.MessageService;
import com.SneakySolo.nearfix.service.RepairRequestService;
import com.SneakySolo.nearfix.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RepairRequestService repairRequestService;
    private final MessageService messageService;
    private final SessionService sessionService;

    @GetMapping("/chat/{requestId}")
    public String getMessage(@PathVariable Integer requestId,
                             HttpSession session, Model model) {

        RepairRequest repairRequest = repairRequestService.getRequestById(requestId);
        List<Message> messages = messageService.getMessages(requestId);

        model.addAttribute("request", repairRequest);
        model.addAttribute("messages", messages);
        model.addAttribute("requestId", requestId);
        model.addAttribute("userId", sessionService.getUserId(session));

        return "chat/chat";
    }

    @PostMapping("/chat/{requestId}/send")
    public String sendMessage (@PathVariable Integer requestId,
                               @RequestParam String message,
                               HttpSession session) {
        messageService.sendMessage(requestId, sessionService.getUserId(session), message);

        return "redirect:/chat/{requestId}";
    }

    @GetMapping("/chat/{requestId}/messages")
    public String getMessageFragments (@PathVariable Integer requestId,
                                       HttpSession session, Model model) {

        List<Message> messages = messageService.getMessages(requestId);
        Integer userId = sessionService.getUserId(session);

        model.addAttribute("messages", messages);
        model.addAttribute("userId", userId);

        return "chat/messages-fragment";
    }
}
