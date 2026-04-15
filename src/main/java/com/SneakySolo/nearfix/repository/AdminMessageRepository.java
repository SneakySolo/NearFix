package com.SneakySolo.nearfix.repository;

import com.SneakySolo.nearfix.entity.AdminMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminMessageRepository extends JpaRepository<AdminMessage, Integer> {
    List<AdminMessage> findByTargetUserIdOrderBySentAtAsc(Integer targetUserId);
}
