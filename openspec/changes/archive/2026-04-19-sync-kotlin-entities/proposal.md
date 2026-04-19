# Proposal: Sincronización de Entidades Kotlin con DB Real

## Intent

Las entidades JPA en los microservicios (auth, inventario, ventas) cubren solo 12 de 23 tablas reales de la base de datos PostgreSQL. Las 11 tablas faltantes incluyen componentes críticos para el POS (turnos_caja, transacciones_pos), trazabilidad (movimientos), alertas y el esquema comercial (planes, suscripciones, pagos). Sin estas entidades, el Agente IA no puede consultar datos reales y la webapp no tiene backend funcional. Además, todas las entidades existentes usan `data class`, violando la convención JPA del proyecto.

## Scope

### In Scope
- Crear 7 entidades faltantes en `siga_saas`: Movimiento, Alerta, TurnoCaja, TransaccionPos, MetodoPago, CarritoPos, UsuarioLocal
- Crear 3 entidades faltantes en `siga_comercial`: Plan, Suscripcion, Pago (+ CarritoComercial)
- Refactorizar TODAS las entidades existentes de `data class` a `class` JPA
- ALTER TABLE: agregar `monto_contado NUMERIC(10,2)` a `turnos_caja`
- Script SQL de migración para `monto_contado`
- Documentación del modelo de datos completo

### Out of Scope
- Repositories, Services y Controllers (fase siguiente)
- Lógica de negocio del POS
- Integración con el Agente IA
- Tests (se crearán junto con la capa de servicio)

## Capabilities

### New Capabilities
- `pos-cash-register`: Gestión de turnos de caja con apertura/cierre y reconciliación de montos
- `inventory-audit-trail`: Registro completo de movimientos de stock (Kardex)
- `billing-subscriptions`: Entidades del esquema comercial para planes y suscripciones

### Modified Capabilities
- `jpa-entity-convention`: Migrar de `data class` a `class` con equals/hashCode por ID

## Approach

1. **Crear entidades nuevas** siguiendo la convención correcta (`class`, no `data class`)
2. **Refactorizar entidades existentes** en los 3 servicios (auth, inventario, ventas)
3. **Script SQL** para `ALTER TABLE siga_saas.turnos_caja ADD COLUMN monto_contado`
4. **Documento de modelo de datos** para el manual de usuario futuro

## Affected Areas

| Area | Impact | Servicio |
|------|--------|----------|
| `services/inventario/entity/` | New: Movimiento, Alerta, UsuarioLocal | inventario |
| `services/ventas/entity/` | New: TurnoCaja, TransaccionPos, MetodoPago, CarritoPos | ventas |
| `services/auth/entity/` | New: Plan, Suscripcion, Pago, CarritoComercial | auth |
| `services/*/entity/*.kt` | Modified: todas las existentes (data class → class) | todos |
| `docs/database/` | New: migration script + modelo documentado | docs |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Romper tests existentes al cambiar data class | Media | Verificar tests antes y después |
| Composite keys (@EmbeddedId) mal mapeadas | Baja | Ya existen patrones en RolPermiso/UsuarioPermiso |
| Entidades comerciales en servicio incorrecto | Media | Decisión: auth ya mapea UsuarioComercial, las demás van ahí |

## Rollback Plan

Git revert del commit. No hay migraciones destructivas — `ADD COLUMN` es aditiva.

## Dependencies

- PostgreSQL 16 con schemas `siga_saas` y `siga_comercial` existentes
- Spring Boot 3.2 + JPA/Hibernate

## Success Criteria

- [ ] Todas las 23 tablas tienen su entidad Kotlin correspondiente
- [ ] Ninguna entidad usa `data class`
- [ ] El proyecto compila sin errores en los 3 servicios
- [ ] Script SQL de migración listo para ejecutar
