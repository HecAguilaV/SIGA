# Tasks: sync-kotlin-entities

## Batch 1: Script SQL (prerequisito)

- [ ] T1.1: Crear `services/backend/scripts/migrations/V001__add_monto_contado.sql`
  - ALTER TABLE `siga_saas.turnos_caja` ADD COLUMN `monto_contado NUMERIC(10,2)`
  - Con COMMENT ON COLUMN

## Batch 2: Servicio Auth — Refactor + Nueva Entidad (6 archivos)

- [ ] T2.1: Refactorizar `UsuarioSaas.kt` — `data class` → `class` + equals/hashCode
- [ ] T2.2: Refactorizar `UsuarioComercial.kt` — `data class` → `class` + equals/hashCode
- [ ] T2.3: Refactorizar `Permiso.kt` — `data class` → `class` + equals/hashCode
- [ ] T2.4: Refactorizar `RolPermiso.kt` — `data class` → `class` (mantener @EmbeddedId)
- [ ] T2.5: Refactorizar `UsuarioPermiso.kt` — `data class` → `class` (mantener @EmbeddedId)
- [ ] T2.6: Crear `UsuarioLocal.kt` — nueva entidad con @EmbeddedId (usuario_id, local_id)

## Batch 3: Servicio Inventario — Refactor + Nuevas Entidades (6 archivos)

- [ ] T3.1: Refactorizar `Producto.kt` — `data class` → `class` + equals/hashCode
- [ ] T3.2: Refactorizar `Stock.kt` — `data class` → `class` + equals/hashCode
- [ ] T3.3: Refactorizar `Local.kt` — `data class` → `class` + equals/hashCode
- [ ] T3.4: Refactorizar `Categoria.kt` — `data class` → `class` + equals/hashCode
- [ ] T3.5: Crear `Movimiento.kt` — nueva entidad con enum TipoMovimiento
- [ ] T3.6: Crear `Alerta.kt` — nueva entidad con enum TipoAlerta

## Batch 4: Servicio Ventas — Refactor + Nuevas Entidades (6 archivos)

- [ ] T4.1: Refactorizar `Venta.kt` + `DetalleVenta.kt` — `data class` → `class` (mismo archivo)
- [ ] T4.2: Refactorizar `Factura.kt` — `data class` → `class` + equals/hashCode
- [ ] T4.3: Crear `TurnoCaja.kt` — nueva entidad con enum EstadoTurno, incluye monto_contado
- [ ] T4.4: Crear `TransaccionPos.kt` — nueva entidad con enum EstadoTransaccion
- [ ] T4.5: Crear `MetodoPago.kt` — nueva entidad
- [ ] T4.6: Crear `CarritoPos.kt` — nueva entidad

## Batch 5: Servicio Backend (monolito) — Refactor + Nuevas Entidades (4 archivos)

- [ ] T5.1: Refactorizar `Plan.kt` — `data class` → `class`, `caracteristicas` TEXT→JSONB
- [ ] T5.2: Refactorizar `Suscripcion.kt` — `data class` → `class` + equals/hashCode
- [ ] T5.3: Crear `Pago.kt` — nueva entidad con enum EstadoPago
- [ ] T5.4: Crear `CarritoComercial.kt` — nueva entidad

## Verificacion

- [ ] T6.1: Compilar servicio `auth` con `./gradlew :auth:compileKotlin`
- [ ] T6.2: Compilar servicio `inventario` con `./gradlew :inventario:compileKotlin`
- [ ] T6.3: Compilar servicio `ventas` con `./gradlew :ventas:compileKotlin`
- [ ] T6.4: Compilar servicio `backend` con `./gradlew :backend:compileKotlin`
