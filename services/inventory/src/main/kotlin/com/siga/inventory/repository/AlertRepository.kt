package com.siga.inventory.repository

import com.siga.inventory.entity.Alert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for inventory [alerts].
 */
@Repository
interface AlertRepository : JpaRepository<Alert, UUID> {
    fun findByStoreId(storeId: UUID): List<Alert>
}
