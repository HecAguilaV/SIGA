package com.siga.sales.repository

import com.siga.sales.entity.CashShift
import com.siga.sales.entity.ShiftStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for cash shifts.
 */
@Repository
interface CashShiftRepository : JpaRepository<CashShift, UUID> {
    fun findByStoreId(storeId: UUID): List<CashShift>
    fun findByUserId(userId: UUID): CashShift?
    fun findByStoreIdAndStatus(storeId: UUID, status: ShiftStatus): CashShift?
}
