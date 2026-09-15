package com.mefp.payment.infrastructure.adapter.out.persistence.repository;

import com.mefp.payment.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository
        extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findByPublishedFalse();

    @Query(value = """
        SELECT *
        FROM outbox_events
        WHERE published = false
          AND (
              locked_until IS NULL
              OR locked_until < CURRENT_TIMESTAMP
          )
        ORDER BY occurred_at
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxEventEntity> findPendingEventsForUpdate();
}