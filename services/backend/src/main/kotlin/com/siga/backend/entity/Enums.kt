package com.siga.backend.entity

/**
 * Estados de una suscripcion al servicio SaaS.
 */
enum class EstadoSuscripcion {
    ACTIVA,
    SUSPENDIDA,
    CANCELADA,
    VENCIDA
}

/**
 * Periodo de facturacion de una suscripcion.
 */
enum class PeriodoSuscripcion {
    MENSUAL,
    ANUAL
}

/**
 * Estados de un pago de suscripcion.
 */
enum class EstadoPago {
    PENDIENTE,
    COMPLETADO,
    FALLIDO,
    REEMBOLSADO
}
