package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.service.RepairRequestService;
import com.SneakySolo.nearfix.service.ReviewService;
import com.SneakySolo.nearfix.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final RepairRequestService repairRequestService;
    private final ReviewService reviewService;
    private final SessionService sessionService;

    @GetMapping("/review/{requestId}")
    public String readReview (@PathVariable Integer requestId,
                                HttpSession session, Model model) {
        RepairRequest request = repairRequestService.getRequestById(requestId);

        model.addAttribute("request", request);
        model.addAttribute("existingReview", reviewService.getReviewForRequest(requestId).orElse(null));

        return "review/submit-review";
    }

    @PostMapping("/review/{requestId}/submit")
    public String submitReview (@PathVariable Integer requestId,
                                @RequestParam Integer stars,
                                @RequestParam(required = false) String comment,
                                HttpSession session) {
        try {
            Integer userId = sessionService.getUserId(session);
            reviewService.submitReview(requestId, userId, stars, comment);

            return "redirect:/customer/request/" + requestId;
        }
        catch (Exception e) {
            return "redirect:/customer/request/" + requestId + "?error=" + e.getMessage();
        }
    }
}
