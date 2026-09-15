package com.mefp.payment.infrastructure.adapter.out.messaging;

import com.mefp.payment.application.event.PaymentCreated;
import com.mefp.payment.application.port.out.PaymentEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class KafkaPaymentEventPublisher
        implements PaymentEventPublisher {

    private final KafkaTemplate<String, PaymentCreated> kafkaTemplate;

    private static final String TOPIC = "payment-created";

    public KafkaPaymentEventPublisher(
            KafkaTemplate<String, PaymentCreated> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(PaymentCreated event) {

        try {

            kafkaTemplate.send(
                    TOPIC,
                    event.paymentId().toString(),
                    event
            ).get();

            System.out.println(
                    "KAFKA CONFIRMO EVENTO: "
                            + event.paymentId()
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Error publicando evento en Redpanda",
                    e
            );
        }
    }
}