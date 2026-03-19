# CI/CD & Automatización Real (Pipeline Skill)

Protocolo para que el código viaje de la mente de Héctor a producción de forma automática y segura.

## 🏗️ Integración Continua (CI)
- **Build & Test**: Ejecutar `./gradlew build` y `npm test` en cada Push/PR.
- **Calidad**: Pasar linters y comprobaciones de seguridad (SQLi check automático).
- **Dockerización**: Generar la imagen Docker solo si los tests pasan.

## 🚀 Despliegue Continuo (CD)
- **Environments**: Separar `Staging` (para pruebas de Misael) y `Production` (para el cliente real).
- **Zero-Downtime**: Blue-Green deployment o Rolling updates para que el emprendedor nunca se detenga.
- **Notificaciones**: Avisar en el canal de comunicación cuando un despliegue es exitoso.

## 🛠️ Estándar de YAML
- **Reutilización**: Usar GitHub Actions Reusable Workflows.
- **Secretos**: NUNCA hardcodear API Keys. Usar GitHub Secrets.
- **Idempotencia**: El pipeline debe poder fallar y reintentarse sin corromper nada.
