package com.siga.billing

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class BillingApplication

fun main(args: Array<String>) {
    SpringApplication.run(BillingApplication::class.java, *args)
}