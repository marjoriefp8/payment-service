package com.mefp.payment.infrastructure.adapter.out.persistence.repository;

import com.mefp.payment.application.port.out.PaymentRepository;
import com.mefp.payment.domain.model.Money;
import com.mefp.payment.domain.model.Payment;
import com.mefp.payment.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryAdapter
        implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentRepositoryAdapter(
            PaymentJpaRepository paymentJpaRepository
    ) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public Payment save(Payment payment) {

        PaymentEntity entity = new PaymentEntity(
                payment.getId(),
                payment.getMoney().amount(),
                payment.getMoney().currency(),
                payment.getStatus()
        );

        PaymentEntity saved =
                paymentJpaRepository.save(entity);

        Money money = new Money(
                saved.getAmount(),
                saved.getCurrency()
        );

        return Payment.restore(
                saved.getId(),
                money,
                saved.getStatus()
        );
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {

        return paymentJpaRepository
                .findById(paymentId)
                .map(this::toDomain);
    }

    private Payment toDomain(PaymentEntity entity) {

        Money money = new Money(
                entity.getAmount(),
                entity.getCurrency()
        );

        return Payment.restore(
                entity.getId(),
                money,
                entity.getStatus()
        );
    }
}