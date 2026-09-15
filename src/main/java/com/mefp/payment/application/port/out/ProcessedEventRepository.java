package com.mefp.payment.application.port.out;

import java.util.UUID;

public interface ProcessedEventRepository {

    boolean existsById(UUID eventId);

    void save(UUID eventId);
}
