package com.siga.inventario.controller

import com.siga.inventario.entity.Producto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/inventario/productos")
class ProductoController {

    // En GREEN usamos Fake It o listado vacío con tenant_id validation
    @GetMapping
    fun listarProductos(@RequestHeader("X-Tenant-Id", required = false) tenantId: String?): ResponseEntity<List<Producto>> {
        if (tenantId == null) {
            // Requerimos que venga el header o un JWT valido (simulado en header aqui para Gateway Pass-through)
            // Cuando haya Gateway Pass-through, Gateway inyecta X-Tenant-Id.
            return ResponseEntity.status(401).build()
        }
        
        // Retornamos lista vacía o mockeada filtrada por el tenantId
        return ResponseEntity.ok(listOf())
    }
}
