-- SIGA - AI Agent Service Initialization (Flyway V1)
-- Fragmented from monolithic script - 2026-04-30

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "vector"; -- Essential for semantic search

SET search_path TO agent, public;

CREATE TABLE IF NOT EXISTS conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL, -- Logical reference to Auth Tenant
    user_id UUID NOT NULL, -- Logical reference to Auth User
    session_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    context JSONB,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    embedding VECTOR(1536), -- Vector for semantic search
    source VARCHAR(100),
    tenant_id UUID NOT NULL,
    indexed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS intent_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    query_text TEXT NOT NULL,
    detected_intent VARCHAR(50),
    confidence_score NUMERIC(3, 2),
    raw_response JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS intent_permissions (
    id SERIAL PRIMARY KEY,
    plan_name VARCHAR(20) NOT NULL,
    intent VARCHAR(50) NOT NULL,
    is_allowed BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_intent_plan UNIQUE (plan_name, intent)
);

CREATE TABLE IF NOT EXISTS pending_actions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    intent VARCHAR(50) NOT NULL,
    action_data JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMPTZ
);
