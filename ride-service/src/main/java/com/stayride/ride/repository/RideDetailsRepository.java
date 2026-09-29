package com.stayride.ride.repository;

import com.stayride.ride.entity.RideDetails;
import com.stayride.ride.entity.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideDetailsRepository extends JpaRepository<RideDetails, Long> {

    Optional<RideDetails> findByIdempotencyKey(String idempotencyKey);

    List<RideDetails> findByUserIdAndRideStatusAndEndTimeAfter(
            Long userId,
            RideStatus rideStatus,
            LocalDateTime time
    );
}