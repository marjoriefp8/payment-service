package com.mefp.payment.application.port.in;


import com.mefp.payment.application.dto.CreatePaymentResponse;

import java.util.UUID;

public interface GetPaymentUseCase {

    CreatePaymentResponse execute(UUID paymentId);
}