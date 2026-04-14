package com.SneakySolo.nearfix.repository;

import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.RepairShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepairShopRepository extends JpaRepository<RepairShop, Integer> {
    Optional<RepairShop> findByOwnerId(Integer ownerId);
}
