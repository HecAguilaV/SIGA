-- SIGA - Billing Service V3
-- Adds sale_invoices table for POS sale invoices (SAGA step 4)

CREATE TABLE IF NOT EXISTS billing.sale_invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sale_id UUID NOT NULL,
    store_id UUID NOT NULL,
    user_id UUID,
    total NUMERIC(12, 2) NOT NULL,
    items JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sale_invoices_sale_id ON billing.sale_invoices(sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_invoices_store_id ON billing.sale_invoices(store_id);
