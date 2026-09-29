package com.stayride.user.controller;

import com.stayride.user.dto.CreateHotelAdminRequest;
import com.stayride.user.dto.CreateUserRequest;
import com.stayride.user.dto.UserResponse;
import com.stayride.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping("/hotel-admins")
    public ResponseEntity<UserResponse> createHotelAdmin(
            @Valid @RequestBody CreateHotelAdminRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createHotelAdmin(request));
    }
}
