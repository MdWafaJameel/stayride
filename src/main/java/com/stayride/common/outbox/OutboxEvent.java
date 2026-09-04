package com.stayride.common.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String aggregateType;

    private Long aggregateId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;
}