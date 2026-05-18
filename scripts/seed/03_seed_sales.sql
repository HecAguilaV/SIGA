-- =============================================================================
-- SIGA - Demo Seed Data: Sales Service (siga_sales)
-- =============================================================================

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

-- Items de ventas (UUIDs hardcodeados — inventory.products está en otra DB)
INSERT INTO sales.sale_items (sale_id, product_id, quantity, unit_price, subtotal) VALUES
    ('d0000001-0001-4001-8001-000000000001', 'd2e2981a-8ad8-5430-b363-2f75d4ff0fb7', 2, 1500, 3000),  -- Jugo Caja 1L
    ('d0000001-0001-4001-8001-000000000001', '5fd8dab2-0e49-54a4-8a83-d86ff2b3d41a', 1, 850,  850),   -- Galleta Surtida
    ('d0000001-0001-4001-8001-000000000001', '4a1b2c3d-0001-4001-8001-000000000002', 1, 1300, 1300),   -- Bebida Cola
    ('d0000001-0001-4001-8001-000000000002', '59b4bc5d-00fc-581b-9728-a5cdafb9cb7f', 1, 3200, 3200),   -- Café Molido
    ('d0000001-0001-4001-8001-000000000003', '4a1b2c3d-0001-4001-8001-000000000002', 6, 1300, 7800)    -- Bebida Cola x6
ON CONFLICT DO NOTHING;

-- Caja abierta (turno activo)
INSERT INTO sales.cash_shifts (id, store_id, user_id, initial_amount, status) VALUES
    ('e0000001-0001-4001-8001-000000000001', 'aa10275e-7d0c-5afc-be0f-b3c51fab8b4a',
     'f02181c8-dc68-5943-8228-3c33347f0c0b', 50000, 'OPEN')
ON CONFLICT DO NOTHING;
