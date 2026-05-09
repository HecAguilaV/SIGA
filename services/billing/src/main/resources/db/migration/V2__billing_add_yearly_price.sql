-- SIGA - Billing Service V2
-- Adds yearly_price column to plans table (was missing in V1)

ALTER TABLE billing.plans
    ADD COLUMN yearly_price NUMERIC(10, 2);
