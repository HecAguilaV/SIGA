-- SIGA - Inventory Core Features (Flyway V2)
-- Extends inventory schema for: SKU, product search, stock reconciliation, warehouse transfers
-- 2026-05-18

-- Enable required extensions (idempotent)
CREATE EXTENSION IF NOT EXISTS "unaccent";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Wrapper function for unaccent to use in indexes (immutable for index safety)
CREATE OR REPLACE FUNCTION f_unaccent(text)
    RETURNS text
    LANGUAGE sql
    IMMUTABLE
    PARALLEL SAFE
AS $$
    SELECT public.unaccent('public.unaccent', $1)
$$;

-- ============================================================================
-- Products: add SKU and unit_type columns
-- ============================================================================
ALTER TABLE inventory.products
    ADD COLUMN sku VARCHAR(50),
    ADD COLUMN unit_type VARCHAR(20);

-- ============================================================================
-- SKU sequences: per-prefix counter for auto-SKU generation
-- ============================================================================
CREATE TABLE inventory.sku_sequences (
    prefix VARCHAR(10) PRIMARY KEY,
    current_value BIGINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL
);

-- ============================================================================
-- Stock: add last_movement_at timestamp
-- ============================================================================
ALTER TABLE inventory.stock
    ADD COLUMN last_movement_at TIMESTAMP;

-- ============================================================================
-- Movements: add correlation_id and destination_store_id for transfers
-- ============================================================================
ALTER TABLE inventory.movements
    ADD COLUMN correlation_id UUID,
    ADD COLUMN destination_store_id UUID;

CREATE INDEX idx_movements_filters
    ON inventory.movements(store_id, type, created_at);

-- ============================================================================
-- Search index: GIN trigram index for unaccented fuzzy product name search
-- ============================================================================
CREATE INDEX idx_products_name_unaccent
    ON inventory.products
    USING gin (f_unaccent(name) gin_trgm_ops);
