# Integration Harness

This document details the infrastructure created to validate SIGA microservices, focusing on the transition to **UUID** and security.

## Covered Microservices
- **Auth**: Identity and permissions management (UUID).
- **Inventory**: Catalog, stock, and location management (UUID).

## The Master Key: `BaseIntegrationTest`

To avoid code duplication and heavy configurations, we have created a base class that orchestrates the testing environment.

### Key Features:
- **MockMvc**: Emulates HTTP requests without starting the full server.
- **Random Port**: Prevents port collisions in CI/CD environments.
- **Active Profiles**: Uses the `test` profile to load `application-test.yml`.

## Overcoming Security in Testing

During development, we faced Spring Security blocks. Here is how we overcame them:

### 1. CSRF Block (Error 403)
**Problem:** POST requests failed with a `403 Forbidden`.
**Solution:** We injected security support into MockMvc and used `.with(csrf())` in each request.
```kotlin
mockMvc.perform(post("/api/v1/auth/users")
    .with(csrf()) // CSRF bypass for test
    .contentType(MediaType.APPLICATION_JSON_VALUE)
    .content(userJson))
```

### 2. Simulated Authentication (Error 401)
**Problem:** Protected endpoints rejected the request with `401 Unauthorized`.
**Solution:** We used the `@WithMockUser(roles = ["ADMIN"])` annotation to simulate a privileged user.

## Persistence and the Schema Mystery

### Multi-Schema in H2
**Problem:** Hibernate failed to find schemas like `AUTH` or `COMMERCIAL` in the in-memory database.
**Solution:** We configured the connection URL in `application-test.yml` to execute schema initialization scripts.
```yaml
url: jdbc:h2:mem:testdb;...;INIT=CREATE SCHEMA IF NOT EXISTS AUTH\;CREATE SCHEMA IF NOT EXISTS COMMERCIAL
```

#### Case 2: Multi-schema (Inventory)
If a service requires multiple schemas (e.g., `INVENTORY`), the `application-test.yml` must reflect it in the H2 URL:
```yaml
url: jdbc:h2:mem:testdb;INIT=CREATE SCHEMA IF NOT EXISTS INVENTORY
```

### 3. UUID Migration: Refactoring Checklist
For each migrated microservice, these steps have been followed:
1. **Entity**: Change `@Id` from `Int` to `UUID?`, `@GeneratedValue(strategy = GenerationType.UUID)`.
2. **Audit**: Implement `@PrePersist` and `@PreUpdate` for `createdAt`/`updatedAt`.
3. **Repository**: Change `JpaRepository<Entity, UUID>`.
4. **Controller**: Update paths to `/api/v1/...` and parameters to `UUID`.
5. **Tests**: Update mocks and test objects to `UUID.randomUUID()`.

### Type Conflict (INTEGER vs UUID)
**Problem:** Error `Values of types "INTEGER" and "UUID" are not comparable`.
**Lesson:** When changing the primary key of an entity, **all** relationship tables (JoinTables) and repositories with custom methods must be updated simultaneously.

### 4. Mockito vs Kotlin Null-Safety (NPE in Tests)
**Problem:** `NullPointerException` when using `any()` on non-nullable Kotlin parameters.
**Cause:** Mockito `any()` returns `null` internally, which violates Kotlin's type restriction before the mock can execute.
**Solution:** We implemented a generic helper to safely "trick" the compiler:
```kotlin
private fun <T> anyObject(): T {
    Mockito.any<T>()
    @Suppress("UNCHECKED_CAST")
    return null as T
}
```

### 5. Hexagonal Orchestration Validation (Billing)
In the Billing modernization, we validated that the business logic does not depend on the external adapter (Transbank).
- **Test**: `SubscriptionServiceTest` verifies that the `PaymentGateway` port is called regardless of the implementation.
- **Lesson**: Using Mocks over Interfaces (Ports) ensures that the microservice can change payment providers (e.g., from Fictitious to Real) without touching a single line of domain logic.

## Automatic Audit
We discovered that Jackson could send null values in fields like `createdAt`, overwriting Kotlin's default values. We implemented lifecycle hooks to ensure integrity:
```kotlin
@PrePersist
fun onPrePersist() {
    val now = Instant.now()
    if (createdAt == null) createdAt = now
    updatedAt = now
}
```

---
*This document is part of SIGA's Strategic Memory - Intelligent Asset Management System.*
