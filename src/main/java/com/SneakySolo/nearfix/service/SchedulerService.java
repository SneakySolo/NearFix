package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.entity.RequestStatus;
import com.SneakySolo.nearfix.repository.RepairRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final RepairRequestRepository repairRequestRepository;

    @Scheduled(fixedRate = 3600000)
    public void updateStatus () {
        List<RepairRequest> list = repairRequestRepository.findByStatusAndExpiresAtBefore(RequestStatus.PENDING, LocalDateTime.now());

        for (RepairRequest repairRequest : list) {
            repairRequest.setStatus(RequestStatus.DESTROYED);
        }
        repairRequestRepository.saveAll(list);
    }
}
