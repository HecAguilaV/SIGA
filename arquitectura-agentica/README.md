<p align="center">
  <img src="../docs/brand/Logo_SIGA.png" alt="Logo SIGA" width="220" />
</p>

# Centro de Conocimiento Agéntico 🧠

Este directorio es el "cerebro" de SIGA para la interacción con Inteligencia Artificial.

## 🤖 Colaboración Humano-Agente
SIGA se desarrolla bajo un ecosistema vivo donde los agentes de IA (como Antigravity) operan bajo las **[Reglas de Oro](file:///c:/Users/hdagu/Desktop/SIGA/arquitectura-agentica/REGLAS_DE_ORO.md)**.

### 🛠️ Metodología SDD (Spec-Driven Development)
Todo cambio arquitectónico sigue este flujo:
1.  **Planificación**: Definir specs aquí en `arquitectura-agentica/`.
2.  **Ejecución**: Implementación modular.
3.  **Verificación**: Pruebas cruzadas entre servicios.

## 📁 Estructura del Contexto
- `AGENTIC-backend.md`: Motor Kotlin, prompts y fallbacks.
- `AGENTIC-webapp.md`: Administración Svelte5.
- `AGENTIC-mobile.md`: Operación Android.
- `AGENTIC-comercial.md`: Portal React.
- `SIGA_STATUS.md`: Estado actual del ecosistema.

## 🛡️ Resiliencia y Memoria
- **Fallback**: Usamos SafeMode (SQL) si la API de IA falla.
- **Memoria**: Utilizamos el sistema de Engram para persistir decisiones críticas.

---
*Consulta `docs/HISTORIA.md` para entender el origen de este ecosistema.*
