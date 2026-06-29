-- SIGA - Platform Admins (Flyway V6)
-- Platform-level administrators (SaaS owners). NOT tenant-scoped.
-- Distinct from auth.users: no customer_id, no UserRole, own permissions set.
CREATE TABLE IF NOT EXISTS auth.platform_admins (
    id              UUID         PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(150) NOT NULL,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_platform_admins_email ON auth.platform_admins(email);
CREATE INDEX IF NOT EXISTS idx_platform_admins_active ON auth.platform_admins(is_active);
