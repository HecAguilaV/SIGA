import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health_check_endpoint():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "UP", "service": "siga-agente"}

def test_chat_requires_auth_header():
    # Asumiremos la entrada del JWT pass-through vía Header X-Tenant-Id u Authorization
    response = client.post("/api/agente/chat", json={"prompt": "Hola"})
    assert response.status_code == 401

def test_chat_with_valid_tenant_id_header():
    headers = {"X-Tenant-Id": "100"}
    response = client.post("/api/agente/chat", json={"prompt": "Dime kpis", "bot_type": "analista"}, headers=headers)
    assert response.status_code == 200
    assert "Analista de Negocio" in response.json()["reply"]

def test_chat_with_operador_bot_type():
    headers = {"X-Tenant-Id": "200"}
    response = client.post("/api/agente/chat", json={"prompt": "Vende esto", "bot_type": "operador"}, headers=headers)
    assert response.status_code == 200
    assert "Operador de Flujo" in response.json()["reply"]

