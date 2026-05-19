-- =============================================================================
-- SIGA - Demo Seed Data: Auth Service (siga_auth)
-- =============================================================================
-- Contraseña demo para TODOS los usuarios: demo123
-- Hash BCrypt: $2a$10$DEMO_HASH_PLACEHOLDER_DO_NOT_USE_IN_PROD (demo-only, do not use in production)
-- =============================================================================

-- Cliente / Tenant demo (Elizabeth, la dueña)
INSERT INTO auth.customers (email, password_hash, name, last_name, company_name, is_active, is_on_trial, role)
VALUES (
    'elizabeth@casinoeliz.cl',
    '$2a$10$DEMO_HASH_PLACEHOLDER_DO_NOT_USE_IN_PROD',
    'Elizabeth', 'González Muñoz',
    'Casino Elizabeth Ltda.',
    true, false, 'customer'
) ON CONFLICT (email) DO NOTHING;

-- Permisos del sistema
INSERT INTO auth.permissions (id, code, name, description, category) VALUES
    ('a0000001-0001-4001-8001-000000000001', 'INVENTORY_READ',  'Leer inventario',       'Ver productos y stock', 'inventory'),
    ('a0000001-0001-4001-8001-000000000002', 'INVENTORY_WRITE', 'Escribir inventario',    'Agregar y modificar productos', 'inventory'),
    ('a0000001-0001-4001-8001-000000000003', 'INVENTORY_DELETE','Eliminar inventario',    'Eliminar productos', 'inventory'),
    ('a0000001-0001-4001-8001-000000000004', 'KIOSK_ADMIN',     'Admin kioskos',          'Gestionar kioskos y puntos de venta', 'inventory'),
    ('a0000001-0001-4001-8001-000000000005', 'STOCK_ADJUST',    'Ajustar stock',          'Realizar ajustes y conteos físicos', 'inventory'),
    ('a0000001-0001-4001-8001-000000000006', 'SALES_READ',      'Leer ventas',            'Ver historial de ventas', 'sales'),
    ('a0000001-0001-4001-8001-000000000007', 'SALES_WRITE',     'Registrar ventas',       'Realizar ventas en POS', 'sales'),
    ('a0000001-0001-4001-8001-000000000008', 'DELIVERY_VIEW',   'Ver entregas',           'Consultar rutas y entregas', 'delivery'),
    ('a0000001-0001-4001-8001-000000000009', 'PRODUCT_CREATE',  'Crear productos',        'Dar de alta nuevos productos', 'inventory'),
    ('a0000001-0001-4001-8001-00000000000a', 'PRODUCT_EDIT',    'Editar productos',       'Modificar datos de productos', 'inventory'),
    ('a0000001-0001-4001-8001-00000000000b', 'USER_READ',       'Leer usuarios',           'Ver usuarios del tenant', 'admin'),
    ('a0000001-0001-4001-8001-00000000000c', 'USER_WRITE',      'Gestionar usuarios',     'Crear y modificar usuarios', 'admin'),
    ('a0000001-0001-4001-8001-00000000000d', 'BILLING_READ',    'Leer facturación',       'Ver planes y facturas', 'billing'),
    ('a0000001-0001-4001-8001-00000000000e', 'BILLING_WRITE',   'Gestionar facturación',  'Modificar suscripción', 'billing'),
    ('a0000001-0001-4001-8001-00000000000f', 'REPORT_READ',     'Ver reportes',           'Acceder a reportes y analytics', 'reports'),
    ('a0000001-0001-4001-8001-000000000010', 'AGENT_READ',      'Consultar agente IA',    'Hacer preguntas al agente', 'agent'),
    ('a0000001-0001-4001-8001-000000000011', 'AGENT_WRITE',     'Acciones del agente IA', 'El agente ejecuta acciones CRUD', 'agent'),
    ('a0000001-0001-4001-8001-000000000012', 'ADMIN_ACCESS',    'Acceso administración',   'Panel de administración SIGA', 'admin')
ON CONFLICT (code) DO NOTHING;

-- OWNER = todos los permisos
INSERT INTO auth.role_permissions (role, permission_id)
SELECT 'OWNER', id FROM auth.permissions
ON CONFLICT DO NOTHING;

-- EMPLOYEE = permisos operativos (sin admin ni billing)
INSERT INTO auth.role_permissions (role, permission_id)
SELECT 'EMPLOYEE', id FROM auth.permissions
WHERE code IN ('INVENTORY_READ', 'INVENTORY_WRITE', 'SALES_READ', 'SALES_WRITE',
               'DELIVERY_VIEW', 'PRODUCT_CREATE', 'PRODUCT_EDIT', 'STOCK_ADJUST',
               'AGENT_READ', 'AGENT_WRITE', 'REPORT_READ')
