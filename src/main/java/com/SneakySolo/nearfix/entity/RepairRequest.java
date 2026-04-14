package com.SneakySolo.nearfix.entity;

import com.SneakySolo.nearfix.domain.bid.Bid;
import com.SneakySolo.nearfix.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_requests")
@Getter @Setter
@NoArgsConstructor
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn (name = "customer_id", nullable = false)
    private User customer;

    @OneToMany(mappedBy = "repairRequest", fetch = FetchType.EAGER)
    private List<Bid> bids = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @OneToMany (mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RequestMedia> requestMedia = new ArrayList<>();

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(12);
        this.status = RequestStatus.PENDING;
    }
}
