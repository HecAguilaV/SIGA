# Agent Service (siga-agent)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Artificial Intelligence service for contextual assistance and data analysis, with A2UI v0.9 protocol support.

## Tech Stack
- **Language**: Kotlin 2.2.0
- **Framework**: Spring Boot 4.0.6 (WebFlux)
- **LLM**: Google Gemini API (GenAI SDK)
- **Service Discovery**: Eureka Client
- **Build Tool**: Gradle

## APIs & Contracts

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Service health check |
| `/api/agent/a2ui` | POST | Main A2UI v0.9 interface — receives `{message, context?, history[], mode?}` and returns an A2UI envelope (`{surfaceId, surface: CreateSurface, provenance}`) |
| `/api/agent/chat/stream` | GET | Legacy SSE — params `message`, `context`, `history`; events: `chunk`, `done`, `error`, `tool`, `a2ui` |

## Architecture

```
Controller → Service → Engine (3-tier)
  ├── Tier 1: GeminiEngine (Google A2UI SDK)
  ├── Tier 2: FallbackEngine (keyword matcher + SQL)
  └── Tier 3: Catalog (default suggestions)
```

## Build & Run

```bash
# Build
./gradlew :services:agent:build

# Run
./gradlew :services:agent:bootRun

# Docker
docker build -t siga-agent -f services/agent/Dockerfile .
docker run -p 8000:8000 siga-agent
```

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `GEMINI_API_KEY` | Google Gemini API key | ✅ |
| `GEMINI_MODEL_ID` | Gemini model ID (e.g., `gemini-2.0-flash`) | ✅ |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka service URL | ✅ |

## Interconnections
- **Service Registry**: Registers with `siga-eureka`.
- **Database**: Direct PostgreSQL connection via JDBC for FallbackEngine queries.
- **Gateway**: Accessible through `siga-gateway` at `:8080`.


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
