package com.SneakySolo.nearfix.repository;

import com.SneakySolo.nearfix.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findByProviderId(Integer providerId);
    boolean existsByRequestRepairId(Integer requestId);
    Optional<Review> findByRequestRepairId(Integer requestId);
}
