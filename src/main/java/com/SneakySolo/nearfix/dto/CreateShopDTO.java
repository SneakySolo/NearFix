package com.SneakySolo.nearfix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateShopDTO {

    @NotBlank(message = "Shop name cannot be empty")
    private String shopName;

    @NotBlank(message = "Description cannot be empty")
    private String description;
}