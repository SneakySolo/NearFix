package com.SneakySolo.nearfix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceBidDTO {

    @NotNull(message = "Price cannot be empty")
    private Double price;

    @NotNull(message = "This cannot be empty")
    private Integer estimatedHour;

    private String message;
}
