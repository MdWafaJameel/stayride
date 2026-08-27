package com.stayride.ride.repository;

import com.stayride.ride.entity.RideDetails;
import com.stayride.ride.entity.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RideDetailsRepository
        extends JpaRepository<RideDetails, Long> {

    List<RideDetails> findByUserIdAndRideStatusAndEndTimeAfter(
            Long userId,
            RideStatus rideStatus,
            LocalDateTime time
    );
}