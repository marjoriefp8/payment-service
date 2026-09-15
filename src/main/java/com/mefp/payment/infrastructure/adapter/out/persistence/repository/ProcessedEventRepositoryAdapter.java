package com.mefp.payment.infrastructure.adapter.out.persistence.repository;

import com.mefp.payment.application.port.out.ProcessedEventRepository;
import com.mefp.payment.infrastructure.adapter.out.persistence.entity.ProcessedEventEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ProcessedEventRepositoryAdapter
        implements ProcessedEventRepository {

    private final ProcessedEventJpaRepository repository;

    public ProcessedEventRepositoryAdapter(
            ProcessedEventJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(UUID eventId) {

        return repository.existsById(eventId);
    }

    @Override
    public void save(UUID eventId) {

        ProcessedEventEntity entity =
                new ProcessedEventEntity(
                        eventId,
                        Instant.now()
                );

        repository.save(entity);
    }
}