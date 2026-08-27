package com.stayride.ride.controller;

import com.stayride.ride.entity.Driver;
import com.stayride.ride.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<Driver> createDriver(
            @RequestBody Driver driver) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(driverService.createDriver(driver));
    }
}