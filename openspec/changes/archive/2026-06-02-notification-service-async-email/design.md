# Design: Notification Service — Async Email via Kafka

## Technical Approach

Mirar el patrón Kafka existente (Sales → Inventory → Billing). Extraer el envío de email de auth hacia un `services/notification` consumer-only. Auth publica `EmailEvent` al topic `email-events`; notification consume, chequea idempotencia con `processed_events`, renderiza template HTML y envía via `JavaMailSender`. Feature flag `app.email.mode=async|sync` permite rollback sin redeploy.

## Architecture Decisions

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Consumer-only service | Sin inbound ports ni REST. Sigue patrón billing. | ✅ Elegido — la notificación es puramente reactiva |
| Sin application layer | La lógica (deserialize → check → render → send) vive en consumer + services. | ✅ Elegido — es thin orchestration, no requiere use case |
| ProcessedEvent con UUID eventId | Mismo patrón que inventory. Check `existsById` antes de procesar. | ✅ Elegido — idempotencia probada contra Kafka redelivery |
| Templates en classpath | Simple string substitution (`{{name}}`, `{{actionUrl}}`). Sin Thymeleaf. | ✅ Elegido — cero dependencias extra, fácil de testear |
| Feature flag `app.email.mode` | `async` (Kafka) por defecto, `sync` (EmailSenderPort directo) como fallback. | ✅ Elegido — rollback sin code revert |

## Data Flow

```
Registration / Password Reset
         │
         ▼
┌─────────────────┐     ┌──────────────┐     ┌──────────────────────┐
│  Auth Service   │────→│  Kafka       │────→│  Notification Service│
│  EmailEvent     │     │  email-events│     │  EmailEventConsumer  │
│  Producer       │     │  key: email  │     │  group: siga-notif   │
└─────────────────┘     └──────────────┘     └──────────────────────┘
                                                     │
                                                     ▼
                                            ┌─────────────────┐
                                            │  ProcessedEvent  │
                                            │  (idempotency)   │
                                            └─────────────────┘
                                                     │
                                                     ▼
                                            ┌─────────────────┐
                                            │ TemplateRenderer │
                                            │  → HTML body     │
                                            └─────────────────┘
                                                     │
                                                     ▼
                                            ┌─────────────────┐
                                            │ EmailSender     │
                                            │ (JavaMailSender │
                                            │  / log fallback)│
                                            └─────────────────┘
```

## File Changes

### New Service: `services/notification`

| File | Action | Description |
|------|--------|-------------|
| `build.gradle.kts` | Create | spring-kafka, mail, data-jpa, web (actuator), postgresql, flyway |
| `NotificationApplication.kt` | Create | `@SpringBootApplication @EnableDiscoveryClient` |
| `config/KafkaConfig.kt` | Create | Consumer-only, `siga-notification` group, `USE_TYPE_INFO_HEADERS=false` (espeja billing) |
| `domain/EmailType.kt` | Create | `enum: WELCOME, PASSWORD_RESET` |
| `domain/EmailEvent.kt` | Create | `data class: eventId, email, type, name, token?, timestamp` |
| `infrastructure/consumer/EmailEventConsumer.kt` | Create | `@KafkaListener(topics=["email-events"])`, check idempotency, dispatch to sender |
| `infrastructure/service/EmailSenderService.kt` | Create | JavaMailSender con log fallback (espeja auth) |
| `infrastructure/service/TemplateRenderer.kt` | Create | Lee template de classpath, reemplaza `{{name}}`, `{{actionUrl}}` |
| `infrastructure/entity/ProcessedEvent.kt` | Create | JPA entity, `@Table(name="processed_events", schema="notification")` |
| `infrastructure/repository/ProcessedEventRepository.kt` | Create | `JpaRepository<ProcessedEvent, UUID>` |
| `resources/application.yml` | Create | Puerto 8086, datasource siga_notification, Kafka consumer config |
| `resources/application-prod.yml` | Create | SMTP config (SPRING_MAIL_* env vars) |
| `resources/templates/welcome.html` | Create | HTML con `{{name}}`, `{{actionUrl}}` |
| `resources/templates/password-reset.html` | Create | HTML con `{{name}}`, `{{actionUrl}}` |
| `resources/db/migration/V1__notification_init.sql` | Create | `CREATE SCHEMA notification; CREATE TABLE processed_events(...)` |
| `Dockerfile` | Create | Espeja Dockerfile de billing/inventory |

### Auth Service Changes

