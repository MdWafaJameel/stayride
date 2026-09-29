package com.stayride.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HotelResponse {
    private Long id;
    private String name;
    private String location;
    private Double rating;
}
