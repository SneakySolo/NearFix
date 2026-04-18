package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.user.Role;
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

    public void sendMessage(Integer senderId, Integer targetUserId, String content) {
        User admin = userRepository.findFirstByRole(Role.ADMIN);

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("customer not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        AdminMessage message = new AdminMessage();
        message.setAdmin(admin);
        message.setTargetUser(targetUser);
        message.setSender(sender);
        message.setMessage(content);

        adminMessageRepository.save(message);
    }

    public List<AdminMessage> getMessages(Integer targetUserId) {
        return adminMessageRepository.findByTargetUserIdOrderByCreatedAtAsc(targetUserId);
    }
}
