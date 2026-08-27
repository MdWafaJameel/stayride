package com.stayride.ride.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRideRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String startLocation;

    @NotBlank
    private String endLocation;

    @NotNull
    @Positive
    private Double distance;
}