package com.stayride.ride.controller;

import com.stayride.ride.dto.BookRideRequest;
import com.stayride.ride.dto.RideResponse;
import com.stayride.ride.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> bookRide(
            @Valid @RequestBody BookRideRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rideService.bookRide(request));
    }

    @PutMapping("/{rideId}/end")
    public ResponseEntity<RideResponse> endRide(
            @PathVariable Long rideId) {

        return ResponseEntity.ok(
                rideService.endRide(rideId));
    }

    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<RideResponse>> recentRides(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                rideService.getRecentCompletedRides(userId));
    }
}
