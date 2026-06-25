-- =============================================================================
-- SIGA - Seed Data: ProBanquet (Single Tenant)
-- =============================================================================
-- Owner: probanquet@gmail.com / demo1234
-- 1 local, ~20 productos en 6 categorías
-- =============================================================================

-- =============================================================================
-- 1. CLEAN: Eliminar datos existentes (orden inverso a FK)
-- =============================================================================
-- Auth
DELETE FROM auth.user_stores;
DELETE FROM auth.user_permissions;
DELETE FROM auth.role_permissions;
DELETE FROM auth.users;
DELETE FROM auth.customers;
DELETE FROM auth.permissions;

-- Inventory
DELETE FROM inventory.alerts;
DELETE FROM inventory.movements;
DELETE FROM inventory.stock;
DELETE FROM inventory.products;
DELETE FROM inventory.categories;
DELETE FROM inventory.stores;


-- =============================================================================
-- 2. PERMISSIONS (colon format — matching frontend PERMISSION_GUARDS)
-- =============================================================================
INSERT INTO auth.permissions (id, code, name, description, category) VALUES
    ('a0000001-0001-4001-8000-000000000001', 'inventory:view',   'Ver inventario',      'Ver productos, stock y locales', 'inventory'),
    ('a0000001-0001-4001-8000-000000000002', 'inventory:write',  'Editar inventario',   'Crear y modificar productos',    'inventory'),
    ('a0000001-0001-4001-8000-000000000003', 'inventory:delete', 'Eliminar inventario', 'Eliminar productos',             'inventory'),
    ('a0000001-0001-4001-8000-000000000004', 'stock:adjust',     'Ajustar stock',       'Realizar ajustes y conteos',     'inventory'),
    ('a0000001-0001-4001-8000-000000000005', 'pos:view',         'Ver POS',             'Ver módulo POS',                 'sales'),
    ('a0000001-0001-4001-8000-000000000006', 'pos:write',        'Vender en POS',       'Realizar ventas en POS',         'sales'),
    ('a0000001-0001-4001-8000-000000000007', 'analytics:view',   'Ver analytics',       'Ver reportes y dashboard',       'reports'),
    ('a0000001-0001-4001-8000-000000000008', 'admin:view',       'Ver usuarios',        'Ver usuarios del tenant',        'admin'),
    ('a0000001-0001-4001-8000-000000000009', 'admin:write',      'Gestionar usuarios',  'Crear y modificar usuarios',     'admin'),
    ('a0000001-0001-4001-8000-00000000000a', 'billing:view',     'Ver facturación',     'Ver planes y facturas',          'billing'),
    ('a0000001-0001-4001-8000-00000000000b', 'billing:write',    'Gestionar facturación','Modificar suscripción',          'billing'),
    ('a0000001-0001-4001-8000-00000000000c', 'report:read',      'Ver reportes',        'Acceder a reportes avanzados',   'reports'),
    ('a0000001-0001-4001-8000-00000000000d', 'agent:read',       'Consultar agente IA', 'Hacer preguntas al agente',      'agent'),
    ('a0000001-0001-4001-8000-00000000000e', 'agent:write',      'Acciones del agente', 'El agente ejecuta acciones CRUD','agent'),
    ('a0000001-0001-4001-8000-00000000000f', 'delivery:view',    'Ver entregas',        'Consultar rutas y entregas',     'delivery')
ON CONFLICT (code) DO NOTHING;


-- =============================================================================
-- 3. ROLE PERMISSIONS: OWNER → todos los permisos
-- =============================================================================
INSERT INTO auth.role_permissions (role, permission_id)
SELECT 'OWNER', id FROM auth.permissions
ON CONFLICT DO NOTHING;

-- EMPLOYEE → permisos operativos
INSERT INTO auth.role_permissions (role, permission_id)
SELECT 'EMPLOYEE', id FROM auth.permissions
WHERE code IN ('inventory:view', 'inventory:write', 'stock:adjust',
               'pos:view', 'pos:write',
               'agent:read', 'agent:write', 'report:read')
ON CONFLICT DO NOTHING;


-- =============================================================================
-- 4. CUSTOMER (Tenant): ProBanquet
-- =============================================================================
INSERT INTO auth.customers (email, password_hash, name, last_name, company_name, is_active, is_on_trial, role, email_verified)
VALUES (
    'probanquet@gmail.com',
    '$2b$12$2gmMNWgibgUqxaJdBQTZA.ALI6Ly7CWSD3Dz6Eg3dq7j59oXadDe.',
    'Héctor', 'Águila',
    'ProBanquet SpA.',
    true, false, 'customer', true
) ON CONFLICT (email) DO NOTHING;


