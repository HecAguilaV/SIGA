-- ============================================
-- Script de Migración: Funciones de Respaldo IA (Circuit Breaker)
-- ============================================
-- Estas funciones están diseñadas para ser llamadas cuando la API de IA falla.
-- Proporcionan datos críticos de negocio de forma determinista y rápida.

-- --------------------------------------------
-- 1. FUNCIÓN: Resumen Diario Operativo
-- --------------------------------------------
-- Retorna un JSON con las métricas clave del día actual para un local específico.
CREATE OR REPLACE FUNCTION siga_saas.fn_ai_fallback_resumen_dia(
    p_local_id INTEGER
) 
RETURNS JSON 
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_ventas DECIMAL(12,2);
    v_cantidad_ventas INTEGER;
    v_producto_top VARCHAR(200);
    v_ticket_promedio DECIMAL(10,2);
    v_resultado JSON;
BEGIN
    -- 1. Calcular Total Vendido y Cantidad de Transacciones Hoy
    SELECT 
        COALESCE(SUM(total), 0), 
        COUNT(*)
    INTO 
        v_total_ventas, 
        v_cantidad_ventas
    FROM siga_saas.VENTAS
    WHERE local_id = p_local_id
      AND DATE(fecha) = CURRENT_DATE
      AND estado = 'COMPLETADA';

    -- 2. Calcular Ticket Promedio
    IF v_cantidad_ventas > 0 THEN
        v_ticket_promedio := v_total_ventas / v_cantidad_ventas;
    ELSE
        v_ticket_promedio := 0;
    END IF;

    -- 3. Encontrar Producto Más Vendido Hoy
    SELECT p.nombre
    INTO v_producto_top
    FROM siga_saas.DETALLES_VENTA dv
    JOIN siga_saas.VENTAS v ON v.id = dv.venta_id
    JOIN siga_saas.PRODUCTOS p ON p.id = dv.producto_id
    WHERE v.local_id = p_local_id
      AND DATE(v.fecha) = CURRENT_DATE
      AND v.estado = 'COMPLETADA'
    GROUP BY p.id, p.nombre
    ORDER BY SUM(dv.cantidad) DESC
    LIMIT 1;

    -- 4. Construir Respuesta JSON
    v_resultado := json_build_object(
        'fecha', CURRENT_DATE,
        'local_id', p_local_id,
        'resumen', json_build_object(
            'total_vendido', v_total_ventas,
            'cantidad_transacciones', v_cantidad_ventas,
            'ticket_promedio', ROUND(v_ticket_promedio, 2),
            'producto_estrella', COALESCE(v_producto_top, 'Sin ventas')
        ),
        'mensaje', 'Datos generados por SafeMode (PL/pgSQL) debido a indisponibilidad del Asistente.'
    );

    RETURN v_resultado;
END;
$$;

COMMENT ON FUNCTION siga_saas.fn_ai_fallback_resumen_dia IS 'Retorna resumen de ventas del día actual. Uso: Fallback cuando falla IA.';

-- --------------------------------------------
-- 2. FUNCIÓN: Verificación de Stock Crítico
-- --------------------------------------------
-- Retorna una tabla con productos bajo el nivel mínimo.
CREATE OR REPLACE FUNCTION siga_saas.fn_ai_fallback_alertas_stock(
    p_local_id INTEGER
) 
RETURNS TABLE (
    producto VARCHAR,
    stock_actual INTEGER,
    stock_minimo INTEGER,
    diferencia INTEGER
) 
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.nombre::VARCHAR,
        s.cantidad,
        s.cantidad_minima,
        (s.cantidad_minima - s.cantidad) as diferencia
    FROM siga_saas.STOCK s
    JOIN siga_saas.PRODUCTOS p ON p.id = s.producto_id
    WHERE s.local_id = p_local_id
      AND s.cantidad <= s.cantidad_minima
      AND p.activo = true
    ORDER BY (s.cantidad_minima - s.cantidad) DESC;
END;
$$;

COMMENT ON FUNCTION siga_saas.fn_ai_fallback_alertas_stock IS 'Retorna lista de productos con stock crítico. Uso: Fallback cuando falla IA.';
