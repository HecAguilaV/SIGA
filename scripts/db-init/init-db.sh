#!/bin/bash
set -e

# ==============================================================================
# SIGA - Database Initialization Script
# ==============================================================================
# This script runs automatically on the first start of the PostgreSQL container.
# It creates independent databases and users for each microservice.
# ==============================================================================

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- 1. Create Databases
    CREATE DATABASE siga_auth;
    CREATE DATABASE siga_inventory;
    CREATE DATABASE siga_sales;
    CREATE DATABASE siga_billing;
    CREATE DATABASE siga_agent;

    -- 2. Create Dedicated Users
    CREATE USER auth_user WITH PASSWORD 'auth_pass_2026';
    CREATE USER inventory_user WITH PASSWORD 'inventory_pass_2026';
    CREATE USER sales_user WITH PASSWORD 'sales_pass_2026';
    CREATE USER billing_user WITH PASSWORD 'billing_pass_2026';
    CREATE USER agent_user WITH PASSWORD 'agent_pass_2026';

    -- 3. Grant Privileges
    GRANT ALL PRIVILEGES ON DATABASE siga_auth TO auth_user;
    GRANT ALL PRIVILEGES ON DATABASE siga_inventory TO inventory_user;
    GRANT ALL PRIVILEGES ON DATABASE siga_sales TO sales_user;
    GRANT ALL PRIVILEGES ON DATABASE siga_billing TO billing_user;
    GRANT ALL PRIVILEGES ON DATABASE siga_agent TO agent_user;
EOSQL

# 4. Create Schemas inside each database
# We connect to each DB and create the required schema
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "siga_auth" <<-EOSQL
    CREATE SCHEMA IF NOT EXISTS siga_saas AUTHORIZATION auth_user;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "siga_inventory" <<-EOSQL
    CREATE SCHEMA IF NOT EXISTS inventory AUTHORIZATION inventory_user;
EOSQL

echo "✅ SIGA Databases and Users created successfully!"
