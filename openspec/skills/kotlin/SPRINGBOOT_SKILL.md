# Spring Boot with Kotlin Best Practices (GitHub Official)

Este protocolo define el estándar de codificación para el backend de SIGA, asegurando un código idiomático y de alta calidad.

## ️ Estructura y DI
- **Inyección de Dependencias**: Usar constructor primario con `private val`. Priorizar `val` (inmutabilidad).
- **Estructura por Dominio**: Organizar por funcionalidad (`user`, `inventory`) en lugar de capas.

##  Capa Web (Controllers)
- **DTOs**: Usar `data class` para inmutabilidad y métodos automáticos.
- **Validación**: Usar JSR 380 (`@Valid`, `@NotNull`) en los DTOs.
- **Error Handling**: `@ControllerAdvice` global para respuestas consistentes.

##  Capa de Datos y Reactividad
- **Coroutines**: Usar `suspend` functions para código no bloqueante.
- **JPA entities**: Usar `open class` (ayudado por el plugin `kotlin-jpa`). No usar `data class` para entidades.

##  Testing
- **Herramientas**: JUnit 5 + MockK (orientado a Kotlin).
- **Slices**: `@WebMvcTest` y `@DataJpaTest` para pruebas enfocadas.
