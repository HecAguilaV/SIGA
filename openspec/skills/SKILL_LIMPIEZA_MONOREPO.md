# SKILL: Protocolo de Auditoría y Limpieza de Monorepo (SIGA) 

Este documento es una **habilidad modular** para agentes de IA. Define cómo mantener la salud documental de SIGA.

##  Objetivo
Identificar, mover o eliminar archivos que generen ruido técnico ("trash") tras una unificación de repositorios, garantizando la preservación histórica.

##  Procedimiento de Auditoría
1.  **Detectar Duplicados**: Buscar carpetas `.github/`, `arquitectura-agentica/` o `.vscode/` dentro de sub-servicios en `services/`.
2.  **Filtrar Temporales**: Identificar archivos de resultados de tests (`test_results_*.txt`), volcados de depuración (`debug_*.js`) o logs.
3.  **Identificar Herencia**: Localizar archivos `.gitignore`, `README.md` o `LICENSE` antiguos que ya estén cubiertos por el monorepo raíz.

##  Reglas de Movimiento
- **Guía Operativa**: El archivo debe estar en la raíz del servicio o en `arquitectura-agentica/`.
- **Memoria Histórica**: Mover a `docs/origen/[servicio]/`. Nunca borrar documentación única de origen sin autorización.

## ️ Reglas de Eliminación
- **Basura Real**: Resultados de tests locales, archivos temporales de editores, volcados de variables de entorno de prueba.
- **Redundancia Total**: Cuando un archivo es una copia exacta bit a bit de otro ya centralizado.

---
*Esta Skill ha sido diseñada para SIGA v1.0 - Marzo 2026*
