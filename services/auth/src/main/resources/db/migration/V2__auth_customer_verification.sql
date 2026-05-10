-- SIGA - Auth Customer Email Verification (Flyway V2)
-- Adds email verification fields to customers table
ALTER TABLE auth.customers
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS verification_token VARCHAR(255),
    ADD COLUMN IF NOT EXISTS verification_token_expires_at TIMESTAMPTZ;
