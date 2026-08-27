package com.stayride.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String roomNumber;
    private String type;
    private Double pricePerNight;
    private Integer capacity;
}
