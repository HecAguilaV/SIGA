package com.siga.sales.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "siga-inventory")
interface InventoryClient {
    
    @GetMapping("/api/products/validate-stock")
    fun validateStock(@RequestHeader("X-Tenant-Id") tenantId: String, sku: String, quantity: Int): Boolean
}
