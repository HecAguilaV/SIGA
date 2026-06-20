#!/bin/bash
# =============================================================================
# SIGA - Setup completo (ejecutar después de abrir el lab)
# =============================================================================
# Este script registra godadmin, crea la empresa Lito y carga datos demo.
# Se conecta a RDS usando las credenciales del secret en Kubernetes.
#
# USO:
#   ./scripts/setup-full.sh
# =============================================================================

set -e

RDS_HOST="siga-production-postgres.cm75k1vmfnks.us-east-1.rds.amazonaws.com"
RDS_PASS=$(kubectl get secret db-secret -n siga -o jsonpath='{.data.DB_PASSWORD}' | base64 -d 2>/dev/null)

if [ -z "$RDS_PASS" ]; then
    echo "❌ No se pudo obtener la contraseña de RDS. ¿Está el lab abierto?"
    exit 1
fi

echo "=============================================="
echo "  SIGA - Setup Completo"
echo "=============================================="

# ---------------------------------------------------------------------------
# Paso 1: Registrar godadmin via API
# ---------------------------------------------------------------------------
echo ""
echo "📌 Paso 1: Registrar God Admin..."
kubectl exec -n siga deployment/siga-auth -- sh -c "
  wget -q -O- --post-data='{\"email\":\"godadmin@siga.cl\",\"password\":\"KikeThron4466.\",\"name\":\"God\",\"lastName\":\"Admin\",\"companyName\":\"SIGA Platform Admin\"}' --header='Content-Type: application/json' http://localhost:8081/api/v1/auth/register" 2>&1

