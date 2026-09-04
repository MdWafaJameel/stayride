package com.stayride.common.kafka;

import com.stayride.booking.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    public void publish(BookingCreatedEvent event) {

        kafkaTemplate.send(
                "booking-created",
                event.getBookingId().toString(),
                event
        ).whenComplete((result, ex) -> {

            if (ex != null) {
                log.debug("KAFKA PUBLISH FAILED: {}", ex.getMessage());
            } else {
                log.info("KAFKA PUBLISHED: topic={}, partition={}, offset={}", result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });
    }
}