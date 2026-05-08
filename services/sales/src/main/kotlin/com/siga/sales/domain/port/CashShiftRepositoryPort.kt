package com.siga.sales.domain.port

import com.siga.sales.domain.model.CashShift
import java.util.UUID

/**
 * Port for CashShift persistence.
 */
interface CashShiftRepositoryPort {
    fun findById(id: UUID): CashShift?
    fun save(shift: CashShift): CashShift
    fun findByStoreId(storeId: UUID): List<CashShift>
    fun findByUserId(userId: UUID): CashShift?
}
