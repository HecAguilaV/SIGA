# Design: billing-uuid-hexagonal

## Technical Approach
Evolucionar el microservicio `billing` desde un CRUD básico hacia una Arquitectura Hexagonal que soporte la migración a UUID y el desacoplamiento de pasarelas de pago.

## Architecture Decisions

### Decision: UUID Migration
**Choice**: Generación de UUID en base de datos mediante Hibernate.
**Rationale**: Cumplimiento de Ley 21.719 y eliminación de vectores de ataque por enumeración de IDs.

### Decision: Hexagonal Ports & Adapters
**Choice**: Definir `PaymentGateway` como puerto y `TransbankAdapter` como adaptador.
**Rationale**: Desacoplar la lógica de suscripciones de los detalles técnicos de las pasarelas de pago (Transbank/SII).

## Data Flow
```
Controller ──→ SubscriptionService ──→ PaymentGateway (Port)
                                            │
                                            └──→ TransbankAdapter (Adapter)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `com.siga.billing.entity.*` | Modify | Cambiar `Int` por `UUID`. Añadir Auditoría. |
| `com.siga.billing.repository.*` | Modify | Actualizar tipos de repositorio. |
| `com.siga.billing.domain.port.PaymentGateway` | Create | Interfaz del puerto de pagos. |
| `com.siga.billing.infrastructure.adapter.TransbankAdapter` | Create | Implementación ficticia de pagos. |
| `com.siga.billing.service.SubscriptionService` | Create | Orquestador de lógica de negocio. |
| `com.siga.billing.controller.*` | Modify | Inyectar `SubscriptionService` y actualizar paths. |

## Interfaces / Contracts

```kotlin
interface PaymentGateway {
    fun processPayment(request: PaymentRequest): PaymentResponse
}

data class PaymentRequest(
    val amount: BigDecimal,
    val customerId: UUID,
    val description: String
)

data class PaymentResponse(
    val success: Boolean,
    val transactionId: String,
    val responseCode: String,
    val siiPayload: Map<String, Any>?
)
```

## Testing Strategy
| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Subscription logic | Mocking PaymentGateway. |
| Integration | End-to-end flow | MockMvc + H2 + Real TransbankAdapter. |
