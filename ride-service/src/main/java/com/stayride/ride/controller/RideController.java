package com.stayride.ride.controller;

import com.stayride.ride.dto.BookRideRequest;
import com.stayride.ride.dto.RideResponse;
import com.stayride.ride.entity.RideDetails;
import com.stayride.ride.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> bookRide(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody BookRideRequest request
    ) {

        RideDetails ride = rideService.bookRide(request, idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RideResponse.from(ride));
    }
}