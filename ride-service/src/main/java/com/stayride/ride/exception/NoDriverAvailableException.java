package com.stayride.ride.exception;

public class NoDriverAvailableException extends RuntimeException {

    public NoDriverAvailableException(String message) {
        super(message);
    }
}