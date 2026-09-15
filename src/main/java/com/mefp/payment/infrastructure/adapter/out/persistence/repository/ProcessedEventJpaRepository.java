package com.mefp.payment.infrastructure.adapter.out.persistence.repository;

import com.mefp.payment.infrastructure.adapter.out.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventJpaRepository
        extends JpaRepository<ProcessedEventEntity, UUID> {
}