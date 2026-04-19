package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.dto.CreateShopDTO;
import com.SneakySolo.nearfix.dto.PlaceBidDTO;
import com.SneakySolo.nearfix.entity.AdminMessage;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.repository.RepairRequestRepository;
import com.SneakySolo.nearfix.repository.UserRepository;
import com.SneakySolo.nearfix.service.AdminMessageService;
import com.SneakySolo.nearfix.service.BidService;
import com.SneakySolo.nearfix.service.RepairShopService;
import com.SneakySolo.nearfix.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final RepairRequestRepository repairRequestRepository;
    private final BidService bidService;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final AdminMessageService adminMessageService;
    private final RepairShopService repairShopService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer userId = sessionService.getUserId(session);

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (provider.getLatitude() == null || provider.getLongitude() == null) {
            model.addAttribute("requests", List.of());
            model.addAttribute("noLocation", true);
            return "provider/dashboard";
        }

        List<RepairRequest> nearby = repairRequestRepository
                .findNearbyPendingRequests(provider.getLatitude(), provider.getLongitude());

        model.addAttribute("requests", nearby);
        model.addAttribute("noLocation", false);
        return "provider/dashboard";
    }

    @GetMapping("/request/{requestId}")
    public String viewRequest(@PathVariable Integer requestId,
                              HttpSession session,
                              Model model) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        Integer userId = sessionService.getUserId(session);

        RepairRequest request = repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        boolean alreadyBid = bidService.getBidsForRequest(requestId)
                .stream()
                .anyMatch(b -> b.getRepairShop().getOwner().getId().equals(userId));

        model.addAttribute("request", request);
        model.addAttribute("alreadyBid", alreadyBid);
        model.addAttribute("placeBidDTO", new PlaceBidDTO());
        return "provider/request-detail";
    }

    @PostMapping("/request/{requestId}/bid")
    public String placeBid(@PathVariable Integer requestId,
                           @RequestParam Double price,
                           @RequestParam Integer estimatedHour,
                           @RequestParam(required = false) String message,
                           HttpSession session,
                           Model model) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        try {
            Integer userId = sessionService.getUserId(session);

            PlaceBidDTO dto = new PlaceBidDTO();
            dto.setPrice(price);
            dto.setEstimatedHour(estimatedHour);
            dto.setMessage(message);

            bidService.placeBid(requestId, dto, userId);
            return "redirect:/provider/dashboard";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/provider/request/" + requestId + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/accepted")
    public String providerChat (HttpSession session, Model model) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        Integer userId = sessionService.getUserId(session);
        List<RepairRequest> acceptedRequests = repairRequestRepository.findAcceptedRequestsByProviderId(userId);
        model.addAttribute("list", acceptedRequests);
        return "provider/accepted-requests";
    }

    @GetMapping("/provider/history")
    public String getHistoryRequests(HttpSession session,
                                     Model model) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        Integer userId = sessionService.getUserId(session);
        List<RepairRequest> requests = repairRequestRepository.findCompletedRequestsByProviderId(userId);

        model.addAttribute("requests", requests);
        return "provider/history";
    }

    @GetMapping("/admin-chat")
    public String getAdminMessages(HttpSession session, Model model) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        Integer userId = sessionService.getUserId(session);
        List<AdminMessage> messages = adminMessageService.getMessages(userId);
        User admin = userRepository.findFirstByRole(Role.ADMIN);

        model.addAttribute("messages", messages);
        model.addAttribute("adminId", admin.getId());
        model.addAttribute("userId", userId);
        model.addAttribute("targetUserId", userId);

        return "provider/admin-chat";
    }

    @PostMapping("/admin-chat/send")
    public String sendAdminMessage(@RequestParam String message,
                                   HttpSession session) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        Integer userId = sessionService.getUserId(session);
        adminMessageService.sendMessage(userId, userId, message);
        return "redirect:/provider/admin-chat";
    }

    @GetMapping("/admin-chat/messages")
    public String getAdminChatFragment(HttpSession session, Model model) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        Integer userId = sessionService.getUserId(session);
        List<AdminMessage> messages = adminMessageService.getMessages(userId);

        model.addAttribute("messages", messages);
        model.addAttribute("userId", userId);

        return "provider/admin-chat-fragment";
    }

    @GetMapping("/setup-shop")
    public String showSetupShop(HttpSession session) {
        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }
        return "provider/setup-shop";
    }

    @PostMapping("/setup-shop")
    public String setupShop(@RequestParam String shopName,
                            @RequestParam String description,
                            HttpSession session,
                            Model model) {

        if (!sessionService.hasRole(session, Role.SERVICE_PROVIDER)) {
            return "redirect:/auth/login";
        }

        try {
            Integer userId = sessionService.getUserId(session);
            CreateShopDTO dto = new CreateShopDTO();
            dto.setShopName(shopName);
            dto.setDescription(description);
            repairShopService.createShop(userId, dto);
            return "redirect:/provider/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "provider/setup-shop";
        }
    }
}
