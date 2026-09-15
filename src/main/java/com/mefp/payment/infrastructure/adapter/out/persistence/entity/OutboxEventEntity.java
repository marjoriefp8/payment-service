package com.mefp.payment.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private boolean published;

    @Column
    private Instant lockedUntil;




    protected OutboxEventEntity() {
    }

    public OutboxEventEntity(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String payload,
            Instant occurredAt
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.published = false;
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public boolean isPublished() {
        return published;
    }

    public void markAsPublished() {
        this.published = true;
    }
    public Instant getLockedUntil() {return lockedUntil;
    }
    public void lockUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public void unlock() {
        this.lockedUntil = null;
    }
}