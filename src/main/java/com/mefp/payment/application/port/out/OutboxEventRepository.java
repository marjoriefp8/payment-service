package com.mefp.payment.application.port.out;


public interface OutboxEventRepository {

    void save(
            String eventType,
            String aggregateId,
            Object event
    );
}