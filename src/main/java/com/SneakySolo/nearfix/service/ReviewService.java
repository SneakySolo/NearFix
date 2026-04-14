package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.bid.Bid;
import com.SneakySolo.nearfix.domain.bid.BidStatus;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.entity.RequestStatus;
import com.SneakySolo.nearfix.entity.Review;
import com.SneakySolo.nearfix.repository.RepairRequestRepository;
import com.SneakySolo.nearfix.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RepairRequestRepository repairRequestRepository;
    private final ReviewRepository reviewRepository;

    public void submitReview(Integer requestId, Integer customerId, Integer stars, String comment) {
        RepairRequest request =  repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("No such request exists"));

        if (!request.getStatus().equals(RequestStatus.DONE)) {
            throw new RuntimeException("Request hasn't been marked DONE yet");
        }

        if (!customerId.equals(request.getCustomer().getId())) {
            throw new RuntimeException("customer does not owns this request");
        }

        if (reviewRepository.existsByRequestRepairId(requestId)) {
            throw new RuntimeException("review already exists");
        }

        User provider = null;
        List<Bid> allBids = request.getBids();
        for (Bid bid : allBids) {
            if (bid.getStatus().equals(BidStatus.ACCEPTED)) {
                Bid acceptedBid = bid;
                provider = acceptedBid.getRepairShop().getOwner();
            }
        }

        if (provider == null) {
            throw new RuntimeException("provider is null");
        }

        Review review = new Review();
        review.setComment(comment);
        review.setStars(stars);
        review.setProvider(provider);
        review.setCustomer(request.getCustomer());
        review.setRequestRepair(request);

        reviewRepository.save(review);
    }

    public Double getAverageRating(Integer providerId) {
        List<Review> reviews = reviewRepository.findByProviderId(providerId);

        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getStars).average().orElse(0.0);
    }

    public Optional<Review> getReviewForRequest(Integer requestId) {
        return reviewRepository.findByRequestRepairId(requestId);
    }
}
