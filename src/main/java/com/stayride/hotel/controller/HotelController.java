package com.stayride.hotel.controller;

import com.stayride.hotel.dto.HotelResponse;
import com.stayride.hotel.dto.RoomResponse;
import com.stayride.hotel.entity.Hotel;
import com.stayride.hotel.entity.Room;
import com.stayride.hotel.service.HotelService;
import com.stayride.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final RoomService roomService;
    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(hotel));
    }

    @GetMapping
    public ResponseEntity<Page<Hotel>> getHotels(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(hotelService.getHotels(pageable));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponse> getHotel(
            @PathVariable Long hotelId) {

        return ResponseEntity.ok(hotelService.getHotel(hotelId));
    }

    @PostMapping("/{hotelId}/rooms")
    public ResponseEntity<Room> createRoom(
            @PathVariable Long hotelId,
            @RequestBody Room room) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(hotelId, room));
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRooms(
            @PathVariable Long hotelId) {

        return ResponseEntity.ok(hotelService.getRooms(hotelId));
    }
}
