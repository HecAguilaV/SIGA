# Billing Service (siga-billing)

Servicio de facturación corporativa para la plataforma SaaS **SIGA** (B2B).

## Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework**: Spring Boot 3.2.x
- **BD**: PostgreSQL (Esquema: `commercial`)

## APIs & Contratos
- **Planes**: `GET /api/v1/billing/plans`
- **Suscripciones**: `POST /api/v1/billing/subscriptions`
- **Facturas SIGA**: `GET /api/v1/billing/invoices`

## Interrelaciones
- **Admin Portal**: Provee datos financieros para el Backoffice.
- **Service Registry**: Se registra en `siga-registry` (Eureka).

## Arquitectura
- [x] Hexagonal
- [x] UUID v4
- [ ] SAGA (Pendiente integración con pagos externos)

---
> "La salud financiera de la plataforma."
