package com.mefp.payment.infrastructure.adapter.out.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mefp.payment.application.port.out.OutboxEventRepository;
import com.mefp.payment.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OutboxEventRepositoryAdapter
        implements OutboxEventRepository {

    private final OutboxEventJpaRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxEventRepositoryAdapter(
            OutboxEventJpaRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(
            String eventType,
            String aggregateId,
            Object event
    ) {

        try {

            String payload =
                    objectMapper.writeValueAsString(event);

            OutboxEventEntity entity =
                    new OutboxEventEntity(
                            UUID.randomUUID(),
                            "Payment",
                            UUID.fromString(aggregateId),
                            eventType,
                            payload,
                            Instant.now()
                    );

            repository.save(entity);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Error serializando evento para Outbox",
                    e
            );
        }
    }
}