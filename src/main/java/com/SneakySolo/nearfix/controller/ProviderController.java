package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.dto.PlaceBidDTO;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.repository.RepairRequestRepository;
import com.SneakySolo.nearfix.repository.UserRepository;
import com.SneakySolo.nearfix.service.BidService;
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
        Integer userId = sessionService.getUserId(session);
        List<RepairRequest> acceptedRequests = repairRequestRepository.findAcceptedRequestsByProviderId(userId);
        model.addAttribute("list", acceptedRequests);
        return "provider/accepted-requests";
    }

    @GetMapping("/provider/history")
    public String getHistoryRequests(HttpSession session,
                                     Model model) {
        Integer userId = sessionService.getUserId(session);
        List<RepairRequest> requests = repairRequestRepository.findCompletedRequestsByProviderId(userId);

        model.addAttribute("requests", requests);
        return "provider/history";
    }
}
