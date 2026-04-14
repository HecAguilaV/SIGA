## Exploration: Análisis de 'arquitectura-agentica'

### Current State
El directorio `arquitectura-agentica/` es, en esencia, la **Constitución de SIGA**. Contiene la visión estratégica (`PROPOSITO_REAL_SIGA.md`), las reglas de comportamiento del agente (`REGLAS_DE_ORO.md`, `CONSTITUCION_AGENTE.md`) y el análisis de la arquitectura del ecosistema (`DIAGRAMA_SISTEMA.md`).

A diferencia de otras carpetas que hemos limpiado, esta **no es una herramienta**, es el **Contenido Vital** que le da sentido al desarrollo.

### Affected Areas
- `arquitectura-agentica/` (Carpeta raíz actual)
- `openspec/core/` (Nueva ubicación propuesta)

### Approaches

1. **Mantenerlo en la Raíz**
   - **Pros**: Visibilidad inmediata.
   - **Cons**: Rompe la estética de "Root Limpio" que estamos persiguiendo. Fragmenta la documentación (parte en `openspec`, parte aquí).

2. **Integrar en `openspec/core/`** (Recomendado)
   - **Pros**: Centraliza toda la "Verdad" (técnica y estratégica) en un solo lugar. Cumple con el deseo de limpieza del usuario. Al ser documentos de texto, encajan perfectamente en la filosofía de OpenSpec.
   - **Cons**: Requiere que el usuario sepa que la "filosofía" ahora vive dentro de `openspec`.

3. **Mover a `EXAMEN/`**
   - **Pros**: Relacionado con el material académico.
   - **Cons**: Mucho de este contenido es para el AGENTE, no solo para el profesor. No es el sitio correcto para reglas de oro activas.

### Recommendation
**Integrar en `openspec/core/`**. 
Propongo renombrar y mover este contenido a una subcarpeta de `openspec` llamada `core/` o `manifesto/`. Esto consolida a `openspec` como el único directorio de diseño y estrategia del proyecto.

### Risks
- **Extravío Cognitivo**: Si el usuario busca "Reglas de Oro" en la raíz y no las ve, podría pensar que se borraron.
- **Mitigación**: Informar claramente que `openspec` es ahora el "Cerebro" total del proyecto.

### Ready for Proposal
**Yes**. La importancia de estos archivos es crítica; deben ser protegidos y organizados.
