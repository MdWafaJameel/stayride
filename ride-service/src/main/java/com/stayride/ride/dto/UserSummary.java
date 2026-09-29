package com.stayride.ride.dto;

public record UserSummary(
        Long id,
        String name,
        String email,
        String phone,
        String role
) {
}