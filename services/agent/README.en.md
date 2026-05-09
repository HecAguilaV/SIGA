# Agent Service (siga-agent)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Artificial Intelligence service for contextual assistance and data analysis.

## Tech Stack
- **Language**: Python 3.11+
- **Framework**: FastAPI
- **LLM**: LangChain / OpenAI / Strands
- **Vector DB**: PGVector (PostgreSQL) - For future Big Data ingestion

## APIs & Contracts
- **Assistant**: `POST /api/v1/agent/chat`
- **Analysis**: `POST /api/v1/agent/analyze`

## Interconnections
- **Data Source**: Queries `Sales` and `Inventory` APIs via Gateway for trend analysis.
- **Service Registry**: Registers with `siga-registry`.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
