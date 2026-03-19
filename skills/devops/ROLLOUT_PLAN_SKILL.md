# DevOps Rollout Plan Skill

Este protocolo guía la creación de planes de despliegue seguros para la migración de SIGA.

## 📋 Requisitos de Entrada
- Descripción del cambio (Infra, App, Config).
- Evaluación de riesgos (Radio de explosión).
- Plan de rollback probado.

## 🏗️ Estructura del Plan
1.  **Resumen Ejecutivo**: Qué, por qué y cuándo.
2.  **Preflight Checks**: Verificación antes de lanzar.
3.  **Step-by-Step Rollout**: Fases de despliegue progresivo.
4.  **Señales de Verificación**: Métricas inmediatas, a corto y largo plazo.
5.  **Procedimiento de Rollback**: Criterios de decisión y pasas para revertir.

## 🛡️ Reglas de Oro
- Nunca despliegues en viernes por la tarde (a menos que sea crítico).
- Monitorea métricas, no solo logs.
- Documenta todo.
- Nunca asumas que "debería funcionar".