-- =============================================================================
-- 5. USER (OWNER): probanquet — el admin del tenant
-- =============================================================================
INSERT INTO auth.users (id, email, password_hash, first_name, last_name, role, customer_id, is_active)
VALUES (
    'a0000000-0000-4000-8000-000000000001',
    'probanquet@gmail.com',
    '$2b$12$2gmMNWgibgUqxaJdBQTZA.ALI6Ly7CWSD3Dz6Eg3dq7j59oXadDe.',
    'Héctor', 'Águila',
    'OWNER', (SELECT id FROM auth.customers WHERE email = 'probanquet@gmail.com'), true
) ON CONFLICT (email) DO NOTHING;


-- =============================================================================
-- 6. INVENTORY: 1 Local
-- =============================================================================
INSERT INTO inventory.stores (id, name, address, city, commercial_user_id, is_active)
VALUES (
    'b0000000-0001-4000-8000-000000000001',
    'ProBanquet Centro',
    'Av. Providencia 1234, Local 1',
    'Santiago',
    'a0000000-0000-4000-8000-000000000001',
    true
) ON CONFLICT (id) DO NOTHING;

-- Asignar el local al usuario OWNER
INSERT INTO auth.user_stores (user_id, store_id)
VALUES ('a0000000-0000-4000-8000-000000000001', 'b0000000-0001-4000-8000-000000000001')
ON CONFLICT DO NOTHING;


-- =============================================================================
-- 7. CATEGORIES (6)
-- =============================================================================
INSERT INTO inventory.categories (id, name, description, is_active) VALUES
    ('c0000000-0001-4000-8000-000000000001', 'Bebidas',       'Bebidas gaseosas, jugos, aguas', true),
    ('c0000000-0001-4000-8000-000000000002', 'Lácteos',       'Leche, quesos, yogures',         true),
    ('c0000000-0001-4000-8000-000000000003', 'Despensa',      'Pastas, arroz, legumbres, enlatados', true),
    ('c0000000-0001-4000-8000-000000000004', 'Snacks',        'Galletas, papas fritas, barras',  true),
    ('c0000000-0001-4000-8000-000000000005', 'Limpieza',      'Detergentes, lavaloza, cloro',    true),
    ('c0000000-0001-4000-8000-000000000006', 'Cuidado Personal', 'Jabón, shampoo, desodorante',   true)
ON CONFLICT (name) DO NOTHING;


