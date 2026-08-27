package com.stayride.user.dto;

import com.stayride.user.entity.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        Role role
) {}
