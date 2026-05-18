-- =============================================================================
-- SIGA - Demo Seed Data
-- =============================================================================
-- Carga data demo para la experiencia interactiva.
-- Los UUIDs son determinísticos (UUID v5) para mantener referencias cruzadas.
-- Idempotente: usa ON CONFLICT DO NOTHING.
--
-- Contraseña demo para TODOS los usuarios: demo123
-- Hash BCrypt: $2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS
-- =============================================================================

-- #############################################################################
-- AUTH SERVICE (DB: siga_auth, Schema: auth)
-- #############################################################################

-- Cliente / Tenant demo (Elizabeth, la dueña)
INSERT INTO auth.customers (email, password_hash, name, last_name, company_name, is_active, is_on_trial, role)
VALUES (
    'elizabeth@casinoeliz.cl',
    '$2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS',
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

-- Mapeo rol → permisos
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

-- Usuarios del tenant
-- NOTA: customer_id se obtiene del insert anterior (asumiendo SERIAL = 1)

INSERT INTO auth.users (id, email, password_hash, first_name, last_name, role, customer_id, is_active)
VALUES
    ('3e24680e-1e54-5868-a2f8-23dc77cdd740', 'elizabeth@casinoeliz.cl',
     '$2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS',
     'Elizabeth', 'González', 'OWNER', 1, true),
    ('8ea0cd5a-6fe6-5b2d-9004-75eb961f0fcd', 'hector@siga.cl',
     '$2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS',
     'Héctor', 'Águila', 'EMPLOYEE', 1, true),
    ('f02181c8-dc68-5943-8228-3c33347f0c0b', 'yesenia@casinoeliz.cl',
     '$2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS',
     'Yesenia', 'Martínez', 'EMPLOYEE', 1, true),
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', 'luis@casinoeliz.cl',
     '$2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS',
     'Luis', 'Cifuentes', 'EMPLOYEE', 1, true),
    ('7b20777b-032c-54cc-8eac-cd2fef89a4a4', 'antonia@casinoeliz.cl',
     '$2b$12$IwerABf2qrSfqo5m7pAWj.jnBNl4RHiVU/IdzFpZu8whVN26Z5uAS',
     'Antonia', 'Rojas', 'EMPLOYEE', 1, true)
ON CONFLICT (email) DO NOTHING;

-- Permisos específicos por usuario (además del rol)
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

-- #############################################################################
-- INVENTORY SERVICE (DB: siga_inventory, Schema: inventory)
-- #############################################################################

