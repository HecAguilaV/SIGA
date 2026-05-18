-- =============================================================================
-- SIGA - Demo Seed Data: Billing Service (siga_billing)
-- =============================================================================

-- Planes
INSERT INTO billing.plans (id, name, description, store_limit, user_limit, monthly_price, yearly_price, sort_order) VALUES
    ('f0000001-0001-4001-8001-000000000001', 'Plan Base',
     'Análisis de inventario con IA. Ideal para negocios pequeños que quieren visibilidad.',
     3, 5, 19990, 199900, 1),
    ('f0000001-0001-4001-8001-000000000002', 'Plan Avanzado',
     'IA operativa con CRUD completo. El agente ejecuta acciones sobre tu inventario.',
     10, 20, 49990, 499900, 2)
ON CONFLICT (name) DO NOTHING;

-- Cliente en billing (corresponde al tenant de Elizabeth)
INSERT INTO billing.customers (id, email, password_hash, name, last_name, company_name, role, plan_id, is_active)
VALUES (
    '3e24680e-1e54-5868-a2f8-23dc77cdd740',
    'elizabeth@casinoeliz.cl',
    '$2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS',
    'Elizabeth', 'González Muñoz',
    'Casino Elizabeth Ltda.',
    'customer',
    'f0000001-0001-4001-8001-000000000002',
    true
) ON CONFLICT (email) DO NOTHING;

-- Suscripción activa (Plan Avanzado, mensual)
INSERT INTO billing.subscriptions (customer_id, plan_id, billing_period, status, starts_at)
VALUES (
    '3e24680e-1e54-5868-a2f8-23dc77cdd740',
    'f0000001-0001-4001-8001-000000000002',
    'MONTHLY', 'ACTIVE',
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;
