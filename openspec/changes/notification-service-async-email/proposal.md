# Proposal: Notification Service — Async Email via Kafka

## Intent

Decouple email sending from auth registration so SMTP failures don't block signups. Extract email responsibility into a dedicated notification microservice consumed via Kafka. Add password reset flow.

## Scope

### In Scope
- `services/notification`: Spring Boot, hexagonal — Kafka consumer, JavaMailSender, HTML templates (welcome, password-reset), ProcessedEvent idempotency, KafkaConfig
- Auth: `spring-kafka` dep, KafkaConfig (producer), EmailEventProducer → `email-events` topic
- Auth: `RegisterCustomerUseCase` publishes event instead of calling `EmailSenderPort`
- Auth: Password reset — `POST /password-reset` (generate token + email) + `POST /password-reset/confirm` (validate + update)
- Docker: notification service in docker-compose.yml
- Tests: integration test with Kafka test container

### Out of Scope
In-app notifications (WebSocket/push), SMS, templates admin UI, retry/DLQ infrastructure, user notification preferences

## Capabilities

### New
- `async-email`: Async email via Kafka — welcome, password-reset, and future email types

### Modified
- `customer-auth`: R1 (registration) — sync email → async event publish. ADD R11 (request reset), R12 (confirm reset)

## Approach

Mirror existing Kafka pattern (sales/inventory/billing):

**Notification service**: `KafkaConfig` (consumer-only, `siga-notification` group), `EmailEventConsumer` with `@KafkaListener`, `ProcessedEvent` entity + repository for idempotency, `EmailSenderService` with `JavaMailSender` + HTML templates. Topic: `email-events`, partition key: recipient email.

**Auth**: `KafkaConfig` (producer-only), `EmailEventProducer` publishing to `email-events`, `EmailEvent` data class (eventId, email, type, token, name, timestamp). Password reset: generate scoped token → persist (15min expiry) → publish event → consumer sends email.

**Idempotency**: UUID `eventId` on every event → consumer checks `processed_events` before processing.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `services/notification/` | NEW | Full service scaffold |
| `services/auth/.../RegisterCustomerUseCase.kt` | MOD | Publish event instead of calling port |
| `services/auth/build.gradle.kts` | MOD | Add `spring-kafka` |
| `services/auth/.../AuthController.kt` | MOD | Add password-reset endpoints |
| `services/auth/.../event/` | NEW | KafkaConfig, EmailEventProducer, EmailEvent |
| `docker-compose.yml` | MOD | siga-notification service + env vars |
| `scripts/db-init/init-db.sh` | MOD | Add notification schema |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Eventual consistency delays email | Low | UI: "if email doesn't arrive in 5min, resend" |
| Kafka down blocks new reg | Low | Publish fails fast → 503. Old sync path as feature flag fallback |
| Token replay on password reset | Low | One-time use, 15min expiry, invalidated on confirm |

## Rollback

Keep `EmailSenderPort` + `EmailSenderService` in auth. Toggle with `app.email.mode=async|sync`. Sync mode restores old behavior — no code revert or redeploy needed.

## Success Criteria

- [ ] Registration returns 201 without calling SMTP
- [ ] Notification service consumes event and sends email
- [ ] Password reset request sends email with reset link
- [ ] Password reset confirm updates password and invalidates token
- [ ] Integration test verifies Kafka → consumer → email flow
- [ ] Duplicate events are skipped (idempotency)
