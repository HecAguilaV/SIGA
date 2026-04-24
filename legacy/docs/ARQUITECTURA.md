# Arquitectura SIGA - Clean Architecture

## Visión General

SIGA implementa **Clean Architecture** (Uncle Bob) con distribución en microservicios, siguiendo principios de arquitectura hexagonal.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           FRONTEND (WebApp/Comercial)                  │
└─────────────────────────────────┬──────────────��────────────────────────┘
                                  │ REST
┌─────────────────────────────────▼───────────────────────────────────────┐
│                        MICROSERVICIOS (Kotlin/Spring Boot)             │
├─────────────────────────────────────────────────────────────────────────┤
│  ┌───────────┐  ┌───────────┐  ┌─────────────┐  ┌────────────┐         │
│  │   auth   │  │  ventas   │  │ inventario │  │ backend   │   ...      │
│  └────┬────┘  └────┬────┘  └─────┬──────┘  └─────┬──────┘         │
│       │            │              │               │                    │
│       └────────────┴─────────────┴───────────────┘                    │
│                          │                                             │
│                    Eureka API Gateway                                  │
└───────────────────────────▲───────────────────────────────────────────┘
                            │
┌───────────────────────────▼───────────────────────────────────────────┐
│                    POSTGRESQL (Multi-tenant)                          │
│  ┌──────────────┐  ┌──────────────┐                              │
│  │ siga_saas   │  │siga_comercial│  (esquemas actuales)              │
│  │ (usuarios,  │  │(planes,      │                              │
│  │  productos, │  │ suscripciones│                             │
│  │  stock,     │  │ facturacion) │                              │
│  │  ventas)    │  │              │                              │
│  └────────────┘  └──────────────┘                              │
└───────────────────────────────────────────────────────────────────────┘
```

---

## Clean Architecture en Cada Microservicio

```
┌───────────────────────────────────────────────────────┐
│          interface/rest (Controllers, DTOs)         │  ← PRESENTATION
├───────────────────────────────────────────────────────┤
│          application (Use Cases, Services)             │  ← APPLICATION
├───────────────────────────────────────────────────────┤
│          domain (Entities, Value Objects)            │  ← DOMAIN (CORE)
├───────────────────────────────────────────────────────┤
│          infrastructure (DB, Clients, External)     │  ← INFRASTRUCTURE
└───────────────────────────────────────────────────────┘
              ↑ Dependencias van hacia el centro
```

### Mapeo de Archivos

| Capa | Ubicación | Ejemplo |
|------|-----------|---------|
| **domain** | `domain/entity/` | Entidades JPA del servicio |
| **application** | `application/usecase/` | `CrearUsuarioUseCase.kt` |
| **interface** | `interface/rest/` | `UsuarioController.kt` |
| **infrastructure** | `infrastructure/persistence/` | `UsuarioRepository.kt` |

---

## Comparación: MVC vs Clean Architecture

### MVC Tradicional (Monolito)
```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ Controller │────▶│  Service   │────▶│  Entity    │
└─────────────┘     └─────────────┘     └─────────────┘
       │                   │                 │
       └───────────────────┴─────────────────┘
              DEPENDENCIAS BI-DIRECCIONALES
                   (Tightly Coupled)
```

**Problemas:**
- Controller dependen de Service y Entity
- Cambiar base de datos = refactorizar TODO
- Unit tests imposibles sin mockear toda la DB
- Difícil cambiar tecnología

### Clean Architecture
```
┌─────────────────────────────────────┐
│   INTERFACE (REST Controller)        │  ← cambia UI aquí
└─────────────────┬─────────────────┘
                  │ depends on
┌─────────────────▼─────────────────┐
│   APPLICATION (Use Cases)          │  ← lógica pura
└─────────────────┬─────────────────┘
                  │ depends on
┌─────────────────▼─────────────────┐
│   DOMAIN (Entities)                │  ← NÚCLEO INDEPENDIENTE
└─────────────────┬─────────────────┘
                  │ depends on
