package com.stayride.ride.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "drivers",
        indexes = {
                @Index(
                        name = "idx_driver_available_id",
                        columnList = "available, id"
                )
        }
)
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean available;

    private Double rating;

    private String vehicleDetails;
}