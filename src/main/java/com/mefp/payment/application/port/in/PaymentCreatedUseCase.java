package com.mefp.payment.application.port.in;

import com.mefp.payment.application.event.PaymentCreated;

public interface PaymentCreatedUseCase {

    void execute(PaymentCreated event);
}