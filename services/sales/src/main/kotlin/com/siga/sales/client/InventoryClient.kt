package com.siga.sales.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "siga-inventory", url = "\${siga-inventory.url:}")
interface InventoryClient {
    
    @GetMapping("/api/products/validate-stock")
    fun validateStock(
        @RequestHeader("X-Tenant-Id") tenantId: String,
        @RequestParam("sku") sku: String,
        @RequestParam("quantity") quantity: Int
    ): Boolean
}
