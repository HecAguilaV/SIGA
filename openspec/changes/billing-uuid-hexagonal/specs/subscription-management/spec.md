# Subscription Management Specification

## Purpose
Gestionar el ciclo de vida de las suscripciones de los clientes comerciales de SIGA, asegurando la integridad de los datos y el cumplimiento de la normativa de privacidad.

## Requirements

### Requirement: UUID Identification
El sistema DEBE utilizar identificadores UUID para Clientes y Suscripciones. No se permiten IDs secuenciales.

#### Scenario: Create new subscription with UUID
- GIVEN un cliente comercial registrado con UUID
- WHEN solicita una nueva suscripción a un plan
- THEN el sistema genera una suscripción con un UUID único
- AND asocia la suscripción al UUID del cliente.

### Requirement: Subscription Audit
El sistema DEBE registrar automáticamente las fechas de creación y actualización de cada suscripción.

#### Scenario: Audit timestamps on update
- GIVEN una suscripción activa
- WHEN se actualiza su estado (ej. de ACTIVE a SUSPENDED)
- THEN el sistema actualiza el campo `updated_at` automáticamente.
