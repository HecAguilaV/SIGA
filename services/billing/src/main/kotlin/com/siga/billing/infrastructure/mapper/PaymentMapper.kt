package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.model.PaymentStatus
import com.siga.billing.entity.PaymentEntity
import com.siga.billing.entity.PaymentStatus as EntityPaymentStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

object PaymentMapper {
    fun toDomain(entity: PaymentEntity): Payment {
        return Payment(
            id = entity.id ?: UUID.randomUUID(),
            subscriptionId = entity.subscriptionId,
            customerId = entity.customerId,
            amount = entity.amount,
            paymentMethod = entity.paymentMethod,
            status = mapStatusToDomain(entity.status),
            reference = entity.reference,
            paidAt = entity.paidAt
        )
    }

    fun toEntity(model: Payment): PaymentEntity {
        return PaymentEntity(
            id = model.id,
            subscriptionId = model.subscriptionId,
            customerId = model.customerId,
            amount = model.amount,
            paymentMethod = model.paymentMethod,
            status = mapStatusToEntity(model.status),
            reference = model.reference,
            paidAt = model.paidAt
        )
    }

    private fun mapStatusToDomain(status: EntityPaymentStatus): PaymentStatus {
        return PaymentStatus.valueOf(status.name)
    }

    private fun mapStatusToEntity(status: PaymentStatus): EntityPaymentStatus {
        return EntityPaymentStatus.valueOf(status.name)
    }
}
