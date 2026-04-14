# Proposal: Reorganizar el Directorio de Skills

## Intent
Limpiar la raíz del proyecto SIGA moviendo las habilidades (skills) a un lugar más adecuado para documentos de estándares arquitectónicos, asegurando que sigan siendo accesibles para los agentes pero sin "ensuciar" el espacio de trabajo principal.

## Scope

### In Scope
- Mover la carpeta `skills/` completa a `openspec/skills/`.
- Actualizar todas las rutas en `.atl/skill-registry.md`.
- Sincronizar el cambio en Engram.

### Out of Scope
- Eliminar habilidades (solo se mueven).
- Cambiar el contenido de las habilidades.

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `skill-registry`: Cambio en la localización de la fuente de verdad de las habilidades.

## Approach
1. Mover el directorio físicamente.
2. Usar `sed` o una herramienta de reemplazo de texto para actualizar todas las ocurrencias de `file:///.../skills/` a `file:///.../openspec/skills/` en el archivo `.atl/skill-registry.md`.

## Affected Areas
| Area | Impact | Description |
|------|--------|-------------|
| `skills/` | Removed | Se elimina de la raíz. |
| `openspec/skills/` | New | Nueva ubicación de los estándares. |
| `.atl/skill-registry.md` | Modified | Actualización de rutas. |

## Risks
- **Desconexión**: Si la ruta queda mal, dejaré de seguir tus reglas filosóficas.
- **Mitigación**: Verificación inmediata leyendo una skill desde la nueva ruta.

## Success Criteria
- [ ] La carpeta `skills/` ya no existe en la raíz.
- [ ] `./openspec/skills/` contiene todos los archivos originales.
- [ ] `.atl/skill-registry.md` tiene las rutas actualizadas y funcionales.
