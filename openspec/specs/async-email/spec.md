# Async Email Specification

## Purpose

Define async email delivery via Kafka events for the notification service. Covers event contract, email types, HTML templates, idempotency, and error handling.

## Requirements

### REQ-1: Event Contract

The system MUST define an `EmailEvent` data class with: `eventId` (UUID), `email` (String), `type` (EmailType), `name` (String), `token` (String?), `timestamp` (Instant).

#### Scenario: Publish and consume

- GIVEN a valid EmailEvent
- WHEN auth producer sends to `email-events` topic with recipient email as Kafka key
- THEN the notification consumer deserializes it via `spring.json.value.default.type`

### REQ-2: Email Types

The system MUST define an `EmailType` enum with WELCOME and PASSWORD_RESET.

#### Scenario: WELCOME on registration

- GIVEN customer registration succeeds
- WHEN RegisterCustomerUseCase completes
- THEN a WELCOME EmailEvent is published to `email-events`
- AND notification service sends `welcome.html`

#### Scenario: PASSWORD_RESET on request

- GIVEN valid password reset request
- WHEN auth publishes PASSWORD_RESET event
- THEN notification service sends `password-reset.html`

### REQ-3: HTML Templates

The system MUST render HTML templates with substitution variables for recipient `name` and action URL.

#### Scenario: Template rendering

- GIVEN a template with `{{name}}` and `{{actionUrl}}` placeholders
- WHEN rendered with valid values
- THEN the output HTML contains the substituted values

### REQ-4: Idempotency

The consumer MUST check `processed_events` table by `eventId` before processing. Duplicate events MUST be skipped.

#### Scenario: Duplicate skipped

- GIVEN `eventId` already in `processed_events`
- WHEN consumer receives the same `eventId`
- THEN no email sent, event logged as duplicate

#### Scenario: New event processed

- GIVEN `eventId` not in `processed_events`
- WHEN consumer processes the event
- THEN email sent and `eventId` persisted in `processed_events`

### REQ-5: Error Handling

Transient failures (SMTP timeout, network) MUST be retried up to 3 times with exponential backoff. Fatal errors (invalid type, missing template) MUST be logged and skipped.

#### Scenario: SMTP retry

- GIVEN a transient SMTP failure on email send
- WHEN the consumer retries
- THEN up to 3 retries with exponential backoff before giving up

#### Scenario: Invalid event skipped

- GIVEN an event with an unknown EmailType
- WHEN the consumer receives it
- THEN the event is logged as invalid and skipped
