# JPA Entity Mapping for Kotlin

Este protocolo define las reglas de diseño para entidades JPA en Kotlin, evitando los peligros de las `data class` y optimizando el rendimiento de Spring Data.

## 🚫 Reglas de Oro
- **No uses `data class` para entidades JPA.** Usa `class` normales.
- Mantén los DTOs de transporte y las entidades de persistencia separados.
- No uses `FetchType.EAGER` por defecto.

## 🆔 Identidad y Igualdad
- No aceptes `equals`/`hashCode` basados en todos los campos generados por `data class`.
- Usa igualdad basada en ID con un `hashCode` estable.

## 🛡️ Guardrails
- Diagnostica N+1 mirando los logs de SQL reales.
- Prefiere `@EntityGraph` o `JOIN FETCH` para soluciones de carga.
- No expongas entidades directamente en las respuestas de la API.
