# Agent Service (siga-agent)

Servicio de Inteligencia Artificial para asistencia contextual y análisis de datos.

## Stack Tecnológico
- **Lenguaje**: Python 3.11+
- **Framework**: FastAPI
- **LLM**: LangChain / OpenAI
- **Vector DB**: ChromaDB / Pinecone (Propuesta)

## APIs & Contratos
- **Asistente**: `POST /api/v1/agent/chat`
- **Análisis**: `POST /api/v1/agent/analyze`

## Interrelaciones
- **Data Source**: Consulta APIs de `Sales` e `Inventory` vía Gateway para análisis de tendencias.
- **Service Registry**: Se registra en `siga-registry`.


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
