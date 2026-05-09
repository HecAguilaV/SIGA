# Shared Kernel (siga-common)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Shared library of cross-cutting utilities and configurations.

## Contents

- **Audit**: HTTP filter-based audit logging engine.
- **Exceptions**: Global error handling and response codes.
- **Utilities**: Helpers for date handling, UUIDs and validations.

## Usage

Imported as a dependency in microservices via Gradle:

```kotlin
implementation(project(":services:common"))
```

## Architecture

- [X] Spring Boot AutoConfiguration
- [X] Law 21.719 compliance (Immutable audit trail)

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
