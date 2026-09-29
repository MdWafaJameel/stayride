package com.stayride.ride.dto;

import com.stayride.ride.entity.RideDetails;
import com.stayride.ride.entity.RideStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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

    public static RideResponse from(RideDetails ride) {

        RideResponse response = new RideResponse();

        response.setId(ride.getId());
        response.setUserId(ride.getUserId());

        if (ride.getDriver() != null) {
            response.setDriverId(ride.getDriver().getId());
            response.setDriverName(ride.getDriver().getName());
        }

        response.setStartLocation(ride.getStartLocation());
        response.setEndLocation(ride.getEndLocation());
        response.setDistance(ride.getDistance());
        response.setFare(ride.getFare());
        response.setRideStatus(ride.getRideStatus());
        response.setStartTime(ride.getStartTime());
        response.setEndTime(ride.getEndTime());

        return response;
    }
}