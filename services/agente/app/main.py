import os
from fastapi import FastAPI, Depends, Header, HTTPException
import py_eureka_client.eureka_client as eureka_client
from contextlib import asynccontextmanager

# Configuración de Eureka
EUREKA_SERVER = os.getenv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://localhost:8761/eureka")
APP_PORT = int(os.getenv("PORT", 8000))

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Registrar en Eureka al inicio
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER,
        app_name="siga-agente",
        instance_port=APP_PORT
    )
    yield
    # No es estrictamente necesario, py-eureka-client se encarga de parar graceful pero podemos explicitarlo

app = FastAPI(title="SIGA Agente de IA", lifespan=lifespan)

@app.get("/health")
def health_check():
    return {"status": "UP", "service": "siga-agente"}

# Dependencia simulada para JWT pass-through (validación delegada)
def verify_token(x_tenant_id: str = Header(None, alias="X-Tenant-Id")):
    if not x_tenant_id:
        raise HTTPException(status_code=401, detail="X-Tenant-Id header missing")
    return x_tenant_id

from app.bots.analista import crear_agente_analista
from app.bots.operador import crear_agente_operador

@app.post("/api/agente/chat")
def chat(payload: dict, tenant_id: str = Depends(verify_token)):
    prompt = payload.get("prompt")
    bot_type = payload.get("bot_type", "analista") # Por defecto, Analista
    
    if not prompt:
        raise HTTPException(status_code=400, detail="Prompt is required")
        
    # Inicialización del agente Model-First (Analista u Operador)
    # inyectándole paramétricamente el tenant_id (Zero-Trust Security Payload)
    if bot_type == "operador":
        agent = crear_agente_operador(tenant_id)
        bot_name = "Operador de Flujo"
    else:
        agent = crear_agente_analista(tenant_id)
        bot_name = "Analista de Negocio"
    
    try:
        reply = f"Validando datos para tenant {tenant_id} usando el {bot_name}..."
        
        return {
            "reply": reply,
            "status": "success",
            "bot_type": bot_name
        }
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"LLM Orchestration Error: {str(e)}")

