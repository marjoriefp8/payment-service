package com.mefp.payment.application.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCreated(
        UUID eventId,
        UUID paymentId,
        BigDecimal amount,
        String currency,
        String status,
        Instant occurredAt
) {
}