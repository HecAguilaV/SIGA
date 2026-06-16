# Inventory Service (siga-inventory)

*Read this in other languages: [![English](README.en.md)](README.en.md)*

Gestión centralizada de stock, productos y movimientos de almacén (Kardex).

## Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework**: Spring Boot 4.0.6
- **Mensajería**: Apache Kafka (SAGA Coreografía)
- **BD**: PostgreSQL (Esquema: `inventory`)

## APIs & Contratos

### Productos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/inventory/products` | Listar productos |
| `POST` | `/api/v1/inventory/products` | Crear producto (auto-SKU si vacío, detección duplicados) |
| `GET` | `/api/v1/inventory/products/search?q=X` | Búsqueda ILIKE + unaccent (mín. 2 caracteres) |
| `GET` | `/api/v1/inventory/products/duplicate-check?name=X` | Verificar duplicados por nombre |

### Stock
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/inventory/stock/product/{id}` | Stock de un producto por punto |
| `GET` | `/api/v1/inventory/stock/store/{id}` | Stock de un punto específico |
| `GET` | `/api/v1/inventory/stock/consolidated?productId=X` | **Stock consolidado multi-punto** (total + desglose) |
| `POST` | `/api/v1/inventory/stock/reconciliations` | **Conteo físico** con detección de discrepancias y alertas |
| `POST` | `/api/v1/inventory/stock/transfers` | **Transferencia** bodega↔punto (atómica vía @Transactional) |

### Movimientos (Kardex)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/inventory/stock/movements` | Historial filtrado por store, tipo, fechas |

## Interrelaciones
- **SAGA**: Escucha `sale-events` (Topic: `sale-events`) para reservar stock.
- **SAGA**: Publica `stock-events` (Topic: `stock-events`) con el resultado de la reserva.
- **Service Registry**: Se registra en `siga-registry` (Eureka).

## Arquitectura
- [x] Hexagonal (Ports & Adapters)
- [x] Strict TDD (50+ tests — unitarios + integración)
- [x] SDD: `openspec/changes/archive/2026-05-19-inventory-core-features/`
- [x] UUID v4 (Ley 21.719)
- [x] Idempotencia (Tabla: `processed_events`)


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
