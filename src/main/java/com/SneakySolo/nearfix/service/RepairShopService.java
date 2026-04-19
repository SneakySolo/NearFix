package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.dto.CreateShopDTO;
import com.SneakySolo.nearfix.entity.RepairShop;
import com.SneakySolo.nearfix.repository.RepairShopRepository;
import com.SneakySolo.nearfix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepairShopService {

    private final RepairShopRepository repairShopRepository;
    private final UserRepository userRepository;

    public void createShop(Integer ownerId, CreateShopDTO dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (repairShopRepository.findByOwnerId(ownerId).isPresent()) {
            throw new RuntimeException("You already have a shop registered");
        }

        RepairShop shop = new RepairShop();
        shop.setOwner(owner);
        shop.setShopName(dto.getShopName());
        shop.setDescription(dto.getDescription());
        repairShopRepository.save(shop);
    }
}