package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.AdminMessage;
import com.SneakySolo.nearfix.repository.AdminMessageRepository;
import com.SneakySolo.nearfix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMessageService {

    private final UserRepository userRepository;
    private final AdminMessageRepository adminMessageRepository;

    public void sendMessage(Integer senderId, Integer adminId, Integer targetUserId, String content) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("admin not found"));

        User customer = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("customer not found"));

        if (!senderId.equals(adminId) && !senderId.equals(targetUserId)) {
            throw new IllegalArgumentException("Sender must be either the admin or the target user.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        AdminMessage message = new AdminMessage();
        message.setAdmin(admin);
        message.setTargetUser(customer);
        message.setSender(sender);
        message.setMessage(content);

        adminMessageRepository.save(message);
    }

    public List<AdminMessage> getMessages(Integer targetUserId) {
        return adminMessageRepository.findByTargetUserIdOrderBySentAtAsc(targetUserId);
    }
}
