-- cleanup-residual-data.sql
-- Wipe tenant/test data from previous sessions, preserving only what you intentionally keep.
--
-- ⚠️  DESTRUCTIVE: This deletes rows from auth.users and auth.customers.
--     Review carefully before running.
--
-- Usage:
--   docker exec -i siga-db psql -U auth_user -d siga_auth < scripts/cleanup-residual-data.sql

BEGIN;

-- 1. Delete test users (anything that isn't an explicit keep)
DELETE FROM auth.users
WHERE email NOT IN (
    'hdaguila@gmail.com'   -- platform admin (created via bootstrap-platform-admin.sh)
);

-- 2. Delete customer records that have no users and aren't yours
--    (review the SELECT first; uncomment the DELETE when satisfied)
-- SELECT id, email, company_name FROM auth.customers WHERE email NOT LIKE '%@yourcompany.com';
-- DELETE FROM auth.customers
-- WHERE email NOT IN (
--     'your-real-customer@example.com'
-- );

-- 3. Reset permissions tables if they're polluted (optional — only if you want a clean slate)
-- TRUNCATE auth.user_permissions RESTART IDENTITY CASCADE;
-- TRUNCATE auth.role_permissions RESTART IDENTITY CASCADE;

-- 4. Verify what's left
SELECT 'remaining users:' AS info, count(*) FROM auth.users
UNION ALL
SELECT 'remaining customers:', count(*) FROM auth.customers
UNION ALL
SELECT 'remaining platform_admins:', count(*) FROM auth.platform_admins;

COMMIT;
