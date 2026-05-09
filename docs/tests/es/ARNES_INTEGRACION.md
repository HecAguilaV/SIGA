# Arnés de Pruebas de Integración

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../en/INTEGRATION_HARNESS.md)*

Este documento detalla la infraestructura creada para validar los microservicios de SIGA, centrándose en la transición a **UUID** y la seguridad.

## Microservicios Cubiertos
- **Auth**: Gestión de identidad y permisos (UUID).
- **Inventory**: Gestión de catálogo, stock y ubicaciones (UUID).

## La Llave Maestra: `BaseIntegrationTest`

Para evitar duplicidad de código y configuraciones pesadas, hemos creado una clase base que orquesta el entorno de pruebas.

### Características Clave:
- **MockMvc**: Emula peticiones HTTP sin levantar el servidor completo.
- **Random Port**: Evita colisiones de puertos en entornos de CI/CD.
- **Active Profiles**: Usa el perfil `test` para cargar `application-test.yml`.

## Superando la Seguridad en Pruebas

Durante el desarrollo, nos enfrentamos a bloqueos de Spring Security. Así los superamos:

### 1. El Bloqueo CSRF (Error 403)
**Problema:** Las peticiones POST fallaban con un `403 Forbidden`.
**Solución:** Inyectamos el soporte de seguridad en MockMvc y usamos `.with(csrf())` en cada petición.
```kotlin
mockMvc.perform(post("/api/v1/auth/users")
    .with(csrf()) // Bypass de CSRF para test
    .contentType(MediaType.APPLICATION_JSON_VALUE)
    .content(userJson))
```

### 2. Autenticación Simulada (Error 401)
**Problema:** Endpoints protegidos rechazaban la petición con `401 Unauthorized`.
**Solución:** Usamos la anotación `@WithMockUser(roles = ["ADMIN"])` para simular un usuario con privilegios.

## Persistencia y el Misterio de los Esquemas

### Multi-Esquema en H2
**Problema:** Hibernate fallaba al no encontrar esquemas como `AUTH` o `COMMERCIAL` en la base de datos de memoria.
**Solución:** Configuramos la URL de conexión en `application-test.yml` para ejecutar scripts de inicialización de esquemas.
```yaml
url: jdbc:h2:mem:testdb;...;INIT=CREATE SCHEMA IF NOT EXISTS AUTH\;CREATE SCHEMA IF NOT EXISTS COMMERCIAL
```

#### Caso 2: Multiesquema (Inventory)
Si un servicio requiere múltiples esquemas (ej. `INVENTORY`), el `application-test.yml` debe reflejarlo en la URL de H2:
```yaml
url: jdbc:h2:mem:testdb;INIT=CREATE SCHEMA IF NOT EXISTS INVENTORY
```

### 3. Migración UUID: Lista de Control de Refactorización
Para cada microservicio migrado, se han seguido estos pasos:
1. **Entidad**: Cambiar `@Id` de `Int` a `UUID?`, `@GeneratedValue(strategy = GenerationType.UUID)`.
2. **Auditoría**: Implementar `@PrePersist` y `@PreUpdate` para `createdAt`/`updatedAt`.
3. **Repositorio**: Cambiar `JpaRepository<Entity, UUID>`.
4. **Controlador**: Actualizar rutas a `/api/v1/...` y parámetros a `UUID`.
5. **Tests**: Actualizar mocks y objetos de prueba a `UUID.randomUUID()`.

### Conflicto de Tipos (INTEGER vs UUID)
**Problema:** Error `Values of types "INTEGER" and "UUID" are not comparable`.
**Lección:** Al cambiar la clave primaria de una entidad, **todas** las tablas de relación (JoinTables) y repositorios con métodos personalizados deben ser actualizados simultáneamente.

### 4. Mockito frente a la Seguridad de Nulabilidad de Kotlin (NPE en Tests)
**Problema:** `NullPointerException` al usar `any()` en parámetros no nulos de Kotlin.
**Causa:** Mockito `any()` devuelve `null` internamente, lo cual viola la restricción de tipos de Kotlin antes de que el mock pueda ejecutarse.
**Solución:** Implementamos un ayudante (helper) genérico para "engañar" al compilador de forma segura:
```kotlin
private fun <T> anyObject(): T {
    Mockito.any<T>()
    @Suppress("UNCHECKED_CAST")
    return null as T
}
```

### 5. Validación de Orquestación Hexagonal (Billing)
En la modernización de Billing, validamos que la lógica de negocio no dependa del adaptador externo (Transbank).
- **Prueba**: El `SubscriptionServiceTest` verifica que se llame al puerto `PaymentGateway` sin importar la implementación.
- **Lección**: El uso de Mocks sobre Interfaces (Puertos) garantiza que el microservicio pueda cambiar de proveedor de pagos (ej: de Ficticio a Real) sin tocar una sola línea de lógica de dominio.

## Auditoría Automática
Descubrimos que Jackson podía enviar valores nulos en campos como `createdAt`, pisando los valores por defecto de Kotlin. Implementamos ganchos de ciclo de vida para garantizar la integridad:
```kotlin
@PrePersist
fun onPrePersist() {
    val now = Instant.now()
    if (createdAt == null) createdAt = now
    updatedAt = now
}
```

---
*Este documento es parte de la Memoria Estratégica de SIGA - Sistema Inteligente de Gestión de Activos.*
