## Exploration: Reorganización de Skills (Habilidades) SIGA

### Current State
El directorio `skills/` reside en la raíz y contiene la "filosofía de ingeniería" y patrones técnicos del proyecto. Aunque es vital para que yo (el agente) trabaje correctamente, el usuario siente que "ensucia" la raíz mientras estos patrones no se están aplicando activamente en el código.

### Affected Areas
- `skills/` (Carpeta raíz)
- `.atl/skill-registry.md` (Debe actualizarse para que los triggers sigan funcionando)
- `openspec/config.yaml` (Si tiene referencias a rutas de skills)

### Approaches

1. **Mover a `openspec/skills/`** (Recomendado)
   - **Pros**: Centraliza toda la "verdad" y el diseño en un solo lugar (`openspec`). Limpia la raíz. Como ya subimos `openspec` al repo, las habilidades viajan con él.
   - **Cons**: Mezcla especificaciones de cambios con estándares globales.
   - **Effort**: Low

2. **Mover a `.atl/skills/`**
   - **Pros**: Mantiene las habilidades en una carpeta oculta (`.atl/` ya está en el registry).
   - **Cons**: Hace que las habilidades sean más difíciles de editar para el humano, ya que las carpetas ocultas suelen esconderse en los editores.
   - **Effort**: Low

3. **Mover a `docs/skills/`**
   - **Pros**: Estándar de la industria.
   - **Cons**: Añade otra carpeta a la raíz, lo cual no resuelve el problema de "limpieza" que busca el usuario.

### Recommendation
**Mover a `openspec/skills/`**. 
Dado que ya establecimos que `openspec` es el "Corazón Arquitectónico" y que el usuario quiere que la memoria sea agnóstica a la rama, tener las habilidades dentro de la estructura de `openspec/` tiene todo el sentido semántico.

### Risks
- **Riesgo**: Que yo pierda el acceso a las reglas si el `skill-registry` no se actualiza perfectamente.
- **Mitigación**: Ejecutar un script de reemplazo global en el registry tras el movimiento.

### Ready for Proposal
**Yes**. El camino está claro.
