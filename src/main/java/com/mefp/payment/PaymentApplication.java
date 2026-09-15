package com.mefp.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableKafka
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				PaymentApplication.class,
				args
		);
	}
}