-- Categorías
INSERT INTO inventory.categories (id, name, description, commercial_user_id) VALUES
    ('b0000001-0001-4001-8001-000000000001', 'Abarrotes',    'Productos de despensa no perecibles', '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('b0000001-0001-4001-8001-000000000002', 'Bebidas',      'Bebidas y jugos',                   '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('b0000001-0001-4001-8001-000000000003', 'Lácteos',      'Lácteos y derivados',               '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('b0000001-0001-4001-8001-000000000004', 'Limpieza',     'Artículos de limpieza e higiene',   '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('b0000001-0001-4001-8001-000000000005', 'Snacks',       'Galletas, snacks y confites',       '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('b0000001-0001-4001-8001-000000000006', 'Congelados',   'Productos congelados',              '3e24680e-1e54-5868-a2f8-23dc77cdd740')
ON CONFLICT (name, commercial_user_id) DO NOTHING;

-- Tiendas / Puntos de venta
INSERT INTO inventory.stores (id, name, address, city, commercial_user_id) VALUES
    ('aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 'Kiosko Norte',    'Av. Principal 1234',       'Santiago', '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('6d590a38-13f5-599e-a053-6c23fba3adba', 'Kiosko Sur',      'Calle Los Álamos 567',     'Santiago', '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48', 'Casino Colegio',  'Av. Educación 890',        'Santiago', '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('467d011b-6349-5cbd-a839-12dc679eaabb', 'Bodega Central',  'Zona Industrial 456',      'Santiago', '3e24680e-1e54-5868-a2f8-23dc77cdd740')
ON CONFLICT DO NOTHING;

-- Asignación users → tiendas (para auth.user_stores)
INSERT INTO auth.user_stores (user_id, store_id) VALUES
    ('8ea0cd5a-6fe6-5b2d-9004-75eb961f0fcd', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a'),
    ('8ea0cd5a-6fe6-5b2d-9004-75eb961f0fcd', '6d590a38-13f5-599e-a053-6c23fba3adba'),
    ('f02181c8-dc68-5943-8228-3c33347f0c0b', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a'),
    ('f02181c8-dc68-5943-8228-3c33347f0c0b', '6d590a38-13f5-599e-a053-6c23fba3adba'),
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a'),
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', '6d590a38-13f5-599e-a053-6c23fba3adba'),
    ('9dba16c4-98b0-5726-bc8c-f0ccc0b33166', 'ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48'),
    ('7b20777b-032c-54cc-8eac-cd2fef89a4a4', '467d011b-6349-5cbd-a839-12dc679eaabb')
ON CONFLICT DO NOTHING;

-- Productos
INSERT INTO inventory.products (id, name, description, category_id, unit_price, commercial_user_id) VALUES
    ('c2605214-3058-5fdd-a264-da89e9f631a9', 'Arroz Grado 1 1kg',       'Arroz blanco grano largo',       'b0000001-0001-4001-8001-000000000001', 1200, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('b6af7d43-d92f-53ca-8040-da20d06a98ad', 'Fideos Spaghetti 500g',   'Pasta italiana',                  'b0000001-0001-4001-8001-000000000001', 950,  '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('de469425-39df-5d4f-8a09-ae98392db22c', 'Aceite Vegetal 1L',       'Aceite mezcla',                   'b0000001-0001-4001-8001-000000000001', 2500, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('f92800ed-2385-5d4f-896f-e6c03c4dd434', 'Azúcar 1kg',              'Azúcar blanca granulada',         'b0000001-0001-4001-8001-000000000001', 1100, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('59b4bc5d-00fc-581b-9728-a5cdafb9cb7f', 'Café Molido 250g',        'Café tostado molido',             'b0000001-0001-4001-8001-000000000001', 3200, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('cf42ef69-cc4c-5acc-a6b4-4a4791fd63d3', 'Té en Cajas 100u',        'Té negro tradicional',            'b0000001-0001-4001-8001-000000000001', 2800, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('5fd8dab2-0e49-54a4-8a83-d86ff2b3d41a', 'Galleta Surtida 200g',    'Galletas variadas',               'b0000001-0001-4001-8001-000000000005', 850,  '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('3a1b2c3d-0001-4001-8001-000000000001', 'Galleta Salada 150g',     'Galletas saladas tipo cracker',   'b0000001-0001-4001-8001-000000000005', 750,  '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('89ad45a9-234e-5476-9f9c-b932e4b50597', 'Leche Entera 1L',         'Leche larga vida',                'b0000001-0001-4001-8001-000000000003', 1100, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('ce00c15e-a8b2-56ac-ba97-dd5f66531429', 'Yogurt Natural 4u',       'Yogurt batido pack 4 unidades',   'b0000001-0001-4001-8001-000000000003', 2200, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('d2e2981a-8ad8-5430-b363-2f75d4ff0fb7', 'Jugo Caja 1L',            'Jugo de fruta en caja',           'b0000001-0001-4001-8001-000000000002', 1500, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('4a1b2c3d-0001-4001-8001-000000000002', 'Bebida Cola 500ml',       'Bebida gaseosa cola',             'b0000001-0001-4001-8001-000000000002', 1300, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('4a1b2c3d-0001-4001-8001-000000000003', 'Agua Mineral 1.5L',       'Agua purificada sin gas',         'b0000001-0001-4001-8001-000000000002', 1000, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('1022a2f4-3c4e-58bf-8e2b-fdc621d1b0b9', 'Servilleta 100u',         'Servilletas blancas descartables', 'b0000001-0001-4001-8001-000000000004', 900,  '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('5a1b2c3d-0001-4001-8001-000000000004', 'Lavaloza 500ml',          'Detergente lavaplatos',           'b0000001-0001-4001-8001-000000000004', 1800, '3e24680e-1e54-5868-a2f8-23dc77cdd740'),
    ('6a1b2c3d-0001-4001-8001-000000000005', 'Papel Higiénico 4u',     'Papel higiénico doble hoja',      'b0000001-0001-4001-8001-000000000004', 1600, '3e24680e-1e54-5868-a2f8-23dc77cdd740')
ON CONFLICT DO NOTHING;

-- Stock por tienda
-- Formato: producto, tienda, cantidad, mínimo
INSERT INTO inventory.stock (product_id, store_id, quantity, minimum_quantity)
SELECT p.id, s.id, qty, min
FROM (VALUES
    -- Kiosko Norte
    ('c2605214-3058-5fdd-a264-da89e9f631a9', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 25,  10),
    ('b6af7d43-d92f-53ca-8040-da20d06a98ad', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 18,  10),
    ('de469425-39df-5d4f-8a09-ae98392db22c', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 12,  5),
    ('f92800ed-2385-5d4f-896f-e6c03c4dd434', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 30,  10),
    ('59b4bc5d-00fc-581b-9728-a5cdafb9cb7f', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 8,   5),
    ('cf42ef69-cc4c-5acc-a6b4-4a4791fd63d3', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 5,   3),
    ('5fd8dab2-0e49-54a4-8a83-d86ff2b3d41a', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 40,  15),
    ('3a1b2c3d-0001-4001-8001-000000000001', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 35,  15),
    ('89ad45a9-234e-5476-9f9c-b932e4b50597', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 15,  10),
    ('ce00c15e-a8b2-56ac-ba97-dd5f66531429', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 10,  5),
    ('d2e2981a-8ad8-5430-b363-2f75d4ff0fb7', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 22,  10),
    ('4a1b2c3d-0001-4001-8001-000000000002', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 50,  20),
    ('4a1b2c3d-0001-4001-8001-000000000003', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 30,  10),
    ('1022a2f4-3c4e-58bf-8e2b-fdc621d1b0b9', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a', 20,  10),
    -- Kiosko Sur
    ('c2605214-3058-5fdd-a264-da89e9f631a9', '6d590a38-13f5-599e-a053-6c23fba3adba', 10,  10),
    ('5fd8dab2-0e49-54a4-8a83-d86ff2b3d41a', '6d590a38-13f5-599e-a053-6c23fba3adba', 25,  15),
    ('d2e2981a-8ad8-5430-b363-2f75d4ff0fb7', '6d590a38-13f5-599e-a053-6c23fba3adba', 15,  10),
    ('4a1b2c3d-0001-4001-8001-000000000002', '6d590a38-13f5-599e-a053-6c23fba3adba', 30,  20),
    ('4a1b2c3d-0001-4001-8001-000000000003', '6d590a38-13f5-599e-a053-6c23fba3adba', 20,  10),
    -- Casino Colegio
    ('de469425-39df-5d4f-8a09-ae98392db22c', 'ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48', 20,  10),
    ('f92800ed-2385-5d4f-896f-e6c03c4dd434', 'ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48', 15,  10),
    ('59b4bc5d-00fc-581b-9728-a5cdafb9cb7f', 'ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48', 10,  5),
    ('cf42ef69-cc4c-5acc-a6b4-4a4791fd63d3', 'ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48', 8,   5),
    ('89ad45a9-234e-5476-9f9c-b932e4b50597', 'ddb8ae2e-1186-5ce3-a5b1-e95fc68d4c48', 20,  10),
    -- Bodega Central (stock grande)
    ('c2605214-3058-5fdd-a264-da89e9f631a9', '467d011b-6349-5cbd-a839-12dc679eaabb', 200, 50),
    ('b6af7d43-d92f-53ca-8040-da20d06a98ad', '467d011b-6349-5cbd-a839-12dc679eaabb', 150, 50),
    ('de469425-39df-5d4f-8a09-ae98392db22c', '467d011b-6349-5cbd-a839-12dc679eaabb', 60,  20),
    ('f92800ed-2385-5d4f-896f-e6c03c4dd434', '467d011b-6349-5cbd-a839-12dc679eaabb', 100, 30),
    ('5fd8dab2-0e49-54a4-8a83-d86ff2b3d41a', '467d011b-6349-5cbd-a839-12dc679eaabb', 120, 40),
    ('1022a2f4-3c4e-58bf-8e2b-fdc621d1b0b9', '467d011b-6349-5cbd-a839-12dc679eaabb', 500, 100),
    ('5a1b2c3d-0001-4001-8001-000000000004', '467d011b-6349-5cbd-a839-12dc679eaabb', 40,  15),
    ('6a1b2c3d-0001-4001-8001-000000000005', '467d011b-6349-5cbd-a839-12dc679eaabb', 60,  20)
) AS t(p_id, s_id, qty, min)
JOIN inventory.products p ON p.id = t.p_id::uuid
JOIN inventory.stores s ON s.id = t.s_id::uuid
ON CONFLICT (product_id, store_id) DO UPDATE
SET quantity = EXCLUDED.quantity, minimum_quantity = EXCLUDED.minimum_quantity;

-- Alertas de stock bajo (para que el dashboard muestre algo)
INSERT INTO inventory.alerts (type, product_id, store_id, message, is_read) VALUES
    ('LOW_STOCK', 'c2605214-3058-5fdd-a264-da89e9f631a9', '6d590a38-13f5-599e-a053-6c23fba3adba',
     'Arroz Grado 1 1kg en Kiosko Sur tiene solo 10 unidades (stock mínimo).', false),
    ('LOW_STOCK', 'ce00c15e-a8b2-56ac-ba97-dd5f66531429', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'Yogurt Natural 4u en Kiosko Norte tiene solo 10 unidades. Reponer pronto.', false),
    ('LOW_STOCK', 'cf42ef69-cc4c-5acc-a6b4-4a4791fd63d3', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'Té en Cajas 100u en Kiosko Norte tiene solo 5 unidades. Reponer.', false)
ON CONFLICT DO NOTHING;

-- #############################################################################
-- SALES SERVICE (DB: siga_sales, Schema: sales)
-- #############################################################################

-- Métodos de pago
INSERT INTO sales.payment_methods (id, name) VALUES
    ('c0000001-0001-4001-8001-000000000001', 'Efectivo'),
    ('c0000001-0001-4001-8001-000000000002', 'Débito'),
    ('c0000001-0001-4001-8001-000000000003', 'Crédito'),
    ('c0000001-0001-4001-8001-000000000004', 'Transferencia')
ON CONFLICT (name) DO NOTHING;

-- Ventas demo (últimos días)
INSERT INTO sales.sales (id, store_id, user_id, total, status, commercial_user_id) VALUES
    ('d0000001-0001-4001-8001-000000000001', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'f02181c8-dc68-5943-8228-3c33347f0c0b', 4500, 'COMPLETED', 1),
    ('d0000001-0001-4001-8001-000000000002', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'f02181c8-dc68-5943-8228-3c33347f0c0b', 3200, 'COMPLETED', 1),
    ('d0000001-0001-4001-8001-000000000003', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'f02181c8-dc68-5943-8228-3c33347f0c0b', 7800, 'COMPLETED', 1)
ON CONFLICT DO NOTHING;

-- Items de ventas
INSERT INTO sales.sale_items (sale_id, product_id, quantity, unit_price, subtotal)
SELECT s.id, p.id, qty, price, subtotal
FROM (VALUES
    ('d0000001-0001-4001-8001-000000000001', 'd2e2981a-8ad8-5430-b363-2f75d4ff0fb7', 2, 1500, 3000),
    ('d0000001-0001-4001-8001-000000000001', '5fd8dab2-0e49-54a4-8a83-d86ff2b3d41a', 1, 850,  850),
    ('d0000001-0001-4001-8001-000000000001', '4a1b2c3d-0001-4001-8001-000000000002', 1, 1300, 1300),
    ('d0000001-0001-4001-8001-000000000002', '59b4bc5d-00fc-581b-9728-a5cdafb9cb7f', 1, 3200, 3200),
    ('d0000001-0001-4001-8001-000000000003', '4a1b2c3d-0001-4001-8001-000000000002', 6, 1300, 7800)
) AS t(sale_id, prod_id, qty, price, subtotal)
JOIN sales.sales s ON s.id = t.sale_id::uuid
JOIN inventory.products p ON p.id = t.prod_id::uuid
ON CONFLICT DO NOTHING;

-- Caja abierta (turno activo)
INSERT INTO sales.cash_shifts (id, store_id, user_id, initial_amount, status) VALUES
    ('e0000001-0001-4001-8001-000000000001', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'f02181c8-dc68-5943-8228-3c33347f0c0b', 50000, 'OPEN')
ON CONFLICT DO NOTHING;

-- #############################################################################
-- BILLING SERVICE (DB: siga_billing, Schema: billing)
-- #############################################################################

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

-- Suscripción activa (Plan Avanzado)
INSERT INTO billing.subscriptions (customer_id, plan_id, billing_period, status, starts_at)
VALUES (
    '3e24680e-1e54-5868-a2f8-23dc77cdd740',
    'f0000001-0001-4001-8001-000000000002',
    'MONTHLY', 'ACTIVE',
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- =============================================================================
-- FIN - Demo Seed Data
-- =============================================================================
