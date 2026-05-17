package com.siga.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableDiscoveryClient
class GatewayApplication {

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("siga-agent") { r ->
                r.path("/api/agent/**")
                    .filters { f -> f.rewritePath("/api/agent/(?<segment>.*)", "/api/agent/\${segment}") }
                    .uri("http://192.168.1.10:8000")
            }
            .route("siga-auth") { r ->
                r.path("/api/auth/**")
                    .filters { f -> f.rewritePath("/api/auth/(?<segment>.*)", "/api/v1/auth/\${segment}") }
                    .uri("lb://siga-auth")
            }
            .route("siga-inventory") { r ->
                r.path("/api/products/**", "/api/stores/**", "/api/inventory/**")
                    .filters { f -> f.rewritePath("/api/(?<service>products|stores|inventory)/(?<segment>.*)", "/api/v1/\${service}/\${segment}") }
                    .uri("lb://siga-inventory")
            }
            .route("siga-sales") { r ->
                r.path("/api/sales/**", "/api/cash-shifts/**")
                    .filters { f -> f.rewritePath("/api/(?<segment>.*)", "/api/v1/\${segment}") }
                    .uri("lb://siga-sales")
            }
            .route("siga-billing") { r ->
                r.path("/api/billing/**", "/api/comercial/**")
                    .filters { f ->
                        f.rewritePath("/api/billing/(?<segment>.*)", "/api/v1/billing/\${segment}")
                            .rewritePath("/api/comercial/(?<segment>.*)", "/api/v1/billing/\${segment}")
                    }
                    .uri("lb://siga-billing")
            }
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}
