package com.siga.gateway.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/api/v1/dashboard")
class DashboardController(
    private val webClientBuilder: WebClient.Builder
) {
    private val webClient = webClientBuilder.build()

    @GetMapping("/insights")
    fun getDashboardInsights(): Mono<ResponseEntity<Map<String, Any>>> {
        val salesSummary = webClient.get()
            .uri("lb://siga-sales/api/v1/sales/summary/daily")
            .retrieve()
            .bodyToMono(List::class.java)
            .onErrorReturn(emptyList<Any>())

        val inventoryStock = webClient.get()
            .uri("lb://siga-inventory/api/v1/inventory/stock/consolidated?size=5")
            .retrieve()
            .bodyToMono(Map::class.java)
            .onErrorReturn(emptyMap<Any, Any>())

        return Mono.zip(salesSummary, inventoryStock).map { tuple ->
            val sales = tuple.t1
            val inventory = tuple.t2
            
            val response = mapOf(
                "trends" to sales,
                "lowStock" to (inventory["content"] ?: emptyList<Any>()),
                "insights" to listOf(
                    mapOf("id" to "1", "title" to "Total Productos", "value" to (inventory["totalElements"] ?: 0), "icon" to "package", "variant" to "primary"),
                    mapOf("id" to "2", "title" to "Ventas Hoy", "value" to (sales.lastOrNull() ?: 0), "icon" to "payments", "variant" to "info")
                ),
                "anomalies" to emptyList<Any>()
            )
            ResponseEntity.ok(response)
        }
    }
}
