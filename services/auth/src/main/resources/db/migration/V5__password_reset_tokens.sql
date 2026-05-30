-- SIGA - Auth Password Reset Tokens (Flyway V5)
-- Creates the password_reset_tokens table for scoped reset tokens
-- with 15-minute expiry and one-time use enforcement.

CREATE TABLE IF NOT EXISTS auth.password_reset_tokens (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON auth.password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_email ON auth.password_reset_tokens(email);
