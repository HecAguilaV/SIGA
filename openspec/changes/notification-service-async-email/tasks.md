# Tasks: Notification Service — Async Email via Kafka

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 700–1000 |
| 400-line budget risk | High |
| Chained PRs recommended | No |
| Suggested split | Single commit — user chose direct commits on migracion-microservicios |
| Delivery strategy | exception-ok |
| Chain strategy | size-exception |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Notes |
|------|------|-------|
| 1 | Notification service scaffold | build, application class, config, entities |
| 2 | Notification core logic | consumer, sender, templates |
| 3 | Auth Kafka producer | KafkaConfig, EmailEvent, EmailEventProducer |
| 4 | RegisterCustomerUseCase modification | Swap sync email → async event |
| 5 | Password reset | Use cases, entity, controller, migration |
| 6 | Infrastructure wiring | docker-compose, init-db, settings.gradle.kts |

## Phase 1: Foundation — Notification Service Scaffold

- [x] 1.1 Create `services/notification/build.gradle.kts` — mirror billing: spring-kafka, mail, data-jpa, web, postgresql, flyway, actuator, eureka-client
- [x] 1.2 Create `services/notification/src/main/kotlin/com/siga/notification/NotificationApplication.kt` — @SpringBootApplication @EnableDiscoveryClient
- [x] 1.3 Create `services/notification/src/main/kotlin/com/siga/notification/config/KafkaConfig.kt` — consumer-only, `siga-notification` group, `USE_TYPE_INFO_HEADERS=false` (mirror billing)
- [x] 1.4 Create `services/notification/src/main/kotlin/com/siga/notification/domain/EmailType.kt` — enum: WELCOME, PASSWORD_RESET
- [x] 1.5 Create `services/notification/src/main/kotlin/com/siga/notification/domain/EmailEvent.kt` — data class: eventId, email, type, name, token?, timestamp
- [x] 1.6 Create `services/notification/src/main/kotlin/com/siga/notification/infrastructure/entity/ProcessedEvent.kt` — JPA entity, `@Table(name = "processed_events", schema = "notification")`, mirror inventory
- [x] 1.7 Create `services/notification/src/main/kotlin/com/siga/notification/infrastructure/repository/ProcessedEventRepository.kt` — JpaRepository<ProcessedEvent, UUID>
- [x] 1.8 Create `services/notification/src/main/resources/db/migration/V1__notification_init.sql` — CREATE SCHEMA notification; CREATE TABLE notification.processed_events
- [x] 1.9 Create `services/notification/src/main/resources/application.yml` — port 8086, datasource siga_notification, Kafka consumer config
- [x] 1.10 Create `services/notification/src/main/resources/application-prod.yml` — SMTP via SPRING_MAIL_* env vars

## Phase 2: Notification Core — Consumer, Sender, Templates

- [x] 2.1 Create `services/notification/src/main/kotlin/com/siga/notification/infrastructure/service/TemplateRenderer.kt` — read classpath template, replace `{{name}}` and `{{actionUrl}}`
- [x] 2.2 Create `services/notification/src/main/kotlin/com/siga/notification/infrastructure/service/EmailSenderService.kt` — JavaMailSender with log fallback
- [x] 2.3 Create `services/notification/src/main/kotlin/com/siga/notification/infrastructure/consumer/EmailEventConsumer.kt` — @KafkaListener, idempotency check, dispatch by type
- [x] 2.4 Create `services/notification/src/main/resources/templates/welcome.html` — HTML template with `{{name}}`, `{{actionUrl}}`
- [x] 2.5 Create `services/notification/src/main/resources/templates/password-reset.html` — HTML template with `{{name}}`, `{{actionUrl}}`
- [x] 2.6 Create `services/notification/Dockerfile` — mirror billing/inventory

## Phase 3: Auth — Kafka Producer

- [x] 3.1 Modify `services/auth/build.gradle.kts` — add `spring-kafka` dependency
- [x] 3.2 Create `services/auth/src/main/kotlin/com/siga/auth/config/KafkaConfig.kt` — producer-only, `ADD_TYPE_INFO_HEADERS=false` (mirror sales)
- [x] 3.3 Create `services/auth/src/main/kotlin/com/siga/auth/event/EmailEvent.kt` — data class mirror of notification's EmailEvent
- [x] 3.4 Create `services/auth/src/main/kotlin/com/siga/auth/event/EmailEventProducer.kt` — KafkaTemplate<String, Any>, topic `email-events`, key = recipient email

## Phase 4: Auth — Modified Registration + Password Reset

- [x] 4.1 Modify `services/auth/.../application/usecase/RegisterCustomerUseCase.kt` — inject EmailEventProducer, publish WELCOME event instead of calling EmailSenderPort; keep sync path via feature flag
- [x] 4.2 Create `services/auth/.../entity/PasswordResetToken.kt` — JPA entity with id, email, token, expiresAt, used, createdAt
- [x] 4.3 Create `services/auth/.../repository/PasswordResetTokenRepository.kt` — JpaRepository with finders by token and email
- [x] 4.4 Create `services/auth/.../application/usecase/ResetPasswordRequestUseCase.kt` — generate token (15min expiry), persist, publish PASSWORD_RESET event, return 200 always
- [x] 4.5 Create `services/auth/.../application/usecase/ResetPasswordConfirmUseCase.kt` — validate token, check expiry, hash new password, invalidate token, update customer
- [x] 4.6 Modify `services/auth/.../controller/AuthController.kt` — add POST /api/v1/auth/reset-password/request and POST /api/v1/auth/reset-password/confirm
- [x] 4.7 Create `services/auth/src/main/resources/db/migration/V5__password_reset_tokens.sql` — CREATE TABLE auth.password_reset_tokens
- [x] 4.8 Add `app.email.mode=async` to auth application.yml (feature flag with async|sync)

## Phase 5: Infrastructure Wiring

- [x] 5.1 Modify `docker-compose.yml` — add `siga-notification` service with Kafka, DB, and Mail env vars
- [x] 5.2 Modify `scripts/db-init/init-db.sh` — add `init_service_db "siga_notification" "notification_user" "notification_pass_2026" "notification"`
- [x] 5.3 Modify `settings.gradle.kts` — add `include("services:notification")`

## Phase 6: Testing

- [x] 6.1 Write unit test for TemplateRenderer — verify `{{name}}` and `{{actionUrl}}` substitution
- [x] 6.2 Write unit test for EmailEventConsumer dispatch — mock repository + sender, verify calls by type
- [x] 6.3 Write integration test for Kafka → consumer → email flow — EmbeddedKafka
- [x] 6.4 Write integration test for idempotency — same eventId twice, verify 1 event processed
- [x] 6.5 Write integration test for password reset flow — controller → Kafka → consumer chain (partial: uses PasswordResetTokenRepository directly)
