# Shared Kernel (siga-common)

*Read this in other languages: [![English](README.en.md)](README.en.md)*

Biblioteca compartida de utilidades y configuraciones transversales (Cross-cutting concerns).

## Contenido

- **Auditoría**: Motor de registro de auditoría basado en filtros HTTP.
- **Excepciones**: Manejo global de errores y códigos de respuesta.
- **Utilidades**: Helpers para manejo de fechas, UUIDs y validaciones.

## Uso

Se importa como dependencia en los microservicios mediante Gradle:

```kotlin
implementation(project(":services:common"))
```

## Arquitectura

- [X] Spring Boot AutoConfiguration
- [X] Cumplimiento Ley 21.719 (Auditoría inmutable)


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
