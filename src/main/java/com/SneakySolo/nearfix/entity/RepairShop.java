package com.SneakySolo.nearfix.entity;

import com.SneakySolo.nearfix.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
public class RepairShop {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne (cascade = CascadeType.ALL)
    private User owner;

    @Column (nullable = false)
    private String shopName;

    @Column (nullable = false)
    private String description;
}
