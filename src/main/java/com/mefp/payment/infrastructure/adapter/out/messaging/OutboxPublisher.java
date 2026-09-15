package com.mefp.payment.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mefp.payment.application.event.PaymentCreated;
import com.mefp.payment.application.port.out.PaymentEventPublisher;
import com.mefp.payment.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import com.mefp.payment.infrastructure.adapter.out.persistence.repository.OutboxEventJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventJpaRepository repository;
    private final PaymentEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventJpaRepository repository,
            PaymentEventPublisher eventPublisher,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {

        List<OutboxEventEntity> events =
                repository.findPendingEventsForUpdate();

        for (OutboxEventEntity event : events) {

            try {

                PaymentCreated paymentCreated =
                        objectMapper.readValue(
                                event.getPayload(),
                                PaymentCreated.class
                        );
                event.lockUntil(
                        Instant.now().plusSeconds(30)
                );

                repository.save(event);

                System.out.println(
                        "EVENTO RECLAMADO: "
                                + event.getId()
                                + " HASTA "
                                + event.getLockedUntil()
                );

                eventPublisher.publish(paymentCreated);

                event.markAsPublished();

                repository.save(event);

                System.out.println(
                        "EVENTO PUBLICADO: "
                                + event.getId()
                );

            } catch (Exception e) {

                System.err.println(
                        "ERROR PUBLICANDO EVENTO: "
                                + event.getId()
                );

                e.printStackTrace();
            }
        }
    }
}