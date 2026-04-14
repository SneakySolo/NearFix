package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.bid.Bid;
import com.SneakySolo.nearfix.domain.bid.BidStatus;
import com.SneakySolo.nearfix.dto.PlaceBidDTO;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.entity.RepairShop;
import com.SneakySolo.nearfix.entity.RequestStatus;
import com.SneakySolo.nearfix.repository.BidRepository;
import com.SneakySolo.nearfix.repository.RepairRequestRepository;
import com.SneakySolo.nearfix.repository.RepairShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final RepairShopRepository repairShopRepository;
    private final RepairRequestRepository repairRequestRepository;

    public void placeBid(Integer requestId, PlaceBidDTO dto, Integer userId) {
        RepairShop shop = repairShopRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("SHOP NOT FOUND"));

        RepairRequest request = repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("This request is no longer open for bids");
        }

        if (bidRepository.existsByRepairRequestIdAndRepairShopId(requestId, shop.getId())) {
            throw new RuntimeException("You have already placed a bid on this request");
        }

        Bid bid = new Bid();
        bid.setPrice(dto.getPrice());
        bid.setMessage(dto.getMessage());
        bid.setEstimatedHour(dto.getEstimatedHour());
        bid.setRepairRequest(request);
        bid.setRepairShop(shop);
        bid.setStatus(BidStatus.PENDING);

        bidRepository.save(bid);
    }

    public List<Bid> getBidsForRequest(Integer requestId) {
        return bidRepository.findByRepairRequestId(requestId);
    }

    @Transactional
    public void acceptBid (Integer bidId, Integer customerId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("no such bid found"));

        RepairRequest request = bid.getRepairRequest();

        if (!customerId.equals(request.getCustomer().getId())) {
            throw new RuntimeException("current owner does not owns this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("No action can be done on this request");
        }
        bid.setStatus(BidStatus.ACCEPTED);

        List<Bid> otherBids = getBidsForRequest(request.getId());
        for (Bid bids : otherBids) {
            if (bids.getStatus() != BidStatus.ACCEPTED) {
                bids.setStatus(BidStatus.REJECTED);
                bidRepository.save(bids);
            }
        }

        request.setStatus(RequestStatus.ACCEPTED);
        bidRepository.save(bid);
        repairRequestRepository.save(request);
    }

    public void markAsDone(Integer requestId, Integer customerId) {
        RepairRequest request = repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("request not found"));

        if (!customerId.equals(request.getCustomer().getId())) {
            throw new RuntimeException("customer does not owns this request");
        }

        if (request.getStatus() != RequestStatus.ACCEPTED) {
            throw new RuntimeException("this request wasn't accepted in the first place");
        }

        request.setStatus(RequestStatus.DONE);
        repairRequestRepository.save(request);
    }
}
