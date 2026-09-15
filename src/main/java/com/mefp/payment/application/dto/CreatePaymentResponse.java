package com.mefp.payment.application.dto;

import com.mefp.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        String status
) {

    public static CreatePaymentResponse from(Payment payment) {

        return new CreatePaymentResponse(
                payment.getId(),
                payment.getMoney().amount(),
                payment.getMoney().currency(),
                payment.getStatus().name()
        );
    }
}