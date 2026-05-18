#!/bin/bash
set -e

# ==============================================================================
# SIGA - Load Demo Seed Data
# ==============================================================================
# Ejecuta los archivos seed contra cada base de datos del stack.
# Orden: auth → inventory → sales → billing
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$(dirname "$SCRIPT_DIR")"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "=============================================="
echo "  SIGA - Load Demo Seed Data"
echo "  $(date '+%Y-%m-%d %H:%M:%S')"
echo "=============================================="

# Configuración de bases de datos
DB_HOST="localhost"
DB_PORT="${DB_PORT:-5432}"

declare -A DB_CONFIG
DB_CONFIG[auth]="siga_auth:auth_user:auth_pass_2026"
DB_CONFIG[inventory]="siga_inventory:inventory_user:inventory_pass_2026"
DB_CONFIG[sales]="siga_sales:sales_user:sales_pass_2026"
DB_CONFIG[billing]="siga_billing:billing_user:billing_pass_2026"

run_seed() {
    local service=$1
    local seed_file="$SCRIPT_DIR/${2:-01_seed_${service}.sql}"
    local config="${DB_CONFIG[$service]}"
    local db_name=$(echo "$config" | cut -d: -f1)
    local db_user=$(echo "$config" | cut -d: -f2)
    local db_pass=$(echo "$config" | cut -d: -f3)

    echo ""
    echo "──────────────────────────────────────────────"
    echo -e "${YELLOW}➜  $service → $db_name${NC}"
    echo "──────────────────────────────────────────────"

    if [ ! -f "$seed_file" ]; then
        echo -e "  ${RED}✗ Archivo no encontrado: $seed_file${NC}"
        return 1
    fi

    PGPASSWORD="$db_pass" psql -h "$DB_HOST" -p "$DB_PORT" -U "$db_user" -d "$db_name" \
        -f "$seed_file" -v ON_ERROR_STOP=1 2>&1 | sed 's/^/  /'

    local exit_code=${PIPESTATUS[0]}
    if [ $exit_code -eq 0 ]; then
        echo -e "  ${GREEN}✅ $service: seed cargado exitosamente${NC}"
    else
        echo -e "  ${RED}❌ $service: error al cargar seed (código: $exit_code)${NC}"
        return $exit_code
    fi
}

# Verificar conexión a PostgreSQL
echo ""
echo "⏳ Verificando conexión a PostgreSQL..."
if ! PGPASSWORD="auth_pass_2026" psql -h "$DB_HOST" -p "$DB_PORT" -U "auth_user" -d "siga_auth" -c "SELECT 1" > /dev/null 2>&1; then
    echo -e "  ${RED}✗ No se puede conectar a PostgreSQL en $DB_HOST:$DB_PORT${NC}"
    echo "  Asegúrate de que siga-db esté corriendo (./scripts/start-staggered.sh)"
    exit 1
fi
echo -e "  ${GREEN}✅ Conexión establecida${NC}"

# Orden de carga
run_seed "auth"     "01_seed_auth.sql"
AUTH_OK=$?

run_seed "inventory" "02_seed_inventory.sql"
INV_OK=$?

run_seed "sales"    "03_seed_sales.sql"
SALES_OK=$?

run_seed "billing"  "04_seed_billing.sql"
BILL_OK=$?

echo ""
echo "=============================================="
echo "  RESUMEN"
echo "=============================================="
echo ""
echo -e "  Auth:     $([ $AUTH_OK -eq 0 ] && echo "${GREEN}✅ OK${NC}" || echo "${RED}❌ FAIL${NC}")"
echo -e "  Inventory: $([ $INV_OK -eq 0 ] && echo "${GREEN}✅ OK${NC}" || echo "${RED}❌ FAIL${NC}")"
echo -e "  Sales:    $([ $SALES_OK -eq 0 ] && echo "${GREEN}✅ OK${NC}" || echo "${RED}❌ FAIL${NC}")"
echo -e "  Billing:  $([ $BILL_OK -eq 0 ] && echo "${GREEN}✅ OK${NC}" || echo "${RED}❌ FAIL${NC}")"
echo ""
echo "  Usuarios demo (contraseña: demo123):"
echo "  • elizabeth@casinoeliz.cl   → Dueña (todos los permisos)"
echo "  • hector@siga.cl            → Admin inventario + kioskos"
echo "  • yesenia@casinoeliz.cl     → Cajera (ventas)"
echo "  • luis@casinoeliz.cl        → Repartidor (entregas)"
echo "  • antonia@casinoeliz.cl     → Bodeguera"
echo ""
echo "  Probar login:"
echo "  curl -X POST http://localhost:8080/api/auth/login \\"
echo "    -H 'Content-Type: application/json' \\"
echo "    -d '{\"email\":\"elizabeth@casinoeliz.cl\",\"password\":\"demo123\"}'"
echo ""
