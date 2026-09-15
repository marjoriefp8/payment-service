package com.mefp.payment.infrastructure.adapter.in.messaging;

import com.mefp.payment.application.event.PaymentCreated;
import com.mefp.payment.application.port.out.ProcessedEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentCreatedConsumer {

    private final ProcessedEventRepository processedEventRepository;

    public PaymentCreatedConsumer(
            ProcessedEventRepository processedEventRepository
    ) {
        this.processedEventRepository =
                processedEventRepository;
    }

    @KafkaListener(
            topics = "payment-created",
            groupId = "payment-service"
    )
    @Transactional
    public void consume(PaymentCreated event) {

        System.out.println(
                "EVENTO RECIBIDO: "
                        + event.eventId()
        );

        if (processedEventRepository.existsById(
                event.eventId()
        )) {

            System.out.println(
                    "EVENTO DUPLICADO. SE IGNORA: "
                            + event.eventId()
            );

            return;
        }

        System.out.println(
                "PROCESANDO EVENTO: "
                        + event
        );

        processedEventRepository.save(
                event.eventId()
        );

        System.out.println(
                "EVENTO PROCESADO Y REGISTRADO: "
                        + event.eventId()
        );
    }
}