import os
from strands_agents import Agent
from .tools import consultar_stock_exacto, registrar_venta

MODEL_ID = os.getenv("MODEL_ID", "ollama_chat/llama3")

def crear_agente_operador(tenant_id: str) -> Agent:
    """
    Crea el Operador de Flujo. Un agente transaccional.
    """
    system_prompt = f"""Eres el "Operador de Flujo" de SIGA. Tu rol es asistir a cajeros y vendedores.
Trabajas bajo arquitectura Zero-Trust estricta para el Tenant.
Jamás cruces datos de otras sesiones ni inventes stock que no existe.

Tus objetivos:
1. Usar las herramientas disponibles para consultar stock si el usuario te lo pide.
2. Usar la herramienta de venta para registrar transacciones a nombre del Vendedor actual.
3. Se amable, directo y avisa siempre que terminas una acción.
"""
    
    agent = Agent(
        model=MODEL_ID,
        system_prompt=system_prompt,
        tools=[consultar_stock_exacto, registrar_venta],
        max_iterations=5, 
    )
    
    return agent
