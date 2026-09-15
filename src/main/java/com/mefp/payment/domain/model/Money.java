package com.mefp.payment.domain.model;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount,
        String currency
) {

    public Money {

        if (amount == null) {
            throw new IllegalArgumentException(
                    "El monto es obligatorio"
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
            );
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                    "La moneda es obligatoria"
            );
        }

        currency = currency.toUpperCase();

        if (currency.length() != 3) {
            throw new IllegalArgumentException(
                    "La moneda debe tener 3 caracteres"
            );
        }
    }
}