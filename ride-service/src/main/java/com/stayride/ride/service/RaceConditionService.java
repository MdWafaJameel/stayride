package com.stayride.ride.service;

import org.springframework.stereotype.Service;

@Service
public class RaceConditionService {

    private int availableDrivers = 1;

    public synchronized void bookDriver() {

        if (availableDrivers > 0) {

            System.out.println(
                    Thread.currentThread().getName()
                            + " found driver available"
            );

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            availableDrivers--;

            System.out.println(
                    Thread.currentThread().getName()
                            + " booked driver"
            );
        } else {
            System.out.println(
                    Thread.currentThread().getName()
                            + " found no driver"
            );
        }
    }
}