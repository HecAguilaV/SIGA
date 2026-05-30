package com.siga.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

/**
 * Entry point for the Notification microservice.
 *
 * Consumer-only service that listens for email events on the `email-events`
 * Kafka topic, renders HTML templates, and sends emails via JavaMailSender.
 */
@SpringBootApplication
@EnableDiscoveryClient
class NotificationApplication

fun main(args: Array<String>) {
    runApplication<NotificationApplication>(*args)
}
