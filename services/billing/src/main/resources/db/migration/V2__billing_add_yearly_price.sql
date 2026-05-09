-- SIGA - Billing Service V2
-- Adds yearly_price column to plans table (was missing in V1)

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'billing' AND table_name = 'plans' AND column_name = 'yearly_price'
    ) THEN
        ALTER TABLE billing.plans ADD COLUMN yearly_price NUMERIC(10, 2);
    END IF;
END $$;
