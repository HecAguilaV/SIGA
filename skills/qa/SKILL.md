# ScoutQA Testing Skill

Este protocolo guía el uso de IA para pruebas exploratorias automáticas en las aplicaciones web de SIGA (WebApp y Portal Comercial).

## 🚀 Flujo de Trabajo
1.  **Definir Prompt**: Instrucciones claras de qué probar (ej. "Verificar flujo de registro").
2.  **Ejecutar**: `scoutqa --url "http://localhost:3000" --prompt "..."`.
3.  **Monitorizar**: Usar el Execution ID para seguir el progreso en el navegador.
4.  **Analizar**: Extraer issues y verificar arreglos con `issue-verify`.

## 🛡️ Escenarios Comunes
- **Smoke Test**: Verificar funcionalidades críticas post-despliegue.
- **Accesibilidad**: Auditoría de cumplimiento WCAG 2.1 AA.
- **Responsividad**: Validar comportamiento en dispositivos móviles.
- **Validación de Forms**: Casos de borde y manejo de errores.

## 🧪 Integración SDD
Esta skill se activará en la fase de **Verify** de cada ciclo SDD para asegurar que ninguna funcionalidad se rompa durante la migración.
