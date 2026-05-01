#!/bin/bash
set -e

# ==============================================================================
# SIGA - Database Initialization Script (Revised)
# ==============================================================================
# This script creates independent databases, users, and SCHEMAS for each service.
# CRITICAL: Schema names MUST match @Table(schema = "...") in Kotlin code.
# ==============================================================================

# Function to initialize a database, user, and schema, and execute its init SQL
init_service_db() {
    local db_name=$1
    local user_name=$2
    local user_pass=$3
    local schema_name=$4
    local sql_file="/docker-entrypoint-initdb.d/sql/${schema_name}_v1_init.sql"

    echo "  - Creating DB: $db_name, User: $user_name, Schema: $schema_name"
    
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
        CREATE USER $user_name WITH PASSWORD '$user_pass';
        CREATE DATABASE $db_name OWNER $user_name;
        GRANT ALL PRIVILEGES ON DATABASE $db_name TO $user_name;
EOSQL

    # Initialize Schema and Tables
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db_name" <<-EOSQL
        CREATE SCHEMA IF NOT EXISTS $schema_name AUTHORIZATION $user_name;
        ALTER USER $user_name SET search_path TO $schema_name, public;
EOSQL

    # Execute SQL Init File if it exists
    if [ -f "$sql_file" ]; then
        echo "    * Executing init script: $sql_file"
        psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db_name" -f "$sql_file"
    else
        echo "    ! No init script found at: $sql_file (skipping table creation)"
    fi
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

echo "✅ SIGA Infrastructure Synchronized with Backup Scripts!"
