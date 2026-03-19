# Microservices Design Patterns (Detailed)

Este protocolo guía la transición de SIGA hacia una arquitectura distribuida resiliente y escalable.

## ️ Descomposición de Servicios
- **Por Capacidad de Negocio**: `OrderService`, `InventoryService`, `PaymentService`.
- **Patrón Strangler Fig**: Extraer funcionalidades del monolito gradualmente.

##  Patrones de Comunicación
- **Sincrónico (REST/gRPC)**: Para respuestas inmediatas. Usar con Circuit Breaker.
- **Asincrónico (Event-Driven)**: Usar Kafka o RabbitMQ para desacoplamiento total. `OrderCreatedEvent` -> `InventoryReserve`.

## ️ Resiliencia y Datos
- **Circuit Breaker**: `failure_threshold=5`. Evita saturar servicios caídos.
- **Database per Service**: Cada microservicio es dueño de su esquema.
- **Saga Pattern**: Gestión de transacciones distribuidas con acciones compensatorias.

##  API Gateway
Punto de entrada único que agrega datos de múltiples servicios (ej. `GET /api/orders` consulta a Orders, Payments e Inventory en paralelo).
