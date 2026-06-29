#!/usr/bin/env bash
# bootstrap-platform-admin.sh
# Crea el primer platform admin (godadmin) en la DB local de auth.
#
# Uso:
#   ./scripts/bootstrap-platform-admin.sh <email> <password> [display_name]
#
# Ejemplo:
#   ./scripts/bootstrap-platform-admin.sh hdaguila@gmail.com "MiPass123!"
#
# Requiere:
#   - docker compose corriendo (siga-db)
#   - python3 con bcrypt (`pip install bcrypt`)
#   - psql client (o usar docker exec)

set -euo pipefail

EMAIL="${1:-}"
PASSWORD="${2:-}"
DISPLAY_NAME="${3:-Platform Owner}"

if [[ -z "$EMAIL" || -z "$PASSWORD" ]]; then
  echo "Uso: $0 <email> <password> [display_name]" >&2
  exit 1
fi

# Generate BCrypt hash (cost 10, matches Spring Security default)
HASH=$(python3 -c "
import bcrypt, sys
pw = sys.argv[1].encode('utf-8')
print(bcrypt.hashpw(pw, bcrypt.gensalt(rounds=10)).decode('utf-8'))
" "$PASSWORD")

echo "Generated BCrypt hash for $EMAIL"
echo "Hash: $HASH"

# Insert into DB via docker exec
docker exec -i siga-db psql -U auth_user -d siga_auth <<EOF
INSERT INTO auth.platform_admins (id, email, password_hash, display_name, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '$EMAIL',
    '$HASH',
    '$DISPLAY_NAME',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO UPDATE SET
    password_hash = EXCLUDED.password_hash,
    is_active = true,
    updated_at = NOW();
EOF

echo ""
echo "Platform admin '$EMAIL' creado/actualizado correctamente."
echo "Ahora podés loguearte con: POST /api/v1/auth/login"
echo "  { \"email\": \"$EMAIL\", \"password\": \"<tu_password>\" }"
