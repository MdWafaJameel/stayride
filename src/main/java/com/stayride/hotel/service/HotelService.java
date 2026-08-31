package com.stayride.hotel.service;

import com.stayride.common.exception.ResourceNotFoundException;
import com.stayride.hotel.dto.HotelResponse;
import com.stayride.hotel.dto.RoomResponse;
import com.stayride.hotel.dto.UpdateHotelRequest;
import com.stayride.hotel.entity.Hotel;
import com.stayride.hotel.repository.HotelRepository;
import com.stayride.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Page<Hotel> getHotels(Pageable pageable) {
        return hotelRepository.findAll(pageable);
    }

    @Cacheable(value = "hotels", key = "#id")
    public HotelResponse getHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found: " + id));

        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getRating()
        );
    }

    @CacheEvict(value = "hotels", key = "#id")
    public HotelResponse updateHotel(Long id, UpdateHotelRequest request) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found: " + id));

        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setRating(request.getRating());

        hotelRepository.save(hotel);

        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getRating()
        );
    }

    public List<RoomResponse> getRooms(Long hotelId) {
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(r -> new RoomResponse(
                        r.getId(),
                        r.getRoomNumber(),
                        r.getType(),
                        r.getPricePerNight(),
                        r.getCapacity()
                ))
                .toList();
    }
}
