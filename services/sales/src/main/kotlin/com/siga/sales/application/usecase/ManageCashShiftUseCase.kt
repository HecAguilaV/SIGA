package com.siga.sales.application.usecase

import com.siga.sales.domain.model.CashShift
import com.siga.sales.domain.port.CashShiftRepositoryPort
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

/**
 * Use case for managing cash shift operations.
 */
@Component
class ManageCashShiftUseCase(
    private val cashShiftRepositoryPort: CashShiftRepositoryPort
) {
    fun openShift(storeId: UUID, userId: UUID, initialAmount: java.math.BigDecimal): CashShift {
        val shift = CashShift(
            id = UUID.randomUUID(),
            storeId = storeId,
            userId = userId,
            openedAt = Instant.now(),
            closedAt = null,
            initialAmount = initialAmount,
            finalAmount = null,
            status = com.siga.sales.domain.model.ShiftStatus.OPEN
        )
        return cashShiftRepositoryPort.save(shift)
    }

    fun closeShift(shiftId: UUID, finalAmount: java.math.BigDecimal): CashShift? {
        val shift = cashShiftRepositoryPort.findById(shiftId) ?: return null
        val updatedShift = shift.copy(
            closedAt = Instant.now(),
            finalAmount = finalAmount,
            status = com.siga.sales.domain.model.ShiftStatus.CLOSED
        )
        return cashShiftRepositoryPort.save(updatedShift)
    }

    fun getOpenShiftByUser(userId: UUID): CashShift? {
        return cashShiftRepositoryPort.findByUserId(userId)
    }
}
