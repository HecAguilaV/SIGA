# Manifiesto de Auditoría y Limpieza (SIGA) 🧹📝

Este documento lista todos los archivos identificados como redundantes, obsoletos o "basura" tras la unificación en monorepo.

## 📁 Zona Crítica: `services/mobile/`
Encontramos que este directorio tiene carpetas que parecen ser réplicas de todo el monorepo.
- `[DELETE] .github/`: Redundante (las acciones deben ser globales).
- `[DELETE] arquitectura-agentica/`: Ya centralizada en la raíz.
- `[DELETE] SIGA-APP/`, `[DELETE] SIGA-BACKEND/`, `[DELETE] SIGA-WEBAPP/`, `[DELETE] SIGA-WEBCOMERCIAL/`: **Urgente**. Parecen ser copias duplicadas de los servicios.
- `[MOVE] docs/` -> `docs/origen/mobile/`: Mover documentación histórica.
- `[MOVE] demo-sigaapp.mp4` -> `docs/media/`: Centralizar media.
- `[DELETE] ver_diagrama.html`, `[DELETE] ver_diagrama_pro.html`: Ya cubiertos por Mermaid u otros diagramas centrales.

## 📁 Zona: `services/comercial/`
- `[DELETE] test_result_final*.txt`: Archivos temporales de pruebas pasadas (v1 a v4).
- `[DELETE] .zshrc.local`: Configuración personal que no debe estar en el repo.
- `[DELETE] karma.conf.js` / `karma.conf.cjs`: Verificar si se usan; si no, eliminar por redundancia de tests.

## 📁 Zona: `services/backend/`
- `[DELETE] tatus`: Archivo posiblemente mal nombrado u obsoleto.
- `[MOVE] docs/` -> `docs/origen/backend/`: Limpieza de historial.

## 📁 Zona: `services/webapp/`
- `[DELETE] debug_auth.js`, `[DELETE] debug_cors.js`: Archivos de depuración temporal.

## 🤝 Metodología de Organización
1.  **Directorio `docs/origen/`**: Se creará para albergar TODA la documentación que no sea la guía operativa actual, pero que queramos conservar por referencia histórica.
2.  **Referencia en README**: El README principal tendrá una sección de "Historia y Origen" apuntando a esta carpeta.

---
*¿Estás de acuerdo con proceder con esta lista? Si falta algo o quieres salvar algún archivo, dímelo.*
