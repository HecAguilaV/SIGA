package com.example.sigaapp

import com.example.sigaapp.util.BusinessLogic
import org.junit.Test
import org.junit.Assert.*

class BusinessLogicTest {

    @Test
    fun calcularMargen_esCorrecto() {
        // Caso: Costo 50, Precio 100 -> Margen 50%
        val margen = BusinessLogic.calcularMargen(50.0, 100.0)
        assertEquals(50.0, margen, 0.01)
    }

    @Test
    fun calcularMargen_datosInvalidos_retornaCero() {
        // Caso: Costo 0
        val margen = BusinessLogic.calcularMargen(0.0, 100.0)
        assertEquals(0.0, margen, 0.01)
    }

    @Test
    fun esQuiebreStock_detectaBajoStock() {
        // Caso: Cantidad 5, Mínimo 10 -> Verdadero (Alerta)
        assertTrue(BusinessLogic.esQuiebreStock(5, 10))
    }

    @Test
    fun esQuiebreStock_stockSuficiente_falso() {
        // Caso: Cantidad 15, Mínimo 10 -> Falso (Stock OK)
        assertFalse(BusinessLogic.esQuiebreStock(15, 10))
    }
}
