# Payment Service

A payment microservice built with **Java 21 and Spring Boot**, designed to demonstrate modern microservice architecture, event-driven communication, reliable event publishing, and idempotent event processing.

The project uses **Hexagonal Architecture**, **PostgreSQL**, the **Outbox Pattern**, and **Redpanda/Kafka** to provide reliable asynchronous communication between components.

---

## Architecture Overview

The service follows a Hexagonal Architecture (Ports and Adapters) approach.

```text
                         ┌─────────────────────┐
                         │     REST Client     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Payment Controller  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Application Layer   │
                         │                     │
                         │ CreatePaymentService│
                         └──────────┬──────────┘
                                    │
                         ┌──────────┴──────────┐
                         │                     │
                         ▼                     ▼
                ┌─────────────────┐   ┌──────────────────┐
                │ Payment          │   │ Outbox Event     │
                │ Repository       │   │ Repository       │
                └────────┬────────┘   └────────┬─────────┘
                         │                     │
                         └──────────┬──────────┘
                                    ▼
                         ┌─────────────────────┐
                         │     PostgreSQL      │
                         │                     │
                         │ payments            │
                         │ outbox_events       │
                         │ processed_events    │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │  Outbox Publisher   │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Redpanda / Kafka    │
                         │                     │
                         │ payment-created     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ PaymentCreated      │
                         │ Consumer            │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ processed_events    │
                         │                     │
                         │ Idempotency check   │
                         └─────────────────────┘
Key Features
Create payments through a REST API.
Validate payment amount and currency.
Persist payments in PostgreSQL.
Publish payment events asynchronously.
Use the Outbox Pattern for reliable event publishing.
Use Redpanda/Kafka for event-driven communication.
Consume PaymentCreated events.
Prevent duplicate event processing using an idempotency mechanism.
Support multiple application instances.
Handle concurrent Outbox processing using PostgreSQL row locking.
Use SKIP LOCKED to allow concurrent Outbox workers to process different events.
Use an event lease mechanism with locked_until.
Demonstrate at-least-once event delivery.
Technologies
Technology	Purpose
Java 21	Programming language
Spring Boot	Application framework
Spring Data JPA	Persistence
Spring Kafka	Kafka/Redpanda integration
PostgreSQL	Relational database
Redpanda	Event streaming platform
Docker	Infrastructure
Maven	Build and dependency management
Project Structure
src/main/java/com/mefp/payment
│
├── domain
│   └── model
│       ├── Money.java
│       ├── Payment.java
│       └── PaymentStatus.java
│
├── application
│   ├── command
│   │   └── CreatePaymentCommand.java
│   │
│   ├── dto
│   │   └── CreatePaymentResponse.java
│   │
│   ├── event
│   │   └── PaymentCreated.java
│   │
│   ├── port
│   │   ├── in
│   │   │   └── CreatePaymentUseCase.java
│   │   │
│   │   └── out
│   │       ├── PaymentRepository.java
│   │       ├── PaymentEventPublisher.java
│   │       ├── OutboxEventRepository.java
│   │       └── ProcessedEventRepository.java
│   │
│   └── usecase
│       └── CreatePaymentService.java
│
└── infrastructure
    └── adapter
        ├── in
        │   ├── rest
        │   │   └── PaymentController.java
        │   │
        │   └── messaging
        │       └── PaymentCreatedConsumer.java
        │
        └── out
            ├── persistence
            │   ├── entity
            │   │   ├── PaymentEntity.java
            │   │   ├── OutboxEventEntity.java
            │   │   └── ProcessedEventEntity.java
            │   │
            │   └── repository
            │       ├── PaymentJpaRepository.java
            │       ├── PaymentRepositoryAdapter.java
            │       ├── OutboxEventJpaRepository.java
            │       ├── OutboxEventRepositoryAdapter.java
            │       ├── ProcessedEventJpaRepository.java
            │       └── ProcessedEventRepositoryAdapter.java
            │
            └── messaging
                ├── KafkaPaymentEventPublisher.java
                └── OutboxPublisher.java
Domain Model
Payment

A Payment represents the core business entity.

It contains:

Payment ID
Money
Payment status

A newly created payment starts with the PENDING status.

Money

Money is represented as a Java record:

public record Money(
    BigDecimal amount,
    String currency
)

The domain validates:

Amount cannot be null.
Amount must be greater than zero.
Currency cannot be empty.
Currency must contain exactly three characters.
Currency is normalized to uppercase.

Example:

250 USD
Application Layer

The application layer contains the use cases and application contracts.

The main use case is:

CreatePaymentUseCase

implemented by:

CreatePaymentService

The service coordinates:

Payment creation.
Payment persistence.
Event creation.
Outbox persistence.

The payment and its Outbox event are persisted within the same database transaction.

Outbox Pattern

The project uses the Transactional Outbox Pattern to avoid losing events when database and messaging operations are performed separately.

Without the Outbox Pattern, a failure could occur like this:

Save payment
     ↓
Payment successfully stored
     ↓
Application tries to publish event
     ↓
Kafka unavailable
     ↓
Event lost

With the Outbox Pattern:

┌─────────────────────────────┐
│ PostgreSQL Transaction      │
│                             │
│ Save Payment                │
│        +                    │
│ Save Outbox Event           │
│                             │
└─────────────────────────────┘
              ↓
       Transaction Commit
              ↓
       Outbox Publisher
              ↓
          Redpanda

The payment and the event are persisted together.

Outbox Processing

The OutboxPublisher periodically searches for unpublished events.

The Outbox table contains information such as:

id
aggregate_id
aggregate_type
event_type
payload
occurred_at
published
locked_until

Pending events can be selected using PostgreSQL row locking:

FOR UPDATE SKIP LOCKED

This allows multiple application instances to process different Outbox records concurrently.

The project also uses:

locked_until

as a temporary lease mechanism for an event being processed.

Event-Driven Communication

The payment service publishes a:

PaymentCreated

event.

The event contains:

eventId
paymentId
amount
currency
status
occurredAt

The event is published to:

payment-created

using Redpanda/Kafka.

Event Flow

When a payment is created:

POST /payments
       │
       ▼
PaymentController
       │
       ▼
CreatePaymentService
       │
       ├───────────────┐
       ▼               ▼
   Payment         PaymentCreated
       │               │
       ▼               ▼
 PostgreSQL       Outbox Event
                       │
                       ▼
                OutboxPublisher
                       │
                       ▼
                 Redpanda/Kafka
                       │
                       ▼
                payment-created
                       │
                       ▼
              PaymentCreatedConsumer
                       │
                       ▼
                processed_events
Idempotent Consumer

Distributed systems can deliver the same event more than once.

For this reason, the consumer does not assume that every received event is new.

Before processing an event, the consumer checks:

processed_events

using the event ID.

The logic is:

Receive event
      │
      ▼
Does event ID already exist?
      │
   ┌──┴───┐
   │      │
  YES     NO
   │      │
   ▼      ▼
 Ignore  Process
         │
         ▼
  Save event ID

Example:

EVENTO RECIBIDO: 36eb940c...

EVENTO DUPLICADO. SE IGNORA:
36eb940c...

This provides protection against duplicate event processing.

Delivery Semantics

This project intentionally demonstrates an at-least-once delivery model.

The Outbox Publisher ensures that an event is retried when necessary.

However, there is an important distributed-systems consideration:

Publish event to Kafka
        ↓
Kafka confirms publication
        ↓
Application fails before updating PostgreSQL
        ↓
Outbox event may be published again

Therefore, the system does not rely on exactly-once delivery.

Instead, it combines:

At-least-once delivery
        +
Idempotent consumer

This is a common approach for reliable event-driven systems.

Concurrency

The application was tested using two running instances:

Instance 1 → port 8080
Instance 2 → port 8081

Both instances process the Outbox concurrently.

PostgreSQL row locking with:

FOR UPDATE SKIP LOCKED

allows different pending events to be handled by different application instances.

Example:

                PostgreSQL
              outbox_events
                   │
          ┌────────┴────────┐
          │                 │
       Event A           Event B
          │                 │
          ▼                 ▼
     Instance 1         Instance 2
          │                 │
          ▼                 ▼
       Redpanda           Redpanda

This allows the Outbox processing workload to be distributed across application instances.

Database

The application uses PostgreSQL.

Main tables:

payments

Stores payment information.

id
amount
currency
status
outbox_events

Stores events that need to be published.

id
aggregate_id
aggregate_type
event_type
payload
occurred_at
published
locked_until
processed_events

Stores event IDs that have already been processed.

event_id
processed_at
Running the Project
Prerequisites

Make sure the following are installed:

Java 21
Maven
Docker Desktop
Git
Start Infrastructure

Start PostgreSQL and Redpanda using Docker Compose:

docker compose up -d

Verify the containers:

docker ps

The application expects:

PostgreSQL → localhost:5432
Redpanda   → localhost:9092
Create the Kafka Topic

The application uses the following topic:

payment-created

If the topic does not exist, create it using Redpanda:

rpk topic create payment-created
Run the Application

Using Maven:

mvn spring-boot:run

The default application port is:

8080
Running Multiple Instances

To run a second instance:

mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"

This allows the application to run simultaneously on:

http://localhost:8080
http://localhost:8081

The two instances can participate in concurrent Outbox processing.

API
Create Payment
Request
POST /payments
Content-Type: application/json

Example:

{
  "amount": 700,
  "currency": "USD"
}
Example Response
{
  "id": "c9667429-8a68-4f38-8d4a-eac050f3a9d2",
  "amount": 700,
  "currency": "USD",
  "status": "PENDING"
}
Example Event

A payment generates a PaymentCreated event.

Example structure:

{
  "eventId": "7909ac6c-6f09-486f-a00b-520cf4b260d0",
  "paymentId": "46cf1118-46be-4184-99b4-eea1271f3ee6",
  "amount": 700,
  "currency": "USD",
  "status": "PENDING",
  "occurredAt": "2026-09-15T14:52:48Z"
}
Testing Scenarios

The project has been tested with several scenarios.

Payment Creation

A payment is successfully created and persisted.

Payment
   ↓
PostgreSQL
Event Publication

The Outbox Publisher successfully publishes the event:

KAFKA CONFIRMO EVENTO: ...
EVENTO PUBLICADO: ...
Event Consumption

The consumer receives and processes the event:

EVENTO RECIBIDO: ...

PROCESANDO EVENTO: PaymentCreated[...]

EVENTO PROCESADO Y REGISTRADO: ...
Duplicate Event

The same event can be received more than once.

The idempotency mechanism detects the duplicate:

EVENTO RECIBIDO: ...

EVENTO DUPLICADO. SE IGNORA: ...

The event is not processed a second time.

Concurrent Outbox Processing

Two application instances were executed simultaneously:

Instance 1 → 8080
Instance 2 → 8081

The Outbox processing was tested using PostgreSQL row locking and:

FOR UPDATE SKIP LOCKED

The test demonstrated that different pending Outbox events can be processed concurrently by different application instances.

Design Patterns

This project demonstrates several important architectural patterns.

Hexagonal Architecture

Separates the business logic from infrastructure concerns.

Domain
  ↑
Application
  ↑
Infrastructure
Transactional Outbox

Ensures that business data and the corresponding event are persisted together.

Idempotent Consumer

Prevents duplicate event processing.

Event-Driven Architecture

Components communicate asynchronously through events.

Ports and Adapters

The application depends on interfaces rather than infrastructure implementations.

For example:

PaymentRepository
PaymentEventPublisher
OutboxEventRepository
ProcessedEventRepository

Infrastructure adapters implement those interfaces.

Reliability Strategy

The reliability strategy can be summarized as:

                  Payment
                     │
                     ▼
              PostgreSQL
                     │
            ┌────────┴────────┐
            │                 │
         Payment          Outbox Event
                              │
                              ▼
                       Outbox Publisher
                              │
                              ▼
                         Redpanda
                              │
                              ▼
                           Consumer
                              │
                              ▼
                     processed_events

The main reliability mechanisms are:

Database transaction.
Transactional Outbox.
Event retry through unpublished Outbox records.
Concurrent processing with PostgreSQL locking.
Event idempotency.
At-least-once delivery.
Configuration

The application uses PostgreSQL and Redpanda/Kafka through Spring configuration.

Example:

spring:
  kafka:
    bootstrap-servers: localhost:9092

  datasource:
    url: jdbc:postgresql://localhost:5432/payments
    username: payment
    password: payment

  jpa:
    hibernate:
      ddl-auto: update

For production environments, credentials and infrastructure configuration should be provided through environment variables or a secure configuration mechanism rather than committed to the repository.

Future Improvements

Possible future improvements include:

Replace hard-coded infrastructure configuration with environment variables.
Add integration tests using Testcontainers.
Add unit tests for domain and application layers.
Add automated API documentation with OpenAPI/Swagger.
Add health checks and observability.
Add metrics and distributed tracing.
Improve Outbox leasing and retry management.
Add dead-letter handling for events that repeatedly fail.
Add CI/CD with GitHub Actions.
Add authentication and authorization.
Add more payment operations such as cancellation or refund.
Learning Goals

This project was created to gain practical experience with:

Microservice architecture.
Hexagonal Architecture.
Domain-driven design concepts.
Spring Boot.
Spring Data JPA.
PostgreSQL.
Event-driven architecture.
Kafka-compatible event streaming with Redpanda.
Transactional Outbox Pattern.
Idempotent consumers.
Distributed processing.
Database locking.
Concurrent application instances.
At-least-once delivery semantics.
Docker-based infrastructure.
Author

Marjorie Fiallos

This project is part of a hands-on learning journey focused on Java, Spring Boot, microservices, event-driven architecture, and distributed systems.

License

This project is intended for educational and portfolio purposes.



