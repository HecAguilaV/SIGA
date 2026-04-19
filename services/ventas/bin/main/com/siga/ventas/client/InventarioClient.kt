package com.siga.ventas.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "siga-inventario")
interface InventarioClient {
    
    // El servicio de inventario expondrá esto, y lo validaremos con TDD.
    @GetMapping("/api/inventario/productos/validar-stock")
    fun validarStock(@RequestHeader("X-Tenant-Id") tenantId: String, sku: String, cantidad: Int): Boolean
}
