package com.SneakySolo.nearfix.repository;

import com.SneakySolo.nearfix.domain.bid.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Integer> {
    boolean existsByRepairRequestIdAndRepairShopId(Integer requestId, Integer shopId); // ← repairRequest not request
    List<Bid> findByRepairRequestId(Integer requestId); // ← repairRequest not request
}
