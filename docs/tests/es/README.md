# SIGA: Calidad y Estrategia de Pruebas

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../en/README.md)*

Este directorio contiene la documentación estratégica de las pruebas del sistema SIGA. Nuestro objetivo es garantizar la seguridad, la estabilidad y el cumplimiento de la **Ley 21.719 (Seudonimización de datos)** mediante un enfoque bilingüe y profesional.

## Filosofía de Calidad
1. **Security by Design**: No se escribe código sin considerar el blindaje de credenciales.
2. **TDD Estricto**: Seguimos el ciclo Rojo-Verde-Refactor para cada cambio estructural (como la migración a UUID).
3. **Memoria Estratégica**: Cada fallo encontrado y superado debe ser documentado para evitar regresiones.

## Pirámide de Pruebas en SIGA

### 1. Auditoría Estática (Security Scans)
- **Gitleaks**: Escaneo preventivo de secretos en el historial de Git.
- **Semgrep**: Análisis de código estático para detectar patrones de seguridad inseguros.
- *Documentación:* [AUDITORIAS_SEGURIDAD.md](AUDITORIAS_SEGURIDAD.md)

### 2. Pruebas de Integración (Harness)
- **BaseIntegrationTest**: Nuestra "llave maestra" para probar microservicios con Spring Boot, MockMvc y H2.
- **Validación UUID**: Garantizamos que cada entidad generada cumpla con el estándar de 128 bits.
- *Documentación:* [ARNES_INTEGRACION.md](ARNES_INTEGRACION.md)

### 3. Pruebas de Persistencia
- Validación de esquemas múltiples en PostgreSQL y su emulación en H2.
- Pruebas de integridad referencial bilingües.

---
*SIGA - Sistema Inteligente de Gestión de Activos*
