package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.dto.CreateRequestDTO;
import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.entity.RequestMedia;
import com.SneakySolo.nearfix.entity.RequestStatus;
import com.SneakySolo.nearfix.repository.RepairRequestRepository;
import com.SneakySolo.nearfix.repository.RequestMediaRepository;
import com.SneakySolo.nearfix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RepairRequestService {

    private final UserRepository userRepository;
    private final RequestMediaRepository requestMediaRepository;
    private final RepairRequestRepository repairRequestRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public void createRequest (CreateRequestDTO dto, Integer customerId) {

        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("no such user exists"));

        RepairRequest request = new RepairRequest();
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setCustomer(user);
        repairRequestRepository.save(request);

        if (dto.getMediaFiles() != null) {
            for (MultipartFile file : dto.getMediaFiles()) {

                if (file.isEmpty()) {
                    continue;
                }

                try {
                    File folder = new File(uploadDir);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    File dest = new File(uploadDir + "/" + filename);
                    file.transferTo(dest);

                    RequestMedia media = new RequestMedia();
                    media.setFilePath(filename);
                    media.setRequest(request);
                    requestMediaRepository.save(media);
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to save file: " + e.getMessage());
                }
            }
        }
    }

    public List<RepairRequest> getRequestsByCustomer(Integer customerId) {
        return repairRequestRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public RepairRequest getRequestById(Integer requestId) {
        return repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("no such request exists"));
    }

    public List<RepairRequest> getActiveRequestsByCustomer(Integer customerId) {
        List <RequestStatus> statuses = new ArrayList<>();
        statuses.add(RequestStatus.ACCEPTED);
        statuses.add(RequestStatus.PENDING);

        return repairRequestRepository.findByCustomerIdAndStatusInOrderByCreatedAtDesc(customerId, statuses);
    }

    public List<RepairRequest> getHistoryRequestsByCustomer(Integer customerId) {
        List <RequestStatus> statuses = new ArrayList<>();
        statuses.add(RequestStatus.DONE);
        statuses.add(RequestStatus.DESTROYED);

        return repairRequestRepository.findByCustomerIdAndStatusInOrderByCreatedAtDesc(customerId, statuses);
    }
}
