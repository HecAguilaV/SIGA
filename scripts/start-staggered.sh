#!/bin/bash
set -e

# ==============================================================================
# SIGA - Staggered Startup Script
# ==============================================================================
# Levanta los servicios de a uno con delay para no colapsar el host.
# Orden respeta dependencias: infra → core → gateway → agent
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$(dirname "$SCRIPT_DIR")"

echo "=============================================="
echo "  SIGA - Staggered Startup"
echo "  $(date '+%Y-%m-%d %H:%M:%S')"
echo "=============================================="

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

wait_for_container() {
    local service=$1
    local timeout=${2:-60}
    local elapsed=0
    echo -n "  Esperando que $service esté healthy..."
    while [ $elapsed -lt $timeout ]; do
        local status=$(docker inspect --format='{{.State.Health.Status}}' "$service" 2>/dev/null || echo "starting")
        if [ "$status" = "healthy" ]; then
            echo -e " ${GREEN}healthy${NC}"
            return 0
        fi
        sleep 3
        elapsed=$((elapsed + 3))
        echo -n "."
    done
    echo -e " ${YELLOW}timeout (sigue en segundo plano)${NC}"
    return 0
}

wait_for_eureka() {
    echo -n "  Esperando que Eureka registre servicios..."
    local timeout=90
    local elapsed=0
    while [ $elapsed -lt $timeout ]; do
        if curl -sf http://localhost:8761/eureka/apps > /dev/null 2>&1; then
            echo -e " ${GREEN}disponible${NC}"
            return 0
        fi
        sleep 5
        elapsed=$((elapsed + 5))
        echo -n "."
    done
    echo -e " ${YELLOW}timeout${NC}"
}

start_service() {
    local service=$1
    local profile_flag=$2

    echo ""
    echo -e "${GREEN}[$((++count))] Levantando: $service${NC}"

    if [ "$profile_flag" = "agent" ]; then
        docker compose -f "$COMPOSE_DIR/docker-compose.yml" --profile agent up -d "$service"
    else
        docker compose -f "$COMPOSE_DIR/docker-compose.yml" up -d "$service"
    fi

    # Esperar que el contenedor esté levantado
    sleep 5

    echo -e "  ${GREEN}✓${NC} $service iniciado"

    # Delay entre servicios (excepto el último)
    if [ -n "$3" ] && [ "$3" != "last" ]; then
        local delay=${3:-60}
        echo -e "  ${YELLOW}⏳ Esperando $delay segundos antes del próximo...${NC}"
        sleep "$delay"
    fi
}

count=0

echo ""
echo "══════════════════════════════════════════════"
echo "  FASE 0: Infraestructura (DB, Eureka, Kafka)"
echo "══════════════════════════════════════════════"

# 1. Base de datos (PostgreSQL + PGVector)
start_service "siga-db" "" "60"

# 2. Service Registry (Eureka)
start_service "siga-eureka" "" "45"

# 3. Kafka (event streaming)
start_service "siga-kafka" "" "30"

# 4. UIs de admin (pgAdmin + Kafka UI - en paralelo)
echo ""
echo -e "${GREEN}[$((++count))] Levantando: pgadmin + siga-kafka-ui${NC}"
docker compose -f "$COMPOSE_DIR/docker-compose.yml" up -d pgadmin siga-kafka-ui
sleep 30

echo ""
echo "══════════════════════════════════════════════"
echo "  FASE 1: Microservicios Core"
echo "══════════════════════════════════════════════"

# 5. Auth (primero para probar login)
start_service "siga-auth" "" "45"

# 6. Gateway (después de auth para que pueda enrutar)
start_service "siga-gateway" "" "45"

# 7. Inventory
start_service "siga-inventory" "" "45"

# 8. Sales
start_service "siga-sales" "" "45"

# 9. Billing
start_service "siga-billing" "" "60"

echo ""
echo "══════════════════════════════════════════════"
echo "  FASE 2: AI Agent (requiere Gemini API Key)"
echo "══════════════════════════════════════════════"

# 10. AI Agent (último de los servicios core, con perfil agent)
start_service "siga-agent" "agent" "30"

echo ""
echo "══════════════════════════════════════════════"
echo "  FASE 3: Ops & Observability"
echo "══════════════════════════════════════════════"

# 11. ContainerFlow (visualizador de arquitectura Docker)
start_service "siga-ops" "" "last"

echo ""
echo "=============================================="
echo -e "  ${GREEN}✅ TODOS LOS SERVICIOS LEVANTADOS${NC}"
echo "  $(date '+%Y-%m-%d %H:%M:%S')"
echo "=============================================="
echo ""
echo "  Servicios:"
echo "  • Eureka:     http://localhost:8761"
echo "  • Gateway:    http://localhost:8080"
echo "  • Auth:       http://localhost:8081"
echo "  • Inventory:  http://localhost:8082"
echo "  • Sales:      http://localhost:8083"
echo "  • Billing:    http://localhost:8084"
echo "  • Agent:      http://localhost:8000"
echo "  • pgAdmin:    http://localhost:5050"
echo "  • Kafka UI:   http://localhost:8085"
echo "  • Ops Panel:  http://localhost:9470"
echo ""
echo "  Para ver logs: docker compose logs -f <service>"
echo "  Para parar:    docker compose down"
echo ""
