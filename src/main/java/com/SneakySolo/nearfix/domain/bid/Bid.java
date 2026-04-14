package com.SneakySolo.nearfix.domain.bid;

import com.SneakySolo.nearfix.entity.RepairRequest;
import com.SneakySolo.nearfix.entity.RepairShop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter @Setter
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false)
    private Double price;

    @Column (nullable = false)
    private Integer estimatedHour;

    @Column (nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn (name = "request_id", nullable = false)
    private RepairRequest repairRequest;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "shop_id", nullable = false)
    private RepairShop  repairShop;

    @Enumerated(EnumType.STRING)
    private BidStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
