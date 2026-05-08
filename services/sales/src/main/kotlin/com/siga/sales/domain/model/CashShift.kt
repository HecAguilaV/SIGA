package com.siga.sales.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Represents an open/closed cash register shift at a store.
 *
 * A shift tracks the opening and closing balances, allowing
 * reconciliation of physical cash against recorded transactions.
 */
data class CashShift(
    val id: UUID,
    val storeId: UUID,
    val userId: UUID,
    val openedAt: Instant,
    val closedAt: Instant?,
    val initialAmount: BigDecimal,
    val finalAmount: BigDecimal?,
    val status: ShiftStatus
)
