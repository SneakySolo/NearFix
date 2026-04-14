package com.SneakySolo.nearfix.entity;

import com.SneakySolo.nearfix.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter @Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn (name = "requests",unique = true)
    private RepairRequest requestRepair;

    @ManyToOne
    private User customer;

    @ManyToOne
    private User provider;

    @Min(value = 1) @Max(value = 5)
    private Integer stars;

    @Column(length = 200, columnDefinition = "TEXT", nullable = true)
    private String comment;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