ON CONFLICT DO NOTHING;

-- Usuarios del tenant (customer_id = 1)
INSERT INTO auth.users (id, email, password_hash, first_name, last_name, role, customer_id, is_active)
VALUES
    ('3e24680e-1e54-5868-a2f8-23dc77cdd740', 'elizabeth@casinoeliz.cl',
     '$2a$10$DEMO_HASH_PLACEHOLDER_DO_NOT_USE_IN_PROD',
     'Elizabeth', 'González', 'OWNER', 1, true),
    ('8ea0cd5a-6fe6-5b2d-9004-75eb961f0fcd', 'hector@siga.cl',
     '$2a$10$DEMO_HASH_PLACEHOLDER_DO_NOT_USE_IN_PROD',
     'Héctor', 'Águila', 'EMPLOYEE', 1, true),
    ('f02181c8-dc68-5943-8228-3c33347f0c0b', 'yesenia@casinoeliz.cl',
     '$2a$10$DEMO_HASH_PLACEHOLDER_DO_NOT_USE_IN_PROD',
     'Yesenia', 'Martínez', 'EMPLOYEE', 1, true),
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', 'luis@casinoeliz.cl',
     '$2a$10$DEMO_HASH_PLACEHOLDER_DO_NOT_USE_IN_PROD',
     'Luis', 'Cifuentes', 'EMPLOYEE', 1, true),
    ('7b20777b-032c-54cc-8eac-cd2fef89a4a4', 'antonia@casinoeliz.cl',
     '$2a$10$DEMO_HASH_PLACEHOLDER_DO_NOT_USE_IN_PROD',
     'Antonia', 'Rojas', 'EMPLOYEE', 1, true)
ON CONFLICT (email) DO NOTHING;

-- Permisos específicos por usuario
-- Héctor: admin de inventario + kioskos
INSERT INTO auth.user_permissions (user_id, permission_id)
SELECT '8ea0cd5a-6fe6-5b2d-9004-75eb961f0fcd', id FROM auth.permissions
WHERE code IN ('INVENTORY_READ', 'INVENTORY_WRITE', 'KIOSK_ADMIN', 'STOCK_ADJUST',
               'PRODUCT_CREATE', 'PRODUCT_EDIT', 'AGENT_READ', 'AGENT_WRITE')
ON CONFLICT DO NOTHING;

-- Yesenia: solo ventas
INSERT INTO auth.user_permissions (user_id, permission_id)
SELECT 'f02181c8-dc68-5943-8228-3c33347f0c0b', id FROM auth.permissions
WHERE code IN ('SALES_READ', 'SALES_WRITE', 'INVENTORY_READ')
ON CONFLICT DO NOTHING;

-- Luis: solo ver entregas e inventario
INSERT INTO auth.user_permissions (user_id, permission_id)
SELECT '9dba16c4-98b0-5726-bc8c-f0ccc0b33166', id FROM auth.permissions
WHERE code IN ('DELIVERY_VIEW', 'INVENTORY_READ')
ON CONFLICT DO NOTHING;

-- Antonia: gestión de bodega
INSERT INTO auth.user_permissions (user_id, permission_id)
SELECT '7b20777b-032c-54cc-8eac-cd2fef89a4a4', id FROM auth.permissions
WHERE code IN ('INVENTORY_READ', 'INVENTORY_WRITE', 'STOCK_ADJUST')
ON CONFLICT DO NOTHING;

-- Asignación users → tiendas (UUIDs de inventory.stores)
INSERT INTO auth.user_stores (user_id, store_id) VALUES
    ('8ea0cd5a-6fe6-5b2d-9004-75eb961f0fcd', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a'), -- Kiosko Norte
    ('8ea0cd5a-6fe6-5b2d-9004-75eb961f0fcd', '6d590a38-13f5-599e-a053-6c23fba3adba'), -- Kiosko Sur
    ('f02181c8-dc68-5943-8228-3c33347f0c0b', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a'), -- Kiosko Norte
    ('f02181c8-dc68-5943-8228-3c33347f0c0b', '6d590a38-13f5-599e-a053-6c23fba3adba'), -- Kiosko Sur
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a'), -- Kiosko Norte
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', '6d590a38-13f5-599e-a053-6c23fba3adba'), -- Kiosko Sur
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', 'ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48'), -- Casino Colegio
    ('7b20777b-032c-54cc-8eac-cd2fef89a4a4', '467d011b-6349-5cbd-a839-12dc679eaabb')  -- Bodega Central
ON CONFLICT DO NOTHING;
