-- SIGA - Auth User Customer ID (Flyway V3)
-- Adds tenant-scoped customer_id to users table
ALTER TABLE auth.users
    ADD COLUMN IF NOT EXISTS customer_id INTEGER;
