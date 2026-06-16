# Agent Service (siga-agent)

*Read this in other languages: [![English](README.en.md)](README.en.md)*

Servicio de Inteligencia Artificial para asistencia contextual y análisis de datos, con soporte del protocolo A2UI v0.9.

## Stack Tecnológico
- **Lenguaje**: Kotlin 2.2.0
- **Framework**: Spring Boot 3.4.3 (WebFlux)
- **LLM**: Google Gemini API (GenAI SDK)
- **Service Discovery**: Eureka Client
- **Build Tool**: Gradle

## APIs & Contratos

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/health` | GET | Health check del servicio |
| `/api/agent/a2ui` | POST | Interfaz principal A2UI v0.9 — recibe `{message, context?, history[], mode?}` y retorna un envelope A2UI (`{surfaceId, surface: CreateSurface, provenance}`) |
| `/api/agent/chat/stream` | GET | SSE legacy — parámetros `message`, `context`, `history`; eventos: `chunk`, `done`, `error`, `tool`, `a2ui` |

## Arquitectura

```
Controller → Service → Engine (3-tier)
  ├── Tier 1: GeminiEngine (Google A2UI SDK)
  ├── Tier 2: FallbackEngine (keyword matcher + SQL)
  └── Tier 3: Catalog (sugerencias por defecto)
```

## Cómo construir y ejecutar

```bash
# Construir
./gradlew :services:agent:build

# Ejecutar
./gradlew :services:agent:bootRun

# Docker
docker build -t siga-agent -f services/agent/Dockerfile .
docker run -p 8000:8000 siga-agent
```

## Variables de Entorno

| Variable | Descripción | Requerida |
|----------|-------------|-----------|
| `GEMINI_API_KEY` | API key de Google Gemini | ✅ |
| `GEMINI_MODEL_ID` | Model ID de Gemini (ej: `gemini-2.0-flash`) | ✅ |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL de Eureka | ✅ |

## Interrelaciones
- **Service Registry**: Se registra en `siga-eureka`.
- **Base de Datos**: Conexión directa a PostgreSQL vía JDBC para consultas del FallbackEngine.
- **Gateway**: Accesible a través de `siga-gateway` en `:8080`.


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
