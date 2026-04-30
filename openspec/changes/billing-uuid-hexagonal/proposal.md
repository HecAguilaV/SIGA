# Proposal: billing-uuid-hexagonal

## Intent
Blindar la seguridad y privacidad de los clientes de SIGA (Ley 21.719) mediante la migración a UUID y desacoplar la lógica de pagos mediante Arquitectura Hexagonal para permitir integraciones futuras (Transbank/SII) sin afectar el núcleo del negocio.

## Scope

### In Scope
- Migración de IDs de `Int` a `UUID` en entidades: `Customer`, `Subscription`, `Payment`, `Plan`.
- Implementación de Arquitectura Hexagonal en el módulo de pagos.
- Creación del puerto `PaymentGateway`.
- Creación del adaptador ficticio `TransbankAdapter`.
- Saneamiento de controladores para usar `/api/v1/billing/...`.

### Out of Scope
- Integración real con APIs de producción de Transbank.
- Lógica de facturación electrónica real del SII (solo emulación de estructura).

## Capabilities

### New Capabilities
- `subscription-management`: Gestión de ciclos de vida de suscripciones con UUID.
- `payment-gateway`: Abstracción de pagos mediante puertos y adaptadores.

### Modified Capabilities
- None

## Approach
Seguiremos un enfoque de **Evolución Hexagonal**:
1. **Entidades**: Refactorizar a UUID con hooks de auditoría.
2. **Puertos**: Definir interfaces para el procesamiento de pagos.
3. **Adaptadores**: Implementar una versión ficticia que emule el comportamiento de Transbank/SII.
4. **Controllers**: Refactorizar para inyectar los nuevos servicios/puertos.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `services/billing/src/main/kotlin/com/siga/billing/entity/` | Modified | Migración a UUID y Auditoría. |
| `services/billing/src/main/kotlin/com/siga/billing/repository/` | Modified | Actualización de tipos JpaRepository. |
| `services/billing/src/main/kotlin/com/siga/billing/controller/` | Modified | Actualización de paths y lógica de inyección. |
| `services/billing/src/main/kotlin/com/siga/billing/service/` | New | Implementación de Puertos y Adaptadores. |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Ruptura de API (Int -> UUID) | High | Actualizar documentación de API y avisar al frontend (commercial). |
| Complejidad Hexagonal | Medium | Usar patrones simples de inyección de Spring. |

## Rollback Plan
Revertir los cambios en las entidades a `Int` y restaurar los controladores desde el historial de Git. Dado que estamos en desarrollo, el impacto es controlado.

## Success Criteria
- [ ] Compilación exitosa del microservicio billing.
- [ ] Suite de tests de integración en verde con UUID.
- [ ] Pasarela ficticia procesando pagos exitosamente en logs.
