package com.mefp.payment.application.port.in;

import com.mefp.payment.application.command.CreatePaymentCommand;
import com.mefp.payment.application.dto.CreatePaymentResponse;


public interface CreatePaymentUseCase {

    CreatePaymentResponse execute(
            CreatePaymentCommand command
    );
}