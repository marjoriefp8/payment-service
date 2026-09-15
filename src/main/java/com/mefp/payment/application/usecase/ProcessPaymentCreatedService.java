package com.mefp.payment.application.usecase;

import com.mefp.payment.application.event.PaymentCreated;
import com.mefp.payment.application.port.in.PaymentCreatedUseCase;
import org.springframework.stereotype.Service;

@Service
public class ProcessPaymentCreatedService
        implements PaymentCreatedUseCase {

    @Override
    public void execute(PaymentCreated event) {

        System.out.println("=================================");
        System.out.println("PAYMENT EVENT RECEIVED");
        System.out.println("Event ID: " + event.eventId());
        System.out.println("Payment ID: " + event.paymentId());
        System.out.println("Amount: " + event.amount());
        System.out.println("Currency: " + event.currency());
        System.out.println("Status: " + event.status());
        System.out.println("Occurred At: " + event.occurredAt());
        System.out.println("=================================");
    }
}