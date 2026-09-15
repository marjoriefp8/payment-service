package com.mefp.payment.infrastructure.adapter.out.persistence.entity;

import com.mefp.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    protected PaymentEntity() {
    }

    public PaymentEntity(
            UUID id,
            BigDecimal amount,
            String currency,
            PaymentStatus status
    ) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}