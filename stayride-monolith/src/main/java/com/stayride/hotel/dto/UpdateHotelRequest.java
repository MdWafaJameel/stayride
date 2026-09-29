package com.stayride.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateHotelRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    private Double rating;
}