# Shared Kernel (siga-common)

Biblioteca compartida de utilidades y configuraciones transversales (Cross-cutting concerns).

## 🛠 Contenido
- **Auditoría**: Motor de registro de auditoría basado en filtros HTTP.
- **Excepciones**: Manejo global de errores y códigos de respuesta.
- **Utilidades**: Helpers para manejo de fechas, UUIDs y validaciones.

## 📡 Uso
Se importa como dependencia en los microservicios mediante Gradle:
```kotlin
implementation(project(":services:common"))
```

## 🏗 Arquitectura
- [x] Spring Boot AutoConfiguration
- [x] Cumplimiento Ley 21.719 (Auditoría inmutable)

---
> "El ADN compartido de SIGA."
