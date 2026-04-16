import os
from strands_agents import Agent
from .tools import get_inventory_kpis, get_sales_metrics

from .memoria import aprender, recordar

# Configuración del modelo subyacente. Usaremos Ollama en mercado, 
# pero Strands abstrae esto via LiteLLM internamente o con soporte Bedrock/OpenAI.
# Para local fallback usaremos ollama_chat
MODEL_ID = os.getenv("MODEL_ID", "ollama_chat/llama3")

def crear_agente_analista(tenant_id: str) -> Agent:
    """
    Crea una instancia inmutable y stateless del Analista para el req actual.
    Notar que inyectamos el tenant_id en el System Prompt para forzar la seguridad.
    """
    system_prompt = f"""Eres el "Analista de Negocio" de SIGA, experto en análisis de KPIs comerciales.
Trabajas bajo arquitectura Zero-Trust. Tienes estrictamente prohibido intentar acceder a datos
que no correspondan al Tenant ID {tenant_id}.

Tus objetivos:
1. Usar las herramientas disponibles para responder analíticamente las dudas del dueño de la pyme.
2. Si un microservicio rechaza tu acceso, debes reportar al usuario que sus políticas IAM no se lo permiten.
3. Se conciso, estructurado e incluye viñetas.
4. Tienes memoria a largo plazo. USA LA HERRAMIENTA `recordar` antes de dar conclusiones sobre el estado pasado, y usa `aprender` si el dueño te dice algo importante que no debes olvidar.
"""
    
    agent = Agent(
        model=MODEL_ID,
        system_prompt=system_prompt,
        tools=[get_inventory_kpis, get_sales_metrics, aprender, recordar],
        max_iterations=5, # Fallback anti-loop infinito (Heurística de Resiliencia)
    )
    
    return agent
