package com.SneakySolo.nearfix.controller;

import com.SneakySolo.nearfix.service.LocationService;
import com.SneakySolo.nearfix.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final SessionService sessionService;

    @PostMapping("/location/update")
    @ResponseBody
    ResponseEntity<String> getLocation(@RequestParam Double latitude,
                                              @RequestParam Double longitude,
                                              HttpSession session) {
        Integer userId = sessionService.getUserId(session);
        locationService.updateLocation(userId,latitude, longitude);
        return ResponseEntity.ok().build();
    }
}
