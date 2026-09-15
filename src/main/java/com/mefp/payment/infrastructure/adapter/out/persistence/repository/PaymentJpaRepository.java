package com.mefp.payment.infrastructure.adapter.out.persistence.repository;

import com.mefp.payment.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository
        extends JpaRepository<PaymentEntity, UUID> {
}