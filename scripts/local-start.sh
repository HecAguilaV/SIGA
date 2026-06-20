#!/bin/bash
# ==============================================================================
# SIGA - Inicio Local (Docker Compose + Seed Data)
# ==============================================================================
# Uso: ./scripts/local-start.sh
#
# Prende todo: PostgreSQL, Redis, Kafka, Eureka, Gateway, 6 microservicios,
# Dashboard, y carga datos demo de Lito Librería y Bazar.
#
# URLs:
#   Dashboard:  http://localhost:3000
#   Gateway:    http://localhost:8080
#   Eureka:     http://localhost:8761
#   Kafka UI:   http://localhost:8085
#   pgAdmin:    http://localhost:5050
#   ContainerFlow: http://localhost:9470
# ==============================================================================

set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  SIGA - Inicio Local${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

# Paso 1: Verificar .env
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  No hay .env, copiando desde .env.example...${NC}"
    cp .env.example .env
    echo -e "${YELLOW}⚠️  Revisá .env y completá GEMINI_API_KEY y JWT_SECRET${NC}"
    echo ""
    read -p "Presioná Enter cuando hayas configurado .env..."
fi

# Paso 2: Build y levantar servicios
echo -e "${BLUE}[1/4] Construyendo imágenes Docker...${NC}"
docker compose build 2>&1 | tail -5

echo ""
echo -e "${BLUE}[2/4] Levantando servicios...${NC}"
docker compose up -d 2>&1

echo ""
echo -e "${YELLOW}⏳ Esperando que PostgreSQL esté listo...${NC}"
until docker compose exec -T siga-db pg_isready -U siga_admin -d siga_master_db &>/dev/null; do
    sleep 2
done
echo -e "${GREEN}✅ PostgreSQL listo${NC}"

echo -e "${YELLOW}⏳ Esperando que Eureka esté listo...${NC}"
until docker compose exec -T siga-eureka wget -q -O- http://localhost:8761/actuator/health &>/dev/null; do
    sleep 3
done
echo -e "${GREEN}✅ Eureka listo${NC}"

echo -e "${YELLOW}⏳ Esperando que el Gateway esté listo...${NC}"
until curl -s -o /dev/null http://localhost:8080/actuator/health; do
    sleep 3
done
echo -e "${GREEN}✅ Gateway listo${NC}"

echo -e "${YELLOW}⏳ Esperando servicios backend...${NC}"
sleep 10

# Paso 3: Cargar seed data
echo ""
echo -e "${BLUE}[3/4] Cargando datos demo...${NC}"
bash scripts/seed/load-seed.sh 2>&1 | tail -5
echo -e "${GREEN}✅ Seed data cargada${NC}"

# Paso 4: Crear usuario godadmin + Yasna
echo ""
echo -e "${BLUE}[4/4] Creando usuarios demo...${NC}"

# God Admin
curl -s -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"godadmin@siga.cl","password":"KikeThron4466.","name":"God","lastName":"Admin","companyName":"SIGA Platform Admin"}' -o /dev/null

# Yasna
curl -s -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"yasna@lito.cl","password":"LitoLibreria2026!","name":"Yasna","lastName":"Aguila","companyName":"Lito Libreria y Bazar"}' -o /dev/null

# Obtener tokens de verificación
sleep 2
GOD_TOKEN=$(docker compose logs siga-auth 2>&1 | grep "godadmin@siga.cl" | grep -oP 'token=\K[a-f0-9-]+' | tail -1)
YASNA_TOKEN=$(docker compose logs siga-auth 2>&1 | grep "yasna@lito.cl" | grep -oP 'token=\K[a-f0-9-]+' | tail -1)

# Verificar usuarios
if [ -n "$GOD_TOKEN" ]; then curl -s "http://localhost:8080/api/auth/verify?token=$GOD_TOKEN" -o /dev/null; fi
if [ -n "$YASNA_TOKEN" ]; then curl -s "http://localhost:8080/api/auth/verify?token=$YASNA_TOKEN" -o /dev/null; fi

echo ""
echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}  SIGA listo!${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""
echo -e "  Dashboard:  ${BLUE}http://localhost:3000${NC}"
echo -e "  Gateway:    ${BLUE}http://localhost:8080${NC}"
echo -e "  Eureka:     ${BLUE}http://localhost:8761${NC}"
echo ""
echo -e "  Usuarios:"
echo -e "    godadmin@siga.cl  / KikeThron4466."
echo -e "    yasna@lito.cl     / LitoLibreria2026!"
echo ""
echo -e "  Para ver logs:  ${YELLOW}docker compose logs -f${NC}"
echo -e "  Para detener:   ${YELLOW}docker compose down${NC}"
echo ""