┌─────────────────▼─────────────────┐
│   INFRASTRUCTURE (DB, AI)          │  ← solo detalles técnicos
└─────────────────────────────────────┘
```

**Beneficios:**
- Entidades testeables SIN base de datos
- Cambiar DB = solo tocar infrastructure
- Cambiar AI = solo tocar infrastructure
- Código reutilizable entre servicios

---

## Por Qué No microservices tradicionales?

| Aspecto | Microservicios Tradicionales | Clean Architecture |
|---------|---------------------------|-------------------|
| **Acoplamiento** | Bajo (entre servicios) | Bajo (entre capas) |
| **Tests** | Difíciles (necesita levantar todo) | Fáciles (capa domain testeable) |
| **Cambio de DB** | toca servicio | toca infrastructure |
| **Latencia** | Alta (llamada red) | N/A (mismo proceso) |
| **Transacciones** | Distribuidas (problema) | Local (同一 servicio) |

**Respuesta para la defensa:**
> "Los microservicios tradicionales son MVC distribuido. Tienen los mismos problemas del MVC más la latencia de red. Clean Architecture solve estos problemas locales."

---

## Esquemas de Base de Datos (Actuales)

| Schema | Servicio(es) | Tablas |
|--------|--------------|-------|
| **siga_saas** | auth, ventas, inventario, backend | USUARIOS, PERMISOS, ROLES_PERMISOS, USUARIOS_PERMISOS, USUARIOS_LOCALES, PRODUCTOS, STOCK, LOCALES, CATEGORIAS, VENTAS, DETALLES_VENTA, TURNOS_CAJA, TRANSACCIONES_POS, METODOS_PAGO, CARRITO_POS, ALERTAS, MOVIMIENTOS |
| **siga_comercial** | auth, backend | USUARIOS (clientes comerciales), PLANES, SUSCRIPCIONES, PAGOS, FACTURAS, CARRITOS |

> **Nota:** El documento `docs/database/MODELO_RELACIONAL.md` propone una reorganización futura a 5 esquemas (`siga_auth`, `siga_inventario`, `siga_ventas`, `siga_comercial`, `siga_agente`). Esta migración se realizará en fases.

---

## Estado de Implementación en SIGA

### ✅ auth (listo)
```
services/auth/
├── entity/              → domain/
│   ├── UsuarioSaas.kt
│   ├── UsuarioComercial.kt
│   └── Permiso.kt
└── security/            → application/
    └── JwtService.kt
```

### ✅ ventas (listo)
```
services/ventas/
├── entity/              → domain/
│   ├── Venta.kt
│   ├── TurnoCaja.kt
│   └── DetalleVenta.kt
└── client/
    └── InventarioClient.kt  → infrastructure/
```

### 🔄 backend (en refactorización)
```
services/backend/
├── controller/    → interface/rest/
├── service/      → application/
├── entity/        → domain/
├── repository/   → infrastructure/persistence/
└── config/       → infrastructure/
```

---

## Reglas de la Convención JPA (Domain Layer)

Todas las entidades en SIGA siguen:

```kotlin
@Entity
@Table(name = "PRODUCTOS", schema = "siga_saas")
class Producto(
    @Id @GeneratedValue val id: Int = 0,
    var nombre: String,
    // ... campos con var para mutables
) {
    // equals/hashCode por ID único
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return id != 0 && id == other.id
    }

    // toString SIN relaciones lazy
    override fun toString(): String = "Producto(id=$id)"
}
```

**Por qué no `data class`:**
- JPA manejaequals/hashCode por ID, no por todos los campos
- `data class` genera toString con TODOS los campos (incluye relaciones lazy = StackOverflowError)
- copy() manual da control sobre qué campos copiar

---

## Testing Unitario con Clean Architecture

```kotlin
// domain/test/ProductoTest.kt
@Test
fun `producto equals by id only`() {
    val p1 = Producto(1, "Cerveza", precio = 1000)
    val p2 = Producto(1, "CERVEZA", precio = 999) // diferente pero mismo ID
    
    assertEquals(p1, p2)       // true! (equals por ID)
    assertEquals(p1.hashCode(), p2.hashCode())
}

// application/test/CrearProductoUseCaseTest.kt
@Test
fun `crear producto sin levant DB`() {
    val mockRepo = mock<ProductoRepository>()
    whenever(mockRepo.save(any())).thenReturn(producto)
    
    val useCase = CrearProductoUseCase(mockRepo)
    val result = useCase.execute("Cerveza", 1000)
    
    assertTrue(result.success)
    verify(mockRepo).save(any())
}
```

**Clave:** Tests de dominio NO necesitan base de datos. Solo la capa infrastructure necesita mocks.

---

## Referencias

- Robert C. Martin - "Clean Architecture" (2017)
- Alistair Cockburn - "Hexagonal Architecture" (2005)
- Vernon Vaughn - "Implementing Domain-Driven Design" (2013)