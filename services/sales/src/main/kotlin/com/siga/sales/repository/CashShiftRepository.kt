package com.siga.sales.repository

import com.siga.sales.entity.CashShiftEntity
import com.siga.sales.entity.ShiftStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for cash shifts (JPA).
 */
@Repository
interface CashShiftRepository : JpaRepository<CashShiftEntity, UUID> {
    fun findByStoreId(storeId: UUID): List<CashShiftEntity>
    fun findByUserId(userId: UUID): CashShiftEntity?
    fun findByStoreIdAndStatus(storeId: UUID, status: ShiftStatus): CashShiftEntity?
}
