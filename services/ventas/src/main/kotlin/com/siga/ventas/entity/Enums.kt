package com.siga.ventas.entity

/**
 * Estados posibles de una venta.
 */
enum class EstadoVenta {
    COMPLETADA,
    CANCELADA,
    PENDIENTE
}

/**
 * Estados de un turno de caja (apertura/cierre).
 */
enum class EstadoTurno {
    ABIERTO,
    CERRADO
}

/**
 * Estados de una transaccion en el punto de venta.
 */
enum class EstadoTransaccion {
    COMPLETADA,
    CANCELADA,
    REEMBOLSADA
}

/**
 * Estados de una factura del portal comercial.
 * Nota: Factura.kt pertenece a siga_comercial pero es gestionada por el servicio ventas.
 */
enum class EstadoFactura {
    PENDIENTE,
    PAGADA,
    VENCIDA,
    ANULADA
}
