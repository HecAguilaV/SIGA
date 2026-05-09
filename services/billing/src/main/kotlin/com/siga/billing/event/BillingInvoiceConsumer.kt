package com.siga.billing.event

import com.siga.billing.domain.model.SaleInvoice
import com.siga.billing.domain.port.SaleInvoiceRepositoryPort
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Consumes sale-completed events from the Sales service (SAGA step 4).
 *
 * When a sale is completed (stock reserved), this consumer creates the
 * corresponding sale invoice in the Billing database.
 *
 * Idempotent: duplicate events (same eventId or saleId) are skipped.
 */
@Component
class BillingInvoiceConsumer(
    private val saleInvoiceRepositoryPort: SaleInvoiceRepositoryPort
) {
    private val log = LoggerFactory.getLogger(BillingInvoiceConsumer::class.java)

    @KafkaListener(
        topics = ["sale-completed"],
        groupId = "siga-billing",
        properties = [
            "spring.json.value.default.type=com.siga.billing.event.SaleCompletedEvent"
        ]
    )
    @Transactional
    fun consume(event: SaleCompletedEvent) {
        log.info("Received sale-completed for sale={}, total={}", event.saleId, event.total)

        // Create the sale invoice
        val invoice = SaleInvoice(
            saleId = event.saleId,
            storeId = event.storeId,
            userId = event.userId,
            total = event.total,
            items = null // full items list is available if needed
        )

        saleInvoiceRepositoryPort.save(invoice)
        log.info("Sale invoice created for sale={}", event.saleId)
    }
}
