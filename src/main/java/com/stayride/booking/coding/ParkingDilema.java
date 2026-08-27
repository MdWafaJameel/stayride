package com.stayride.booking.coding;

import java.util.Arrays;

public class ParkingDilema {

    public static long carParkingRoof(long[] cars, int k) {

        // Sort the positions of the cars
        Arrays.sort(cars);

        long minimumLength = Long.MAX_VALUE;

        /*
         * After sorting, consider every group of k consecutive cars.
         *
         * For cars from index i to i + k - 1:
         *
         * The roof starts at cars[i]
         * and ends at cars[i + k - 1]
         *
         * Because both endpoints are included:
         *
         * length = end - start + 1
         */
        for (int i = 0; i <= cars.length - k; i++) {

            long start = cars[i];
            long end = cars[i + k - 1];

            long roofLength = end - start + 1;

            minimumLength = Math.min(minimumLength, roofLength);
        }

        return minimumLength;
    }

    public static void main(String[] args) {

        long[] cars = {6, 2, 12, 7};
        int k = 3;

        long result = carParkingRoof(cars, k);

        System.out.println("Minimum roof length: " + result);
    }
}

