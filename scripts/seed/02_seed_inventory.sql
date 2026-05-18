-- =============================================================================
-- SIGA - Demo Seed Data: Inventory Service (siga_inventory)
-- =============================================================================

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
    -- Bodega Central
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

-- Alertas de stock bajo
INSERT INTO inventory.alerts (type, product_id, store_id, message, is_read) VALUES
    ('LOW_STOCK', 'c2605214-3058-5fdd-a264-da89e9f631a9', '6d590a38-13f5-599e-a053-6c23fba3adba',
     'Arroz Grado 1 1kg en Kiosko Sur tiene solo 10 unidades (stock mínimo).', false),
    ('LOW_STOCK', 'ce00c15e-a8b2-56ac-ba97-dd5f66531429', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'Yogurt Natural 4u en Kiosko Norte tiene solo 10 unidades. Reponer pronto.', false),
    ('LOW_STOCK', 'cf42ef69-cc4c-5acc-a6b4-4a4791fd63d3', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'Té en Cajas 100u en Kiosko Norte tiene solo 5 unidades. Reponer.', false)
ON CONFLICT DO NOTHING;
