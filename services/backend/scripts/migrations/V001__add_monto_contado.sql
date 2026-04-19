-- =============================================================
-- V001: Agregar campo monto_contado a turnos_caja
-- Permite registrar el monto contado fisicamente por el cajero
-- al cerrar caja, para detectar descuadres automaticamente.
-- =============================================================

ALTER TABLE siga_saas.turnos_caja
ADD COLUMN IF NOT EXISTS monto_contado NUMERIC(10, 2);

COMMENT ON COLUMN siga_saas.turnos_caja.monto_contado
IS 'Monto contado fisicamente por el cajero al cerrar caja. La diferencia con monto_final indica descuadre.';
