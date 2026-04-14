package com.SneakySolo.nearfix.repository;

import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RepairRequestRepository extends JpaRepository<RepairRequest, Integer> {
    List<RepairRequest> findByCustomerIdOrderByCreatedAtDesc(Integer customerId);
    List<RepairRequest> findByStatusAndExpiresAtBefore(RequestStatus status, LocalDateTime time);
    List<RepairRequest> findByCustomerIdAndStatusInOrderByCreatedAtDesc(Integer customerId, List<RequestStatus> statuses);

    @Query("SELECT r FROM RepairRequest r JOIN r.bids b WHERE r.status = 'ACCEPTED' AND b.status = 'ACCEPTED' AND b.repairShop.owner.id = :providerId")
    List<RepairRequest> findAcceptedRequestsByProviderId(@Param("providerId") Integer providerId);

    @Query("SELECT r FROM RepairRequest r JOIN r.bids b WHERE r.status = 'DONE' AND b.status = 'ACCEPTED' AND b.repairShop.owner.id = :providerId")
    List<RepairRequest> findCompletedRequestsByProviderId(@Param("providerId") Integer providerId);

    @Query("""
    SELECT r FROM RepairRequest r
    WHERE r.status = 'PENDING'
    AND (6371 * acos(
        LEAST(1.0, GREATEST(-1.0,
            cos(radians(:lat)) * cos(radians(r.customer.latitude)) *
            cos(radians(r.customer.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(r.customer.latitude))
        ))
    )) <= 12
""")
    List<RepairRequest> findNearbyPendingRequests(@Param("lat") Double lat, @Param("lng") Double lng);
}
