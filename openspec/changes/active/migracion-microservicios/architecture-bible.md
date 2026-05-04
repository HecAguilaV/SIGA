# Arquitectura de Referencia — SIGA Microservicios (Gold Standard)

Documento técnico que fundamenta las decisiones arquitectónicas del proyecto SIGA
en su migración hacia una Arquitectura de Microservicios Hexagonal.

---

## 1. Principios de Oro (Hexagonal)

### 1.1 Agnosticismo de Dominio
El Núcleo de Negocio (`domain/`) **NO** debe tener importaciones de:
- `org.springframework.*`
- `jakarta.persistence.*`
- `org.springframework.data.*`

**Justificación**: Si mañana decidimos cambiar PostgreSQL por MongoDB o Kafka por RabbitMQ, 
el Dominio y la Aplicación NO se tocan.

### 1.2 Estructura de Capas (The Gold Standard)
Cada servicio (ej: `billing`, `inventory`) debe seguir este esquema:

```text
com.siga.[service]/
├── domain/
│   ├── model/      # Puro Kotlin (data class). Lógica de negocio AQUÍ.
│   └── port/        # Interfaces (Puertos). Definen contratos hacia afuera.
├── application/
│   └── usecase/   # Orquestación (SAGA, validaciones, flujos).
├── infrastructure/
│   ├── adapter/    # Implementación de Puertos (JPA, Kafka, REST).
│   ├── mapper/     # Conversión Entity (JPA) <-> Model (Dominio).
│   └── persistence/ # Entidades JPA (Entity.kt) y Repositorios.
└── controller/       # Entrada REST (Habla con Use Cases o Puertos).
```

---

## 2. Estado de los Servicios (3 de mayo 2026)

| Servicio | Estado Hexagonal | Notas |
|----------|-----------------|-------|
| **billing** | ✅ **COMPLETO** | Gold Standard. Billing UUID, PaymentGateway Port, Mappers implementados. |
| **inventory** | ✅ **COMPLETO** | Gold Standard. SAGA logic moved to `ReserveStockUseCase`. Mappers done. |
| **sales** | 🔄 **PENDIENTE** | Aún acoplado. Requiere refactorización similar. |
| **auth** | 🔄 **PENDIENTE** | Aún acoplado. |

---

## 3. Patrón SAGA (Coreografía)

La transacción distribuida en SIGA usa **SAGA Coreografía** (Event-Driven).

### Flujo Actual (Inventory & Sales):
1. **Sales** emite `SALE_INITIATED` con items y `tenantId`.
2. **Inventory** (`ReserveStockUseCase`) valida stock (All-or-nothing).
   - Si OK -> Emite `STOCK_RESERVED`.
   - Si Falla -> Emite `STOCK_FAILED`.
3. **Sales** (`StockEventConsumer`) escucha respuesta y cambia estado a `COMPLETED` o `CANCELLED`.

**Key Takeaway**: La lógica de negocio de la reserva está en `application/usecase/`, no en el Consumer de Kafka.

---

## 4. Migración de "Entities" a "Models"

| Concepto | Antes (Incorrecto) | Ahora (Correcto) |
|----------|-------------------|------------------|
| **Modelo de Dominio** | `Product.kt` (con `@Entity`) | `domain/model/Product.kt` (Puro) |
| **Entidad JPA** | — | `entity/ProductEntity.kt` (Infraestructura) |
| **Conversión** | — | `infrastructure/mapper/ProductMapper.kt` |

---

## 5. Filosofía de Código (Para el Docente)

> "En SIGA, el código no es solo 'hacer que funcione'. Es establecer una base donde el Framework (Spring) es un detalle de implementación, no el dueño de la arquitectura."

*   **Clean Code**: Nombres significativos, funciones cortas, una sola razón de cambio (SRP).
*   **SOLID**: Dependency Inversion (Los módulos de alto nivel no dependen de los bajos, ambos dependen de abstracciones/Puertos).
*   **Defensible**: Si un docente pregunta "¿Por qué Spring?", la respuesta es "Porque el Adaptador lo permite, no porque el Dominio lo exija".
