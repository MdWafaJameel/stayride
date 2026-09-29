package com.stayride.ride.service;

import com.stayride.ride.entity.Driver;
import com.stayride.ride.exception.DriverNotFoundException;
import com.stayride.ride.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    @Transactional
    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver not found with id: " + id));
    }

    @Transactional
    public Driver updateDriver(Long id, Driver updatedDriver) {

        Driver existingDriver = getDriverById(id);

        existingDriver.setName(updatedDriver.getName());
        existingDriver.setAvailable(updatedDriver.isAvailable());
        existingDriver.setRating(updatedDriver.getRating());
        existingDriver.setVehicleDetails(updatedDriver.getVehicleDetails());

        return driverRepository.save(existingDriver);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
    }
}