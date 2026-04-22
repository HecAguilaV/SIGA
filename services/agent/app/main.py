import os
from fastapi import FastAPI, Depends, Header, HTTPException
import py_eureka_client.eureka_client as eureka_client
from contextlib import asynccontextmanager
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Eureka Configuration
EUREKA_SERVER = os.getenv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://localhost:8761/eureka")
APP_PORT = int(os.getenv("PORT", 8000))

from app.core.database import db
from app.bots.analyst import create_analyst_agent
from app.bots.operator import create_operator_agent

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Register with Eureka on startup
    logger.info(f"Registering with Eureka at {EUREKA_SERVER}")
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER,
        app_name="siga-agent",
        instance_port=APP_PORT
    )
    # Initialize PGVector Database
    await db.connect()
    
    yield
    
    # Clean Disconnect
    await db.disconnect()

app = FastAPI(title="SIGA AI Agent", lifespan=lifespan)

@app.get("/health")
def health_check():
    return {"status": "UP", "service": "siga-agent"}

# Simulated dependency for JWT pass-through (delegated validation)
def verify_token(x_tenant_id: str = Header(None, alias="X-Tenant-Id")):
    if not x_tenant_id:
        raise HTTPException(status_code=401, detail="X-Tenant-Id header missing")
    return x_tenant_id

@app.post("/api/agent/chat")
async def chat(payload: dict, tenant_id: str = Depends(verify_token)):
    prompt = payload.get("prompt")
    bot_type = payload.get("bot_type", "analyst") # Default: Analyst
    
    if not prompt:
        raise HTTPException(status_code=400, detail="Prompt is required")
        
    # Model-First Agent Initialization (Analyst or Operator)
    # Parametrically injecting tenant_id (Zero-Trust Security Payload)
    if bot_type == "operator":
        agent = create_operator_agent(tenant_id)
        bot_name = "Workflow Operator"
    else:
        agent = create_analyst_agent(tenant_id)
        bot_name = "Business Analyst"
    
    try:
        logger.info(f"Chat request for tenant {tenant_id} using {bot_type}")
        # In a real scenario, we would call: response = await agent.run(prompt)
        reply = f"Validated data for tenant {tenant_id} using {bot_name}. (AI Orchestration ready in English)"
        
        return {
            "reply": reply,
            "status": "success",
            "bot_type": bot_name
        }
    except Exception as e:
        logger.error(f"LLM Orchestration Error: {str(e)}")
        raise HTTPException(status_code=503, detail=f"LLM Orchestration Error: {str(e)}")
