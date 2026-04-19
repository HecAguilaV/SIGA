package com.siga.inventario.entity

/**
 * Tipos de movimiento de stock (Kardex).
 * Cada movimiento registra un cambio en la cantidad de un producto en un local.
 */
enum class TipoMovimiento {
    ENTRADA,
    SALIDA,
    VENTA,
    AJUSTE,
    TRASLADO
}

/**
 * Tipos de alerta generadas automaticamente por el sistema.
 */
enum class TipoAlerta {
    STOCK_BAJO,
    STOCK_AGOTADO,
    VENTA_ALTA,
    MOVIMIENTO_SOSPECHOSO
}
