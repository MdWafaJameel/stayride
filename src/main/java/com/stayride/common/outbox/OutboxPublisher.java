package com.stayride.common.outbox;

import com.stayride.booking.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop100ByStatusOrderByIdAsc("PENDING");

        for (OutboxEvent event : events) {

            try {

                BookingCreatedEvent bookingEvent =
                        objectMapper.readValue(
                                event.getPayload(),
                                BookingCreatedEvent.class
                        );

                kafkaTemplate.send(
                        "booking-created",
                        event.getAggregateId().toString(),
                        bookingEvent
                ).whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.debug("OUTBOX KAFKA PUBLISH FAILED: {}", ex.getMessage());
                    } else {

                        event.setStatus("SENT");
                        event.setProcessedAt(LocalDateTime.now());

                        outboxEventRepository.save(event);

                        log.info("OUTBOX EVENT PUBLISHED: {}", event.getId());
                    }
                });

            } catch (Exception e) {

                log.error("OUTBOX PROCESSING FAILED: {}", event.getId());
            }
        }
    }
}