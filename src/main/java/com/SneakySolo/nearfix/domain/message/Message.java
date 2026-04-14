package com.SneakySolo.nearfix.domain.message;

import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.entity.RepairRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter @Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne (fetch = FetchType.EAGER)
    private RepairRequest repairRequest;

    @ManyToOne (fetch = FetchType.EAGER)
    private User sender;

    @Column (columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentAt;

    @PrePersist
    void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}