package com.stayride.hotel.service;

import com.stayride.common.exception.ResourceNotFoundException;
import com.stayride.hotel.entity.Hotel;
import com.stayride.hotel.entity.Room;
import com.stayride.hotel.repository.HotelRepository;
import com.stayride.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public Room createRoom(Long hotelId, Room room) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("hotel not found"));

        room.setHotel(hotel);

        return roomRepository.save(room);
    }
}
