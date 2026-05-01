# Proposal: SAGA Sales-Inventory

## Intent
Implementar el patrón SAGA (Coreografía) para coordinar transacciones distribuidas entre los microservicios Sales e Inventory vía Apache Kafka, reemplazando la comunicación síncrona actual (Feign) por eventos asíncronos con compensación automática.

## Problem
Actualmente, Sales llama a Inventory de forma síncrona vía Feign (`InventoryClient.kt`). Si Inventory se cae, la venta falla inmediatamente. No hay mecanismo de compensación ni trazabilidad de la transacción distribuida.

## Scope
- **In scope**: Flujo Sale→Stock (reserva/confirmación/compensación), infraestructura Kafka en ambos servicios, eventos de dominio, tests unitarios e integración.
- **Out of scope**: Billing integration, Agent integration, Big Data pipeline (fase posterior).

## Approach
**Coreografía** (cada servicio escucha y reacciona a eventos) sobre **Orquestación** (un servicio central dirige). Razón: con solo 2 servicios involucrados, la coreografía es más simple, tiene menos puntos de fallo y no requiere un servicio adicional de orquestación.

## Rollback Plan
El Feign client (`InventoryClient.kt`) se mantiene como fallback. Si Kafka falla, se puede reactivar la comunicación síncrona sin cambios en la lógica de negocio.

## Security Impact
- Los eventos Kafka llevarán `tenantId` y `userId` para trazabilidad.
- No se transmiten datos sensibles (passwords, tokens) por Kafka.
- Los UUIDs de las transacciones garantizan idempotencia.
