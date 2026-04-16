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

@app.post("/api/agente/chat")
def chat(payload: dict, tenant_id: str = Depends(verify_token)):
    # Aquí llamaremos a Strands posteriormente
    return {
        "reply": f"Recibí '{payload.get('prompt')}'. Validando datos para tenant {tenant_id}...",
        "status": "success"
    }
