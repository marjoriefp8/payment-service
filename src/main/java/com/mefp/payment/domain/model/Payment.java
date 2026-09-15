package com.mefp.payment.domain.model;

import java.util.UUID;

public class Payment {

    private final UUID id;
    private final Money money;
    private PaymentStatus status;

    public Payment(
            UUID id,
            Money money
    ) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El id es obligatorio"
            );
        }

        if (money == null) {
            throw new IllegalArgumentException(
                    "El dinero es obligatorio"
            );
        }

        this.id = id;
        this.money = money;
        this.status = PaymentStatus.PENDING;
    }

    private Payment(
            UUID id,
            Money money,
            PaymentStatus status
    ) {

        this.id = id;
        this.money = money;
        this.status = status;
    }

    public static Payment restore(
            UUID id,
            Money money,
            PaymentStatus status
    ) {

        return new Payment(
                id,
                money,
                status
        );
    }

    public UUID getId() {
        return id;
    }

    public Money getMoney() {
        return money;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}