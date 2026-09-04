package com.stayride.booking.service;

import com.stayride.booking.dto.BookingResponse;
import com.stayride.booking.entity.Booking;
import com.stayride.booking.entity.BookingStatus;
import com.stayride.booking.event.BookingCreatedEvent;
import com.stayride.booking.repository.BookingRepository;
import com.stayride.common.exception.ResourceNotAvailableException;
import com.stayride.common.exception.ResourceNotFoundException;
import com.stayride.common.outbox.OutboxEvent;
import com.stayride.common.outbox.OutboxEventRepository;
import com.stayride.hotel.entity.Room;
import com.stayride.hotel.repository.RoomRepository;
import com.stayride.user.entity.User;
import com.stayride.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public BookingResponse bookRoom(Long userId, String roomType, LocalDate checkInDate, LocalDate checkOutDate) {

        // 1. Validate dates
        if(checkOutDate.isBefore(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // 2. Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        // 3. Find rooms of requested type
        List<Room> rooms = roomRepository.findByType(roomType);

        // 4. Find first available room
        for (Room room : rooms) {

            boolean alreadyBooked = bookingRepository.existsOverlappingBooking(
                    room.getId(), checkInDate, checkOutDate
            );

            if (!alreadyBooked) {

                // 5. Calculate number of nights
                long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

                // 6. Calculate total amount
                double totalAmount = nights * room.getPricePerNight();

                // 7. Create booking
                Booking booking = new Booking();

                booking.setUser(user);
                booking.setRoom(room);
                booking.setCheckInDate(checkInDate);
                booking.setCheckOutDate(checkOutDate);
                booking.setStatus(BookingStatus.CONFIRMED);
                booking.setTotalAmount(totalAmount);

                // 8. Save booking
                Booking savedBooking = bookingRepository.save(booking);

                BookingCreatedEvent event = new BookingCreatedEvent(
                        savedBooking.getId(),
                        savedBooking.getUser().getId(),
                        savedBooking.getRoom().getId(),
                        savedBooking.getCheckInDate(),
                        savedBooking.getCheckOutDate(),
                        savedBooking.getTotalAmount()
                );

                try {
                    String payload = objectMapper.writeValueAsString(event);

                    OutboxEvent outboxEvent = OutboxEvent.builder()
                            .eventType("BOOKING_CREATED")
                            .aggregateType("BOOKING")
                            .aggregateId(savedBooking.getId())
                            .payload(payload)
                            .status("PENDING")
                            .createdAt(LocalDateTime.now())
                            .build();

                    outboxEventRepository.save(outboxEvent);

                } catch (Exception e) {
                    throw new RuntimeException("Failed to create outbox event", e);
                }

                return new BookingResponse(
                        savedBooking.getId(),
                        booking.getUser().getId(),
                        booking.getRoom().getId(),
                        booking.getRoom().getRoomNumber(),
                        booking.getRoom().getType(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getStatus(),
                        booking.getTotalAmount()
                );
            }
        }

        throw new ResourceNotAvailableException("No rooms available for the selected dates");
    }

    @Transactional
    public String cancelBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) throw new AccessDeniedException("Not your booking");

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);

        return "Booking cancelled successfully";
    }
}
