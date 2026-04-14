package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.bid.Bid;
import com.SneakySolo.nearfix.domain.bid.BidStatus;
import com.SneakySolo.nearfix.domain.message.Message;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.entity.RequestStatus;
import com.SneakySolo.nearfix.repository.BidRepository;
import com.SneakySolo.nearfix.repository.MessageRepository;
import com.SneakySolo.nearfix.repository.RepairRequestRepository;
import com.SneakySolo.nearfix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final RepairRequestRepository repairRequestRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final MessageRepository messageRepository;

    public boolean isProvider(Integer senderId) {
        List<Bid> bids = bidRepository.findAll();

        for (Bid bid : bids) {
            if (bid.getStatus().equals(BidStatus.ACCEPTED)
                && bid.getRepairShop().getOwner().getId().equals(senderId)) {
                return true;
            }
        }
        return false;
    }

    public void sendMessage(Integer requestId, Integer senderId, String content) {
        RepairRequest repairRequest = repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("no such request exists"));

        if (!repairRequest.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new RuntimeException("not an accepted request");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        if (sender.getId().equals(repairRequest.getCustomer().getId())
            || isProvider(senderId)) {

            Message message = new Message();
            message.setSender(sender);
            message.setRepairRequest(repairRequest);
            message.setMessage(content);

            messageRepository.save(message);
        }
        else {
            throw new RuntimeException("you aren't the customer or the particular service provider");
        }
    }

    public List<Message> getMessages(Integer requestId) {
        return messageRepository.findByRepairRequestIdOrderBySentAtAsc(requestId);
    }
}
