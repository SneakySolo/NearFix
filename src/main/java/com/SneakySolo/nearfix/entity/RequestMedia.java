package com.SneakySolo.nearfix.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "request_media")
@NoArgsConstructor
@Getter @Setter
public class RequestMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn (name = "request_id",  nullable = false)
    private RepairRequest request;
}
