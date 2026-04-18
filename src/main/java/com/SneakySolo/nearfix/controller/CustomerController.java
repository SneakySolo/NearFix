package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.bid.Bid;
import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.dto.CreateRequestDTO;
import com.SneakySolo.nearfix.entity.AdminMessage;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.repository.AdminMessageRepository;
import com.SneakySolo.nearfix.repository.UserRepository;
import com.SneakySolo.nearfix.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final RepairRequestService repairRequestService;
    private final SessionService sessionService;
    private final BidService bidService;
    private final ReviewService reviewService;
    private final AdminMessageService adminMessageService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer userId = sessionService.getUserId(session);

        model.addAttribute("activeRequests", repairRequestService.getActiveRequestsByCustomer(userId));
        model.addAttribute("historyRequests", repairRequestService.getHistoryRequestsByCustomer(userId));
        return "customer/dashboard";
    }

    @GetMapping("/request/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createRequestDTO",  new CreateRequestDTO());
        return "customer/create-request";
    }

    @PostMapping("/request/new")
    public String createRequest (@RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam (required = false) List<MultipartFile> mediaFiles,
                                 HttpSession session, Model model) {

        try {
            CreateRequestDTO createRequestDTO = new CreateRequestDTO();
            createRequestDTO.setTitle(title);
            createRequestDTO.setDescription(description);
            createRequestDTO.setMediaFiles(mediaFiles);

            Integer userId = sessionService.getUserId(session);
            repairRequestService.createRequest(createRequestDTO, userId);
            return  "redirect:/customer/dashboard";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "customer/create-request";
        }
    }

    @GetMapping("/request/{requestId}")
    public String getRequestById (@PathVariable Integer requestId, Model model) {
        RepairRequest request = repairRequestService.getRequestById(requestId);
        List<Bid> bid = bidService.getBidsForRequest(requestId);

        Map<Integer, Double> ratings = new HashMap<>();
        for (Bid bids : bid) {
            Integer shopOwnerId = bids.getRepairShop().getOwner().getId();
            ratings.put(bids.getRepairShop().getId(), reviewService.getAverageRating(shopOwnerId));
        }

        model.addAttribute("ratings", ratings);
        model.addAttribute("request", request);
        model.addAttribute("bids", bid);

        return "customer/request-detail";
    }

    @PostMapping("/request/{requestId}/bid/{bidId}/accept")
    public String acceptBid (@PathVariable Integer requestId,
                             @PathVariable Integer bidId,
                             HttpSession session) {

        try {
            Integer userId = sessionService.getUserId(session);
            bidService.acceptBid(bidId, userId);

            return "redirect:/customer/request/" + requestId;
        }
        catch (RuntimeException e) {
            return "redirect:/customer/request/" + requestId + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/request/{requestId}/done")
    public String markAsDone(@PathVariable Integer requestId,
                             HttpSession session) {
        try {
            Integer userId = sessionService.getUserId(session);
            bidService.markAsDone(requestId, userId);
            return "redirect:/customer/request/" + requestId;
        }
        catch (RuntimeException e) {
            return "redirect:/customer/request/" + requestId + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/admin-chat")
    public String getAdminMessages (HttpSession session, Model model) {
        Integer userId = sessionService.getUserId(session);
        List<AdminMessage> messages = adminMessageService.getMessages(userId);
        User admin = userRepository.findFirstByRole(Role.ADMIN);

        model.addAttribute("messages", messages);
        model.addAttribute("adminId", admin.getId());
        model.addAttribute("userId", userId);
        model.addAttribute("targetUserId", userId);

        return "customer/admin-chat";
    }

    @PostMapping("/admin-chat/send")
    public String sendMessages (@RequestParam String message,
                                HttpSession session) {
        Integer userId = sessionService.getUserId(session);
        adminMessageService.sendMessage(userId, userId, message);
        return "redirect:/customer/admin-chat";
    }

    @GetMapping("/admin-chat/messages")
    public String getAdminChatFragment(HttpSession session, Model model) {
        Integer userId = sessionService.getUserId(session);
        List<AdminMessage> messages = adminMessageService.getMessages(userId);

        model.addAttribute("messages", messages);
        model.addAttribute("userId", userId);

        return "customer/admin-chat-fragment";
    }
}
