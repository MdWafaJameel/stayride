package com.stayride.hotel.repository;

import com.stayride.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByType(String roomType);

    List<Room> findByHotelId(Long hotelId);
}
