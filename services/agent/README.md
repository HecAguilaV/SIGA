# Agent Service (siga-agent)

*Read this in other languages: [![English](README.en.md)](README.en.md)*

Servicio de Inteligencia Artificial para asistencia contextual y análisis de datos.

## Stack Tecnológico
- **Lenguaje**: Python 3.11+
- **Framework**: FastAPI
- **LLM**: LangChain / OpenAI / Strands
- **Vector DB**: PGVector (PostgreSQL) - Para ingestión Big Data futura

## APIs & Contratos
- **Asistente**: `POST /api/v1/agent/chat`
- **Análisis**: `POST /api/v1/agent/analyze`

## Interrelaciones
- **Data Source**: Consulta APIs de `Sales` e `Inventory` vía Gateway para análisis de tendencias.
- **Service Registry**: Se registra en `siga-registry`.


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
