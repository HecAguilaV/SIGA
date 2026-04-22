package com.siga.sales.repository

import com.siga.sales.entity.CashShift
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for cash shifts.
 */
@Repository
interface CashShiftRepository : JpaRepository<CashShift, Int> {
    fun findByStoreId(storeId: Int): List<CashShift>
    fun findByUserId(userId: Int): CashShift?
    fun findByStoreIdAndStatus(storeId: Int, status: String): CashShift?
}
