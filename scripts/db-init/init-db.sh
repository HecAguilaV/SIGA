#!/bin/bash
set -e

# ==============================================================================
# SIGA - Database Initialization Script (Revised)
# ==============================================================================
# This script creates independent databases, users, and SCHEMAS for each service.
# CRITICAL: Schema names MUST match @Table(schema = "...") in Kotlin code.
# ==============================================================================

# Function to initialize a database, user, and schema (DDL handled by Flyway)
init_service_db() {
    local db_name=$1
    local user_name=$2
    local user_pass=$3
    local schema_name=$4
    # DDL SQL file path: /docker-entrypoint-initdb.d/sql/${schema_name}_v1_init.sql
    # Not executed here anymore — Flyway migrations own all DDL.

    echo "  - Creating DB: $db_name, User: $user_name, Schema: $schema_name"
    
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
        CREATE USER $user_name WITH PASSWORD '$user_pass';
        CREATE DATABASE $db_name OWNER $user_name;
        GRANT ALL PRIVILEGES ON DATABASE $db_name TO $user_name;
EOSQL

    # Initialize Schema (Flyway manages DDL now - see V1 migrations)
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db_name" <<-EOSQL
        CREATE SCHEMA IF NOT EXISTS $schema_name AUTHORIZATION $user_name;
        ALTER USER $user_name SET search_path TO $schema_name, public;
EOSQL

    # NOTA: La ejecución de DDL (CREATE TABLE, etc.) ya no se hace aquí.
    # Flyway gestiona todo el esquema a través de las migraciones V1__*.sql.
    #
    # SQL file path kept for reference only (Flyway handles DDL now):
    # local sql_file="/docker-entrypoint-initdb.d/sql/${schema_name}_v1_init.sql"
    # if [ -f "$sql_file" ]; then ...
}

# 1. Auth Service
init_service_db "siga_auth" "auth_user" "auth_pass_2026" "auth"

# 2. Billing Service (SaaS)
init_service_db "siga_billing" "billing_user" "billing_pass_2026" "billing"

# 3. Inventory Service
init_service_db "siga_inventory" "inventory_user" "inventory_pass_2026" "inventory"

# 4. Sales Service (POS)
init_service_db "siga_sales" "sales_user" "sales_pass_2026" "sales"

# 5. Agent Service (AI)
init_service_db "siga_agent" "agent_user" "agent_pass_2026" "agent"

# 6. Notification Service (Async Email)
init_service_db "siga_notification" "notification_user" "notification_pass_2026" "notification"

echo "✅ SIGA Infrastructure Synchronized with Backup Scripts!"
