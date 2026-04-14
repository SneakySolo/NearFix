package com.SneakySolo.nearfix.repository;

import com.SneakySolo.nearfix.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRepairRequestIdOrderBySentAtAsc(Integer requestId);
}
