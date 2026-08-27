package com.stayride.ride.dto;

import com.stayride.ride.entity.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RideResponse {

    private Long id;
    private Long userId;
    private Long driverId;
    private String driverName;
    private String startLocation;
    private String endLocation;
    private Double distance;
    private Double fare;
    private RideStatus rideStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}