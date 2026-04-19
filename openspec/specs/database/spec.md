# Delta for jpa-entity-convention

## ADDED Requirements

### Requirement: JPA Entity Convention

Todas las entidades JPA del proyecto MUST usar `class` (NO `data class`) con `equals`/`hashCode` basados en el campo `id`.

#### Scenario: Entidad nueva creada correctamente

- GIVEN una tabla existente en la DB
- WHEN se crea la entidad Kotlin correspondiente
- THEN MUST ser una `class` abierta con `var` en campos mutables
- AND MUST tener `equals`/`hashCode` basados unicamente en `id`
- AND MUST tener `toString` que NO incluya relaciones lazy

#### Scenario: Entidad con clave compuesta

- GIVEN una tabla con PK compuesta (ej: `roles_permisos`)
- WHEN se crea la entidad Kotlin
- THEN MUST usar `@EmbeddedId` con una clase `@Embeddable` separada
- AND la clase embeddable MUST implementar `Serializable`

---

# Delta for pos-cash-register

## ADDED Requirements

### Requirement: Turno de Caja

El sistema MUST registrar apertura y cierre de caja con timestamps y montos para cada cajero en cada local.

#### Scenario: Apertura de caja

- GIVEN un cajero autenticado asignado a un local
- WHEN abre caja
- THEN se crea un registro en `turnos_caja` con `fecha_apertura`, `monto_inicial` y estado `ABIERTO`

#### Scenario: Cierre de caja con reconciliacion

- GIVEN un turno de caja ABIERTO
- WHEN el cajero cierra caja e ingresa el monto contado fisicamente
- THEN se registra `fecha_cierre`, `monto_final` (calculado), `monto_contado` (ingresado) y estado `CERRADO`
- AND la diferencia (`monto_final - monto_contado`) permite detectar descuadres

#### Scenario: Restriccion de turno unico

- GIVEN un cajero con un turno ABIERTO en un local
- WHEN intenta abrir otro turno en el mismo local
- THEN el sistema MUST rechazar la operacion

---

# Delta for inventory-audit-trail

## ADDED Requirements

### Requirement: Kardex de Movimientos

El sistema MUST registrar cada cambio de stock como un movimiento con trazabilidad completa.

#### Scenario: Movimiento por entrada de mercaderia

- GIVEN un producto y un local existentes
- WHEN un operador registra una entrada de stock
- THEN se crea un movimiento tipo `ENTRADA` con `cantidad_anterior`, `cantidad_nueva`, `usuario_id` y timestamp

#### Scenario: Movimiento automatico por venta

- GIVEN una venta completada
- WHEN el sistema descuenta stock
- THEN se crea un movimiento tipo `VENTA` vinculado a `venta_id` con trazabilidad completa

#### Scenario: Consulta del agente IA

- GIVEN el agente analista recibe la pregunta "quien ajusto el stock de harina ayer"
- WHEN consulta la API de movimientos
- THEN obtiene registros filtrados por producto, fecha y tipo, incluyendo el `usuario_id` responsable

---

# Delta for billing-subscriptions

## ADDED Requirements

### Requirement: Entidades del Portal Comercial

El sistema MUST mapear las tablas de `siga_comercial` (planes, suscripciones, pagos, carritos) como entidades Kotlin en el servicio correspondiente.

#### Scenario: Plan con limites operativos

- GIVEN un plan definido en la DB
- WHEN se consulta via API
- THEN retorna `limite_bodegas`, `limite_usuarios`, `limite_productos` y `caracteristicas` (JSONB)

#### Scenario: Suscripcion con estados

- GIVEN un usuario comercial con suscripcion
- WHEN cambia de estado (ACTIVA, SUSPENDIDA, CANCELADA, VENCIDA)
- THEN el sistema MUST actualizar el estado y la `fecha_actualizacion`
