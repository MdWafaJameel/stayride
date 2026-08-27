package com.stayride.booking.controller;

import com.stayride.booking.dto.BookingResponse;
import com.stayride.booking.dto.CreateBookingRequest;
import com.stayride.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> bookRoom(
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication
    ) {

        Long userId = Long.parseLong(authentication.getName());

        BookingResponse booking = bookingService.bookRoom(
                userId,
                request.getRoomType(),
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {

        Long userId = Long.parseLong(authentication.getName());

        String response = bookingService.cancelBooking(
                bookingId,
                userId
        );

        return ResponseEntity.ok(response);
    }
}
