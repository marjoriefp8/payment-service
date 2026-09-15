package com.mefp.payment.application.port.out;

import com.mefp.payment.application.event.PaymentCreated;

public interface PaymentEventPublisher {

    void publish(PaymentCreated event);
}