package com.mefp.payment.infrastructure.adapter.in.rest;

import com.mefp.payment.application.command.CreatePaymentCommand;
import com.mefp.payment.application.dto.CreatePaymentRequest;
import com.mefp.payment.application.dto.CreatePaymentResponse;
import com.mefp.payment.application.port.in.CreatePaymentUseCase;
import com.mefp.payment.application.port.in.GetPaymentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;

    public PaymentController(
            CreatePaymentUseCase createPaymentUseCase,
            GetPaymentUseCase getPaymentUseCase
    ) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.getPaymentUseCase = getPaymentUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePaymentResponse createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {

        System.out.println(">>> ENTRE AL CONTROLLER");
        System.out.println(">>> amount = " + request.amount());
        System.out.println(">>> currency = " + request.currency());
        CreatePaymentCommand command =
                new CreatePaymentCommand(
                        request.amount(),
                        request.currency()
                );

        return createPaymentUseCase.execute(command);
    }

    @GetMapping("/{id}")
    public CreatePaymentResponse getPayment(
            @PathVariable UUID id
    ) {

        return getPaymentUseCase.execute(id);
    }
}