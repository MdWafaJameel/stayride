package com.stayride.ride.service;

import com.stayride.common.exception.ResourceNotAvailableException;
import com.stayride.common.exception.ResourceNotFoundException;
import com.stayride.ride.dto.BookRideRequest;
import com.stayride.ride.dto.RideResponse;
import com.stayride.ride.entity.Driver;
import com.stayride.ride.entity.RideDetails;
import com.stayride.ride.entity.RideStatus;
import com.stayride.ride.repository.DriverRepository;
import com.stayride.ride.repository.RideDetailsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final DriverRepository driverRepository;
    private final RideDetailsRepository rideDetailsRepository;

    @Transactional
    public RideResponse bookRide(BookRideRequest request) {

        Driver driver = driverRepository.findFirstByAvailableTrue()
                .orElseThrow(() ->
                        new ResourceNotAvailableException(
                                "No available drivers"));

        driver.setAvailable(false);
        driverRepository.save(driver);

        RideDetails ride = new RideDetails();
        ride.setUserId(request.getUserId());
        ride.setDriver(driver);
        ride.setStartLocation(request.getStartLocation());
        ride.setEndLocation(request.getEndLocation());
        ride.setDistance(request.getDistance());
        ride.setFare(request.getDistance() * 25);
        ride.setRideStatus(RideStatus.ONGOING);
        ride.setStartTime(LocalDateTime.now());

        ride = rideDetailsRepository.save(ride);

        return toResponse(ride);
    }

    @Transactional
    public RideResponse endRide(Long rideId) {

        RideDetails ride = rideDetailsRepository.findById(rideId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ride not found"));

        ride.setRideStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());

        Driver driver = ride.getDriver();
        driver.setAvailable(true);

        rideDetailsRepository.save(ride);

        return toResponse(ride);
    }

    public List<RideResponse> getRecentCompletedRides(Long userId) {

        LocalDateTime time =
                LocalDateTime.now().minusMinutes(30);

        return rideDetailsRepository
                .findByUserIdAndRideStatusAndEndTimeAfter(
                        userId,
                        RideStatus.COMPLETED,
                        time
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RideResponse toResponse(RideDetails ride) {

        return new RideResponse(
                ride.getId(),
                ride.getUserId(),
                ride.getDriver().getId(),
                ride.getDriver().getName(),
                ride.getStartLocation(),
                ride.getEndLocation(),
                ride.getDistance(),
                ride.getFare(),
                ride.getRideStatus(),
                ride.getStartTime(),
                ride.getEndTime()
        );
    }

}
