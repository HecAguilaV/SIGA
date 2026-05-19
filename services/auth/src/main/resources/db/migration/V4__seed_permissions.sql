-- SIGA - Seed Granular Permissions (Flyway V4)
-- Inserts the initial permission catalogue for granular user-permission assignment.
-- Uses ON CONFLICT to handle re-runs safely.

INSERT INTO auth.permissions (code, name, description, category)
VALUES
    ('INVENTORY_READ',   'INVENTORY_READ',   'View inventory',                    'INVENTORY'),
    ('INVENTORY_WRITE',  'INVENTORY_WRITE',  'Add/edit products',                 'INVENTORY'),
    ('INVENTORY_DELETE', 'INVENTORY_DELETE', 'Delete products',                   'INVENTORY'),
    ('KIOSK_ADMIN',      'KIOSK_ADMIN',      'Manage kiosks',                     'KIOSK'),
    ('DELIVERY_VIEW',    'DELIVERY_VIEW',    'View delivery routes',              'DELIVERY'),
    ('REPORTS_VIEW',     'REPORTS_VIEW',     'View reports',                      'REPORTS'),
    ('SALES_CREATE',     'SALES_CREATE',     'Create sales',                      'SALES'),
    ('SALES_READ',       'SALES_READ',       'View sales',                        'SALES')
ON CONFLICT (code) DO NOTHING;
