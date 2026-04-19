# Design: sync-kotlin-entities

## Decision: Patron de Entidad JPA

Todas las entidades seguiran este patron estandar:

```kotlin
@Entity
@Table(name = "TABLA", schema = "siga_saas")
class NombreEntidad(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, length = 100)
    var nombre: String,

    // ... campos
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NombreEntidad) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "NombreEntidad(id=$id, nombre=$nombre)"
}
```

**Reglas:**
- `class` (NO `data class`) — evita problemas con proxies Hibernate
- `var` en campos mutables, `val` solo para `id` y timestamps de creacion
- `equals`/`hashCode` basados en `id` — seguro para colecciones y proxies lazy
- `toString` sin relaciones `@ManyToOne` ni `@OneToMany` — evita LazyInitializationException
- Timestamps como `Instant` (no `LocalDateTime`) — consistencia timezone

## Decision: Ubicacion de Entidades por Servicio

| Servicio | Schema Actual | Entidades Nuevas | Entidades a Refactorizar |
|----------|:------------:|------------------|--------------------------|
| auth | siga_saas + siga_comercial | UsuarioLocal | UsuarioSaas, UsuarioComercial, Permiso, RolPermiso, UsuarioPermiso |
| inventario | siga_saas | Movimiento, Alerta | Producto, Stock, Local, Categoria |
| ventas | siga_saas + siga_comercial | TurnoCaja, TransaccionPos, MetodoPago, CarritoPos | Venta, DetalleVenta, Factura |
| backend | siga_comercial | Plan, Suscripcion, Pago, CarritoComercial | (entidades duplicadas del monolito, no se tocan) |

## Decision: Referencias Cross-Schema

Las referencias entre schemas se implementan como IDs simples SIN `@ManyToOne`:

```kotlin
// CORRECTO — referencia logica
@Column(name = "usuario_id")
var usuarioId: Int  // referencia logica a siga_auth.usuarios

// INCORRECTO — FK cross-schema
@ManyToOne
@JoinColumn(name = "usuario_id")
var usuario: UsuarioSaas  // NO — acopla servicios
```

## Decision: Enums

Los enums de estado se definen como `enum class` en Kotlin y se persisten como `@Enumerated(EnumType.STRING)`:

```kotlin
enum class EstadoTurno { ABIERTO, CERRADO }
enum class TipoMovimiento { ENTRADA, SALIDA, VENTA, AJUSTE, TRASLADO }
enum class TipoAlerta { STOCK_BAJO, STOCK_AGOTADO, VENTA_ALTA, MOVIMIENTO_SOSPECHOSO }
enum class EstadoTransaccion { COMPLETADA, CANCELADA, REEMBOLSADA }
```

## Decision: ALTER TABLE

Un unico script SQL para la columna nueva:

```sql
ALTER TABLE siga_saas.turnos_caja
ADD COLUMN monto_contado NUMERIC(10,2);

COMMENT ON COLUMN siga_saas.turnos_caja.monto_contado
IS 'Monto contado fisicamente por el cajero al cerrar caja';
```

## Arquitectura Final

```
services/
├── auth/entity/
│   ├── UsuarioSaas.kt          (refactor)
│   ├── UsuarioComercial.kt     (refactor)
│   ├── Permiso.kt              (refactor)
│   ├── RolPermiso.kt           (refactor)
│   ├── UsuarioPermiso.kt       (refactor)
│   └── UsuarioLocal.kt         (NEW)
├── inventario/entity/
│   ├── Producto.kt             (refactor)
│   ├── Stock.kt                (refactor)
│   ├── Local.kt                (refactor)
│   ├── Categoria.kt            (refactor)
│   ├── Movimiento.kt           (NEW)
│   └── Alerta.kt               (NEW)
├── ventas/entity/
│   ├── Venta.kt                (refactor)
│   ├── Factura.kt              (refactor)
│   ├── TurnoCaja.kt            (NEW)
│   ├── TransaccionPos.kt       (NEW)
│   ├── MetodoPago.kt           (NEW)
│   └── CarritoPos.kt           (NEW)
└── backend/entity/             (monolito — solo entidades nuevas)
    ├── Plan.kt                 (NEW — si no existe)
    ├── Suscripcion.kt          (NEW — si no existe)
    ├── Pago.kt                 (NEW — si no existe)
    └── CarritoComercial.kt     (NEW — si no existe)
```