# ---------------------------------------------------------------------------
# Paso 2: Registrar Yasna (Lito) via API
# ---------------------------------------------------------------------------
echo ""
echo "📌 Paso 2: Registrar Yasna Aguila (Lito)..."
kubectl exec -n siga deployment/siga-auth -- sh -c "
  TOKEN=\$(wget -q -O- --post-data='{\"email\":\"yasna@lito.cl\",\"password\":\"LitoLibreria2026!\",\"name\":\"Yasna\",\"lastName\":\"Aguila\",\"companyName\":\"Lito Libreria y Bazar\"}' --header='Content-Type: application/json' http://localhost:8081/api/v1/auth/register 2>&1)
  echo \"\$TOKEN\"
" 2>&1

# ---------------------------------------------------------------------------
# Paso 3: Verificar ambos customers y obtener tokens de verificación
# ---------------------------------------------------------------------------
echo ""
echo "📌 Paso 3: Buscar tokens de verificación en logs..."
VERIFY_GOD=$(kubectl logs -n siga deployment/siga-auth --tail=100 2>&1 | grep "godadmin@siga.cl" | grep -oP 'token=\K[a-f0-9-]+' | tail -1)
VERIFY_YASNA=$(kubectl logs -n siga deployment/siga-auth --tail=100 2>&1 | grep "yasna@lito.cl" | grep -oP 'token=\K[a-f0-9-]+' | tail -1)

if [ -n "$VERIFY_GOD" ]; then
  echo "Verificando godadmin..."
  kubectl exec -n siga deployment/siga-auth -- sh -c "wget -q -O- 'http://localhost:8081/api/v1/auth/verify?token=$VERIFY_GOD'" 2>&1
fi

if [ -n "$VERIFY_YASNA" ]; then
  echo "Verificando Yasna..."
  kubectl exec -n siga deployment/siga-auth -- sh -c "wget -q -O- 'http://localhost:8081/api/v1/auth/verify?token=$VERIFY_YASNA'" 2>&1
fi

# ---------------------------------------------------------------------------
# Paso 4: Crear schemas (por si no existen)
# ---------------------------------------------------------------------------
echo ""
echo "📌 Paso 4: Crear schemas en todas las DBs..."
for db in siga_auth siga_inventory siga_sales siga_billing siga_notification; do
  kubectl run psql-setup --rm -i --restart=Never --image=postgres:16-alpine \
    --env="PGPASSWORD=$RDS_PASS" -- \
    psql -h "$RDS_HOST" -U siga_admin -d "$db" \
    -c "CREATE SCHEMA IF NOT EXISTS auth;
        CREATE SCHEMA IF NOT EXISTS inventory;
        CREATE SCHEMA IF NOT EXISTS sales;
        CREATE SCHEMA IF NOT EXISTS billing;
        CREATE SCHEMA IF NOT EXISTS notification;
        GRANT ALL ON SCHEMA auth,inventory,sales,billing,notification TO siga_admin;" 2>&1
done

# ---------------------------------------------------------------------------
# Paso 5: Recrear pods de servicios para que Hibernate cree tablas
# ---------------------------------------------------------------------------
echo ""
echo "📌 Paso 5: Reiniciar servicios para crear tablas..."
kubectl rollout restart deployment -n siga siga-auth siga-billing siga-inventory siga-sales siga-notification 2>&1
sleep 30
kubectl wait --for=condition=ready pod -n siga -l app=siga-auth --timeout=60s 2>&1

# ---------------------------------------------------------------------------
# Paso 6: Cargar seed data de Lito
# ---------------------------------------------------------------------------
echo ""
echo "📌 Paso 6: Cargar seed data de Lito en RDS..."

# Insertar cajeros en auth
kubectl run psql-seed --rm -i --restart=Never --image=postgres:16-alpine \
  --env="PGPASSWORD=$RDS_PASS" -- \
  psql -h "$RDS_HOST" -U siga_admin -d siga_auth -v ON_ERROR_STOP=1 -c "
INSERT INTO auth.users (id, email, password_hash, first_name, last_name, role, customer_id, is_active, created_at, updated_at)
VALUES
    ('a1000000-0001-4000-8000-000000000001', 'cajero1@lito.cl', '\$2b\$10\$kZ05/QmRfdqbt/I3sK3FOuDU4lky7/yv2ZRftCJ6HbsZN63RfDTEq', 'Carlos', 'Muñoz', 'CASHIER', 2, true, NOW(), NOW()),
    ('a1000000-0001-4000-8000-000000000002', 'cajero2@lito.cl', '\$2b\$10\$kZ05/QmRfdqbt/I3sK3FOuDU4lky7/yv2ZRftCJ6HbsZN63RfDTEq', 'María', 'Soto', 'CASHIER', 2, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
" 2>&1

# Insertar tiendas, categorías, productos y stock
kubectl run psql-inv --rm -i --restart=Never --image=postgres:16-alpine \
  --env="PGPASSWORD=$RDS_PASS" -- \
  psql -h "$RDS_HOST" -U siga_admin -d siga_inventory -v ON_ERROR_STOP=1 -c "
INSERT INTO inventory.stores (id, name, address, city, is_active, created_at)
VALUES
    ('b1000000-0001-4000-8000-000000000001', 'Lito Librería Centro', 'Av. Providencia 1234, Local 5', 'Santiago', true, NOW()),
    ('b1000000-0001-4000-8000-000000000002', 'Lito Bazar Norte', 'Av. Independencia 5678', 'Santiago', true, NOW())
ON CONFLICT (id) DO NOTHING;
INSERT INTO inventory.categories (id, name, description, is_active, created_at)
VALUES
    ('c1000000-0001-4000-8000-000000000001', 'Libros', 'Libros y textos', true, NOW()),
    ('c1000000-0001-4000-8000-000000000002', 'Cuadernos', 'Cuadernos y blocks', true, NOW()),
    ('c1000000-0001-4000-8000-000000000003', 'Escritura', 'Lápices, bolígrafos', true, NOW()),
    ('c1000000-0001-4000-8000-000000000004', 'Escolar', 'Útiles escolares', true, NOW()),
    ('c1000000-0001-4000-8000-000000000005', 'Bazar', 'Decoración y bazar', true, NOW()),
    ('c1000000-0001-4000-8000-000000000006', 'Oficina', 'Insumos oficina', true, NOW())
ON CONFLICT (name) DO NOTHING;
" 2>&1

# Productos + Stock (ejecutar desde el archivo seed-lito.sql)
kubectl run psql-prod --rm -i --restart=Never --image=postgres:16-alpine \
  --env="PGPASSWORD=$RDS_PASS" -- \
  psql -h "$RDS_HOST" -U siga_admin -d siga_inventory -v ON_ERROR_STOP=1 < scripts/seed/seed-lito.sql 2>&1

# Asignar cajeros a tiendas
kubectl run psql-usr --rm -i --restart=Never --image=postgres:16-alpine \
  --env="PGPASSWORD=$RDS_PASS" -- \
  psql -h "$RDS_HOST" -U siga_admin -d siga_auth -c "
INSERT INTO auth.user_stores (user_id, store_id, assigned_at) VALUES
    ('a1000000-0001-4000-8000-000000000001', 'b1000000-0001-4000-8000-000000000001', NOW()),
    ('a1000000-0001-4000-8000-000000000002', 'b1000000-0001-4000-8000-000000000002', NOW())
ON CONFLICT DO NOTHING;
" 2>&1

# ---------------------------------------------------------------------------
# Paso 7: Verificar estado final
# ---------------------------------------------------------------------------
echo ""
echo "=============================================="
echo "  VERIFICACIÓN FINAL"
echo "=============================================="

echo ""
echo "✅ Login godadmin:"
kubectl exec -n siga deployment/siga-auth -- sh -c "wget -q -O- --post-data='{\"email\":\"godadmin@siga.cl\",\"password\":\"KikeThron4466.\"}' --header='Content-Type: application/json' http://localhost:8081/api/v1/auth/login 2>&1" | head -c 80

echo ""
echo "✅ Login Yasna:"
kubectl exec -n siga deployment/siga-auth -- sh -c "wget -q -O- --post-data='{\"email\":\"yasna@lito.cl\",\"password\":\"LitoLibreria2026!\"}' --header='Content-Type: application/json' http://localhost:8081/api/v1/auth/login 2>&1" | head -c 80

echo ""
echo "✅ Stock en tiendas:"
kubectl run psql-chk --rm -i --restart=Never --image=postgres:16-alpine \
  --env="PGPASSWORD=$RDS_PASS" -- \
  psql -h "$RDS_HOST" -U siga_admin -d siga_inventory -c "
SELECT s.name as tienda, count(p.*) as productos, sum(st.quantity) as stock_total
FROM inventory.stores s
JOIN inventory.stock st ON st.store_id = s.id
JOIN inventory.products p ON p.id = st.product_id
GROUP BY s.name;" 2>&1

echo ""
echo "=============================================="
echo "  ✅ SETUP COMPLETADO"
echo "=============================================="
echo ""
echo "Credenciales:"
echo "  godadmin@siga.cl  / KikeThron4466."
echo "  yasna@lito.cl     / LitoLibreria2026!"
echo "  cajero1@lito.cl   / LitoCajero2026!"
echo "  cajero2@lito.cl   / LitoCajero2026!"