| File | Action | Description |
|------|--------|-------------|
| `build.gradle.kts` | Modify | Agregar `spring-kafka` dependency |
| `config/KafkaConfig.kt` | Create | Producer-only (espeja sales), `JsonSerializer.ADD_TYPE_INFO_HEADERS=false` |
| `event/EmailEvent.kt` | Create | `data class` mirror del domain en notification (ambos lados del contrato) |
| `event/EmailEventProducer.kt` | Create | `KafkaTemplate<String, Any>`, TOPIC = `"email-events"`, key = recipient email |
| `application/usecase/RegisterCustomerUseCase.kt` | Modify | Inyectar `EmailEventProducer`, publicar WELCOME event. `EmailSenderPort` queda como respaldo por feature flag |
| `application/usecase/ResetPasswordRequestUseCase.kt` | Create | Genera token (15min expiry), persiste, publica PASSWORD_RESET event, retorna 200 siempre |
| `application/usecase/ResetPasswordConfirmUseCase.kt` | Create | Valida token, chequea expiry, hashea nueva password, invalida token, actualiza customer |
| `entity/PasswordResetToken.kt` | Create | JPA entity: id, customerId, token, expiresAt, usedAt, createdAt |
| `repository/PasswordResetTokenRepository.kt` | Create | `JpaRepository`, finders por token y customer |
| `controller/AuthController.kt` | Modify | Agregar `POST /api/v1/auth/reset-password/request` y `POST /api/v1/auth/reset-password/confirm` |
| `resources/db/migration/V5__password_reset_tokens.sql` | Create | `CREATE TABLE auth.password_reset_tokens(...)` |

### Infrastructure

| File | Action | Description |
|------|--------|-------------|
| `docker-compose.yml` | Modify | Agregar `siga-notification` service con env vars (KAFKA, DB, MAIL) |
| `scripts/db-init/init-db.sh` | Modify | Agregar schema `siga_notification` + `notification_user` |

## Interfaces / Contracts

### EmailEvent JSON Schema (Kafka)

```json
{
  "eventId": "uuid",
  "email": "string",
  "type": "WELCOME | PASSWORD_RESET",
  "name": "string",
  "token": "string | null",
  "timestamp": "ISO-8601 instant"
}
```

### Kafka Topic

| Property | Value |
|----------|-------|
| Topic name | `email-events` |
| Partitions | 1 (default) |
| Replication | 1 (dev) |
| Key | Recipient email (garantiza orden por destinatario) |
| Producer serializer | `JsonSerializer`, `ADD_TYPE_INFO_HEADERS=false` |
| Consumer deserializer | `JsonDeserializer`, `USE_TYPE_INFO_HEADERS=false` |
| Consumer group | `siga-notification` |
| Auto offset reset | `earliest` |

### Feature Flag

```yaml
app:
  email:
    mode: async  # async | sync — default async
```

En auth `RegisterCustomerUseCase`: si `mode == async`, llama `EmailEventProducer.publish()`; si `mode == sync`, llama `EmailSenderPort.sendVerificationEmail()` (comportamiento legacy).

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | TemplateRenderer | Reemplazo de variables `{{name}}`, `{{actionUrl}}` en templates conocidos |
| Unit | EmailEventConsumer (dispatch logic) | Mock ProcessedEventRepository + EmailSenderService, verificar llamadas por tipo |
| Integration | Kafka → Consumer → Email | `@SpringBootTest` con EmbeddedKafka o Testcontainers. Publicar EmailEvent, verificar email enviado |
| Integration | Password reset flow | Auth controller → Kafka → Notification consumer (Testcontainers) |
| Integration | Idempotency | Publicar mismo eventId dos veces, verificar email enviado 1 vez |
| Integration | Duplicate event | `processed_events` ya tiene el eventId → consumer saltea |

## Migration / Rollout

1. **Deploy notification service** primero — arranca, crea schema y tabla `processed_events`, pero no consume porque no hay eventos todavía.
2. **Deploy auth** con `app.email.mode=sync` (default legacy). Sin cambios visibles.
3. **Toggle** `app.email.mode=async` via config. A partir de ese momento, registration y password-reset publican eventos Kafka. Si algo falla, toggle back a `sync`.
4. **Monitorear** consumer lag en Kafka UI. Verificar que `processed_events` se populate.
5. **Luego de 1 semana** sin issues, remover el `EmailSenderPort` del auth y limpiar el feature flag.

## Open Questions

- [ ] ¿Cuántas particiones para `email-events` en producción? 1 alcanza para el volumen esperado, pero documentar que es configurable.
- [ ] ¿Usar `@RetryableTopic` de Spring Kafka para retry automático (3 intentos) o manejarlo manual en el consumer? Spec dice 3 retries con exponential backoff.
