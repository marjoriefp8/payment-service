package com.mefp.payment.application.usecase;

import com.mefp.payment.application.dto.CreatePaymentResponse;
import com.mefp.payment.application.port.in.GetPaymentUseCase;
import com.mefp.payment.application.port.out.PaymentRepository;
import com.mefp.payment.domain.model.Payment;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPaymentService
        implements GetPaymentUseCase {

    private final PaymentRepository paymentRepository;

    public GetPaymentService(
            PaymentRepository paymentRepository
    ) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CreatePaymentResponse execute(
            UUID paymentId
    ) {

        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pago no encontrado"
                                )
                        );

        return CreatePaymentResponse.from(payment);
    }
}