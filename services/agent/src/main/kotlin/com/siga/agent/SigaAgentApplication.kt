package com.siga.agent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class SigaAgentApplication

fun main(args: Array<String>) {
    runApplication<SigaAgentApplication>(*args)
}
