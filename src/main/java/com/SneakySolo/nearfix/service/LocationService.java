package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final UserService  userService;
    private final UserRepository userRepository;

    public void updateLocation (Integer userId, Double latitude, Double longitude) {

        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("user not found"));
        user.setLatitude(latitude);
        user.setLongitude(longitude);

        userRepository.save(user);
    }
}
