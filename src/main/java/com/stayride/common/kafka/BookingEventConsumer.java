package com.stayride.common.kafka;

import com.stayride.booking.event.BookingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingEventConsumer {

    @KafkaListener(
            topics = "booking-created",
            groupId = "stayride-notification"
    )
    public void consume(BookingCreatedEvent event) {

        log.info("BOOKING CREATED: {}", event.getBookingId());
    }
}