# SIGA PROTOTIPO

Prototipo interactivo de alto impacto para SIGA, construido con **SvelteKit 5**, **Bulma** y **Chart.js**, desplegable en Vercel. Demuestra las tres funcionalidades clave del producto: Inventario, Análisis y Asistente inteligente respaldado por una API de LLM.

## 🧰 Tecnologías
- SvelteKit 5
- Bulma CSS
- Chart.js
- Vercel Serverless Functions

## 🚀 Puesta en marcha local
```bash
npm install
npm run dev
```
La aplicación se ejecutará en `http://localhost:5173`.

Para que el asistente responda desde la IA, crea un archivo `.env` en la raíz con:
```
GEMINI_API_KEY=tu_clave_privada
```

## 📦 Estructura destacada
- `src/routes/+page.svelte`: módulo de Inventario con filtro por local.
- `src/routes/analisis/+page.svelte`: módulo Analítico con gráfico de barras y explicación guiada.
- `src/routes/asistente/+page.svelte`: asistente conversacional que renderiza gráfico de mermas si el LLM devuelve `[GRAFICO_MERMAS]`.
- `src/routes/api/chat/+server.js`: función serverless que invoca al LLM usando RAG sobre los datos simulados.
- `src/lib/datosSimulados.js`: store writable con locales, productos, ventas y mermas.

## ☁️ Despliegue en Vercel
1. Sube el repositorio a GitHub y enlázalo en Vercel.
2. En el panel del proyecto, define la variable de entorno `GEMINI_API_KEY` (Preview/Production/Development según necesites).
3. Vercel detectará SvelteKit automáticamente y generará el build. Tras el despliegue, prueba `/asistente` para validar la integración con la IA.

## 📝 Licencia
Este proyecto incluye el archivo `LICENSE` heredado del repositorio remoto.
