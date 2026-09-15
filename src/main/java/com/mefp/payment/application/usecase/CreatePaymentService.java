package com.mefp.payment.application.usecase;

import com.mefp.payment.application.command.CreatePaymentCommand;
import com.mefp.payment.application.dto.CreatePaymentResponse;
import com.mefp.payment.application.event.PaymentCreated;
import com.mefp.payment.application.port.in.CreatePaymentUseCase;
import com.mefp.payment.application.port.out.OutboxEventRepository;
import com.mefp.payment.application.port.out.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import com.mefp.payment.domain.model.Money;
import com.mefp.payment.domain.model.Payment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class CreatePaymentService
        implements CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxEventRepository;

    public CreatePaymentService(
            PaymentRepository paymentRepository,
            OutboxEventRepository outboxEventRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Override
    @Transactional
    public CreatePaymentResponse execute(
            CreatePaymentCommand command
    ) {

        // 1. Crear Money
        Money money = new Money(
                command.getAmount(),
                command.getCurrency()
        );

        // 2. Crear Payment
        Payment payment = new Payment(
                UUID.randomUUID(),
                money
        );

        // 3. Guardar Payment
        Payment savedPayment =
                paymentRepository.save(payment);

        // 4. Crear evento
        PaymentCreated event = new PaymentCreated(
                UUID.randomUUID(),
                savedPayment.getId(),
                savedPayment.getMoney().amount(),
                savedPayment.getMoney().currency(),
                savedPayment.getStatus().name(),
                Instant.now()
        );

        // 5. Guardar evento en Outbox
        outboxEventRepository.save(
                "PaymentCreated",
                savedPayment.getId().toString(),
                event
        );

        // 6. Devolver respuesta
        return CreatePaymentResponse.from(savedPayment);
    }
}