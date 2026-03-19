package com.example.sigaapp.util

object BusinessLogic {
    /**
     * Calcula el margen de ganancia de un producto.
     * @param costo Costo unitario del producto.
     * @param precio Precio de venta.
     * @return El porcentaje de margen (0-100) o 0 si los datos son inválidos.
     */
    fun calcularMargen(costo: Double, precio: Double): Double {
        if (costo <= 0 || precio <= 0) return 0.0
        val ganancia = precio - costo
        return (ganancia / precio) * 100
    }

    /**
     * Determina si un producto está en quiebre de stock crítico.
     */
    fun esQuiebreStock(cantidad: Int, minimo: Int): Boolean {
        return cantidad <= minimo
    }
}
