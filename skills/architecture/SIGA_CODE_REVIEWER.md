# Skill: SIGA Code Reviewer (Auditor de Cimientos)

Este protocolo define cómo el Orquestador debe revisar el código fuente antes de integrarlo al Monorepo. Responde directamente a la necesidad de no tener "bases endebles".

## Objetivo
Prevenir que las bases estructurales de SIGA colapsen por mala arquitectura. Cada Microservicio, endpoint de API o flujo de IA debe cumplir con las reglas de "Cero Fricción", "Blindaje de Seguridad" y "Resiliencia Estructural".

## Reglas de Revisión (Checklist Arquitectónico)
1. **Verificación de Esquemas de Base de Datos**:
   - ¿El código respeta la división real observada (ej. `siga_comercial` para SaaS vs `siga_saas` para Inventario/Local/Rol)? 
   - No se deben mezclar contextos pesados.
2. **Autorización Contextual Dinámica (RBAC/ABAC)**:
   - ¿Las funciones del Agente o del Backend heredan y verifican el JWT / `UsuarioPermiso`?
   - Si es un Chofer, el API Gateway / BFF debe cortarle el paso a permisos CRUD de stock antes de que llegue a la lógica pesada.
3. **Resiliencia Extrema (Mecanismo Fallback IA)**:
   - Si se desarrolla el Agente, es **OBLIGATORIO** que las llamadas al LLM (gemini u o1) estén envueltas en control de errores.
   - En caso de caída de la API, el bloque de rescate (Fallback) debe disparar una consulta nativa SQL/PL-SQL predefinida o devolver un mensaje de sistema transparente: "Servicio Inteligente degradado temporalmente. Retornando consulta directa." ¡El usuario nunca queda tirado!
4. **Cero Ruido**:
   - Sin emojis genéricos en la documentación formal ni dependencias experimentales innecesarias.

---
> Aprobado por: COLEGA Orchestrator
