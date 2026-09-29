package com.stayride.ride.service;

import com.stayride.ride.client.UserClient;
import com.stayride.ride.dto.BookRideRequest;
import com.stayride.ride.entity.Driver;
import com.stayride.ride.entity.RideDetails;
import com.stayride.ride.entity.RideStatus;
import com.stayride.ride.exception.NoDriverAvailableException;
import com.stayride.ride.repository.DriverRepository;
import com.stayride.ride.repository.RideDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final UserClient userClient;
    private final DriverRepository driverRepository;
    private final RideDetailsRepository rideDetailsRepository;

    @Transactional
    public RideDetails bookRide(BookRideRequest request, String idempotencyKey) throws NoDriverAvailableException {

        Optional<RideDetails> existingRide =
                rideDetailsRepository.findByIdempotencyKey(idempotencyKey);

        if (existingRide.isPresent()) {
            return existingRide.get();
        }

        userClient.getUser(request.getUserId());

        Driver driver = driverRepository.findFirstByAvailableTrueOrderByIdAsc()
                .orElseThrow(() ->
                        new NoDriverAvailableException(
                                "No driver available"));

        double fare = calculateFare(request.getDistance());

        RideDetails ride = new RideDetails();

        ride.setUserId(request.getUserId());
        ride.setDriver(driver);
        ride.setStartLocation(request.getStartLocation());
        ride.setEndLocation(request.getEndLocation());
        ride.setDistance(request.getDistance());
        ride.setFare(fare);
        ride.setRideStatus(RideStatus.ONGOING);
        ride.setIdempotencyKey(idempotencyKey);
        ride.setStartTime(LocalDateTime.now());

        driver.setAvailable(false);
//        driverRepository.save(driver);

        return rideDetailsRepository.save(ride);
    }

    private double calculateFare(Double distance) {
        return distance * 15;
    }
}