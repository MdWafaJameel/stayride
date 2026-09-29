package com.stayride.ride.repository;

import com.stayride.ride.entity.Driver;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Driver> findFirstByAvailableTrueOrderByIdAsc();
}