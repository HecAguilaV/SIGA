-- =============================================================================
-- SIGA - Seed Data: Lito Librería y Bazar
-- Customer: Yasna Aguila (yasna@lito.cl, customer_id=2)
-- God Admin: godadmin@siga.cl (customer_id=1)
--
-- Contraseñas:
--   God Admin:    KikeThron4466.
--   Yasna:        LitoLibreria2026!
--   Cajeros:      LitoCajero2026!
-- =============================================================================

-- =============================================================================
-- 1. AUTENTICACIÓN (ejecutar en siga_auth)
-- =============================================================================

-- 1.1 Crear los empleados de Yasna
INSERT INTO auth.users (id, email, password_hash, first_name, last_name, role, customer_id, is_active, created_at, updated_at)
VALUES
    ('a1000000-0001-4000-8000-000000000001',
     'cajero1@lito.cl',
     '$2b$10$kZ05/QmRfdqbt/I3sK3FOuDU4lky7/yv2ZRftCJ6HbsZN63RfDTEq',
     'Carlos', 'Muñoz', 'CASHIER', 2, true, NOW(), NOW()),
    ('a1000000-0001-4000-8000-000000000002',
     'cajero2@lito.cl',
     '$2b$10$kZ05/QmRfdqbt/I3sK3FOuDU4lky7/yv2ZRftCJ6HbsZN63RfDTEq',
     'María', 'Soto', 'CASHIER', 2, true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- 1.2 Asignar cajeros a tiendas
INSERT INTO auth.user_stores (user_id, store_id, assigned_at) VALUES
    ('a1000000-0001-4000-8000-000000000001', 'b1000000-0001-4000-8000-000000000001', NOW()),
    ('a1000000-0001-4000-8000-000000000002', 'b1000000-0001-4000-8000-000000000002', NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 2. INVENTARIO (ejecutar en siga_inventory)
-- =============================================================================

INSERT INTO inventory.stores (id, name, address, city, is_active, created_at)
VALUES
    ('b1000000-0001-4000-8000-000000000001',
     'Lito Librería Centro',
     'Av. Providencia 1234, Local 5', 'Santiago',
     true, NOW()),
    ('b1000000-0001-4000-8000-000000000002',
     'Lito Bazar Norte',
     'Av. Independencia 5678', 'Santiago',
     true, NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO inventory.categories (id, name, description, is_active, created_at)
VALUES
    ('c1000000-0001-4000-8000-000000000001', 'Libros', 'Libros y textos', true, NOW()),
    ('c1000000-0001-4000-8000-000000000002', 'Cuadernos', 'Cuadernos y blocks de notas', true, NOW()),
    ('c1000000-0001-4000-8000-000000000003', 'Escritura', 'Lápices, bolígrafos, marcadores', true, NOW()),
    ('c1000000-0001-4000-8000-000000000004', 'Escolar', 'Útiles escolares generales', true, NOW()),
    ('c1000000-0001-4000-8000-000000000005', 'Bazar', 'Artículos de bazar y decoración', true, NOW()),
    ('c1000000-0001-4000-8000-000000000006', 'Oficina', 'Insumos de oficina', true, NOW())
ON CONFLICT (name) DO NOTHING;

INSERT INTO inventory.products (id, sku, barcode, name, description, category_id, unit_price, unit_type, is_active, created_at, updated_at)
VALUES
    ('d1000000-0001-4000-8000-000000000001', 'LIB-001', '7800000000011', 'El Principito (Edición Especial)', 'Saint-Exupéry, tapa dura', 'c1000000-0001-4000-8000-000000000001', 18990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000002', 'LIB-002', '7800000000028', 'Cien Años de Soledad', 'García Márquez, edición conmemorativa', 'c1000000-0001-4000-8000-000000000001', 24990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000003', 'LIB-003', '7800000000035', 'Manual de Contabilidad General', 'Texto académico contabilidad', 'c1000000-0001-4000-8000-000000000001', 32990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000004', 'LIB-004', '7800000000042', 'Atlas Universal Ilustrado', 'Atlas geográfico a todo color', 'c1000000-0001-4000-8000-000000000001', 45990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000005', 'CDN-001', '7800000000059', 'Cuaderno Universitario 100 Hojas', 'Cuaderno cuadriculado, espiral', 'c1000000-0001-4000-8000-000000000002', 2990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000006', 'CDN-002', '7800000000066', 'Cuaderno Profesional Tapa Dura 200h', 'Cuaderno ejecutivo, línea fina', 'c1000000-0001-4000-8000-000000000002', 7990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000007', 'CDN-003', '7800000000073', 'Block de Notas Adhesivas 100h', 'Post-it surtidos, 5 colores', 'c1000000-0001-4000-8000-000000000002', 1990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000008', 'ESC-001', '7800000000080', 'Set Bolígrafos x12 (Azul)', 'Bolígrafos tinta azul, pack 12', 'c1000000-0001-4000-8000-000000000003', 3990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000009', 'ESC-002', '7800000000097', 'Lápiz Grafito HB x12', 'Lápices grafito HB, pack 12', 'c1000000-0001-4000-8000-000000000003', 2490.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000010', 'ESC-003', '7800000000103', 'Marcadores Permanentes x8', 'Marcadores multiusos, 8 colores', 'c1000000-0001-4000-8000-000000000003', 5990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000011', 'ESC-004', '7800000000110', 'Resaltadores Fluorescentes x6', 'Resaltadores surtidos, 6 colores', 'c1000000-0001-4000-8000-000000000003', 3490.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000012', 'ESC-005', '7800000000127', 'Mochila Escolar Reforzada', 'Mochila compartimentos, resistente', 'c1000000-0001-4000-8000-000000000004', 19990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000013', 'ESC-006', '7800000000134', 'Estuche Geometría Completo', 'Compás, transportador, reglas', 'c1000000-0001-4000-8000-000000000004', 4990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000014', 'ESC-007', '7800000000141', 'Goma de Borrar Blanca x10', 'Gomas blandas, pack 10', 'c1000000-0001-4000-8000-000000000004', 1490.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000015', 'BZR-001', '7800000000158', 'Vaso Decorativo Cerámica', 'Vaso artesanal cerámica esmaltada', 'c1000000-0001-4000-8000-000000000005', 8990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000016', 'BZR-002', '7800000000165', 'Set 3 Velas Aromáticas', 'Velas de soja, lavanda, vainilla', 'c1000000-0001-4000-8000-000000000005', 6990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000017', 'BZR-003', '7800000000172', 'Marco Fotos Madera 15x20', 'Marco de madera natural', 'c1000000-0001-4000-8000-000000000005', 4990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000018', 'BZR-004', '7800000000189', 'Reloj de Pared Decorativo', 'Reloj vintage 30cm, silencioso', 'c1000000-0001-4000-8000-000000000005', 24990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000019', 'OFI-001', '7800000000196', 'Calculadora Científica', 'Calculadora 240 funciones, solar', 'c1000000-0001-4000-8000-000000000006', 12990.00, 'UNIDAD', true, NOW(), NOW()),
    ('d1000000-0001-4000-8000-000000000020', 'OFI-002', '7800000000202', 'Archivador Lomo Ancho A4', 'Archivador metálico, 500 hojas', 'c1000000-0001-4000-8000-000000000006', 7990.00, 'UNIDAD', true, NOW(), NOW())
ON CONFLICT (barcode) DO NOTHING;

-- Stock Tienda Centro
INSERT INTO inventory.stock (id, product_id, store_id, quantity, minimum_quantity, updated_at, last_movement_at)
SELECT gen_random_uuid(), p.id, 'b1000000-0001-4000-8000-000000000001',
    CASE p.sku
        WHEN 'LIB-001' THEN 15 WHEN 'LIB-002' THEN 15 WHEN 'LIB-003' THEN 8 WHEN 'LIB-004' THEN 8
        WHEN 'CDN-001' THEN 80 WHEN 'CDN-002' THEN 30 WHEN 'CDN-003' THEN 50
        WHEN 'ESC-001' THEN 40 WHEN 'ESC-002' THEN 35 WHEN 'ESC-003' THEN 20 WHEN 'ESC-004' THEN 25
        WHEN 'ESC-005' THEN 12 WHEN 'ESC-006' THEN 18 WHEN 'ESC-007' THEN 60
        WHEN 'BZR-001' THEN 10 WHEN 'BZR-002' THEN 15 WHEN 'BZR-003' THEN 20 WHEN 'BZR-004' THEN 5
        WHEN 'OFI-001' THEN 12 WHEN 'OFI-002' THEN 15
        ELSE 10
    END,
    5, NOW(), NOW()
FROM inventory.products p
WHERE NOT EXISTS (SELECT 1 FROM inventory.stock s WHERE s.product_id = p.id AND s.store_id = 'b1000000-0001-4000-8000-000000000001');

-- Stock Tienda Norte
INSERT INTO inventory.stock (id, product_id, store_id, quantity, minimum_quantity, updated_at, last_movement_at)
SELECT gen_random_uuid(), p.id, 'b1000000-0001-4000-8000-000000000002',
    CASE p.sku
        WHEN 'LIB-001' THEN 8 WHEN 'LIB-002' THEN 8 WHEN 'LIB-003' THEN 4 WHEN 'LIB-004' THEN 4
        WHEN 'CDN-001' THEN 40 WHEN 'CDN-002' THEN 15 WHEN 'CDN-003' THEN 25
        WHEN 'ESC-001' THEN 20 WHEN 'ESC-002' THEN 18 WHEN 'ESC-003' THEN 10 WHEN 'ESC-004' THEN 12
        WHEN 'ESC-005' THEN 6 WHEN 'ESC-006' THEN 9 WHEN 'ESC-007' THEN 30
        WHEN 'BZR-001' THEN 5 WHEN 'BZR-002' THEN 8 WHEN 'BZR-003' THEN 10 WHEN 'BZR-004' THEN 3
        WHEN 'OFI-001' THEN 6 WHEN 'OFI-002' THEN 8
        ELSE 5
    END,
    3, NOW(), NOW()
FROM inventory.products p
WHERE NOT EXISTS (SELECT 1 FROM inventory.stock s WHERE s.product_id = p.id AND s.store_id = 'b1000000-0001-4000-8000-000000000002');

-- =============================================================================
-- Si los usuarios ya existen, actualizar contraseñas
-- =============================================================================
UPDATE auth.customers SET password_hash = '$2b$10$bV5Lbcu2Rx6AwlH6jv5yG.Rbpnc5riqDaxn6fIJE/VhtkODahoPga'
WHERE email = 'yasna@lito.cl';

UPDATE auth.customers SET password_hash = '$2a$10$mODkEaItfYM9J.dEfNQqOeFdRkQv5VcFtqSUfAF3GJRPZb0tXOga'
WHERE email = 'godadmin@siga.cl';

UPDATE auth.users SET password_hash = '$2b$10$kZ05/QmRfdqbt/I3sK3FOuDU4lky7/yv2ZRftCJ6HbsZN63RfDTEq'
WHERE email IN ('cajero1@lito.cl', 'cajero2@lito.cl');