-- =============================================================================
-- 8. PRODUCTS (20)
-- =============================================================================
INSERT INTO inventory.products (id, sku, barcode, name, description, category_id, unit_price, unit_type, commercial_user_id, is_active)
VALUES
    -- Bebidas
    ('d0000000-0001-4000-8000-000000000001', 'BEB-001', '7800010000019', 'Coca-Cola 1.5L',
     'Bebida gaseosa Coca-Cola 1.5 litros', 'c0000000-0001-4000-8000-000000000001', 2990.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000002', 'BEB-002', '7800010000026', 'Agua Mineral Cielo 1.5L',
     'Agua mineral sin gas 1.5 litros', 'c0000000-0001-4000-8000-000000000001', 1290.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000003', 'BEB-003', '7800010000033', 'Jugo Natural Naranja 1L',
     'Jugo natural de naranja 1 litro', 'c0000000-0001-4000-8000-000000000001', 2490.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),

    -- Lácteos
    ('d0000000-0001-4000-8000-000000000004', 'LAC-001', '7800010000040', 'Leche Entera 1L',
     'Leche de vaca entera 1 litro', 'c0000000-0001-4000-8000-000000000002', 1190.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000005', 'LAC-002', '7800010000057', 'Yogur Natural Batido 1kg',
     'Yogur natural cremoso 1 kg', 'c0000000-0001-4000-8000-000000000002', 3290.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000006', 'LAC-003', '7800010000064', 'Queso Gauda 500g',
     'Queso gauda laminado 500 gramos', 'c0000000-0001-4000-8000-000000000002', 4990.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),

    -- Despensa
    ('d0000000-0001-4000-8000-000000000007', 'DES-001', '7800010000071', 'Arroz Grado 1 1kg',
     'Arroz grado 1 grano largo 1 kilo', 'c0000000-0001-4000-8000-000000000003', 1590.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000008', 'DES-002', '7800010000088', 'Spaghetti 400g',
     'Pastas largas tipo spaghetti 400 gramos', 'c0000000-0001-4000-8000-000000000003', 990.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000009', 'DES-003', '7800010000095', 'Porotos Negros 500g',
     'Legumbres porotos negros 500 gramos', 'c0000000-0001-4000-8000-000000000003', 1890.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-00000000000a', 'DES-004', '7800010000101', 'Atún En Aceite 3x80g',
     'Atún en aceite vegetal pack 3 latas', 'c0000000-0001-4000-8000-000000000003', 3990.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-00000000000b', 'DES-005', '7800010000118', 'Aceite Vegetal 1L',
     'Aceite vegetal mezcla 1 litro', 'c0000000-0001-4000-8000-000000000003', 2490.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),

    -- Snacks
    ('d0000000-0001-4000-8000-00000000000c', 'SNK-001', '7800010000125', 'Papas Fritas Clásicas 160g',
     'Papas fritas sabor clásico 160 gramos', 'c0000000-0001-4000-8000-000000000004', 1890.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-00000000000d', 'SNK-002', '7800010000132', 'Galletas Surtidas 300g',
     'Galletas dulces variadas 300 gramos', 'c0000000-0001-4000-8000-000000000004', 2590.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-00000000000e', 'SNK-003', '7800010000149', 'Barra Cereal Chocolate x6',
     'Barras de cereal bañadas en chocolate pack 6', 'c0000000-0001-4000-8000-000000000004', 3290.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),

    -- Limpieza
    ('d0000000-0001-4000-8000-00000000000f', 'LIM-001', '7800010000156', 'Detergente Líquido 1L',
     'Detergente líquido para ropa 1 litro', 'c0000000-0001-4000-8000-000000000005', 3990.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000010', 'LIM-002', '7800010000163', 'Lavaloza 750ml',
     'Lavaloza líquido antibacterial 750 ml', 'c0000000-0001-4000-8000-000000000005', 2590.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000011', 'LIM-003', '7800010000170', 'Cloro Gel 1L',
     'Cloro gel espeso 1 litro', 'c0000000-0001-4000-8000-000000000005', 1790.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),

    -- Cuidado Personal
    ('d0000000-0001-4000-8000-000000000012', 'CP-001', '7800010000187', 'Jabón Líquido Manos 250ml',
     'Jabón líquido para manos 250 ml', 'c0000000-0001-4000-8000-000000000006', 2290.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000013', 'CP-002', '7800010000194', 'Shampoo 2en1 400ml',
     'Shampoo y acondicionador 2 en 1 400 ml', 'c0000000-0001-4000-8000-000000000006', 4990.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true),
    ('d0000000-0001-4000-8000-000000000014', 'CP-003', '7800010000200', 'Desodorante Spray 150ml',
     'Desodorante aerosol protección 48h 150 ml', 'c0000000-0001-4000-8000-000000000006', 3490.00, 'UNIDAD',
     'a0000000-0000-4000-8000-000000000001', true)
ON CONFLICT (barcode) DO NOTHING;


-- =============================================================================
-- 9. STOCK: todos los productos en el local
-- =============================================================================
INSERT INTO inventory.stock (id, product_id, store_id, quantity, minimum_quantity, updated_at, last_movement_at)
SELECT
    gen_random_uuid(), p.id, 'b0000000-0001-4000-8000-000000000001',
    CASE p.sku
        WHEN 'BEB-001' THEN 50 WHEN 'BEB-002' THEN 40 WHEN 'BEB-003' THEN 25
        WHEN 'LAC-001' THEN 30 WHEN 'LAC-002' THEN 20 WHEN 'LAC-003' THEN 15
        WHEN 'DES-001' THEN 60 WHEN 'DES-002' THEN 80 WHEN 'DES-003' THEN 35 WHEN 'DES-004' THEN 40 WHEN 'DES-005' THEN 30
        WHEN 'SNK-001' THEN 45 WHEN 'SNK-002' THEN 35 WHEN 'SNK-003' THEN 25
        WHEN 'LIM-001' THEN 25 WHEN 'LIM-002' THEN 30 WHEN 'LIM-003' THEN 20
        WHEN 'CP-001'  THEN 30 WHEN 'CP-002'  THEN 20 WHEN 'CP-003'  THEN 25
        ELSE 15
    END,
    5, NOW(), NOW()
FROM inventory.products p
WHERE NOT EXISTS (
    SELECT 1 FROM inventory.stock s
    WHERE s.product_id = p.id AND s.store_id = 'b0000000-0001-4000-8000-000000000001'
);

-- =============================================================================
-- HECHO. Owner: probanquet@gmail.com / demo1234
-- =============================================================================
