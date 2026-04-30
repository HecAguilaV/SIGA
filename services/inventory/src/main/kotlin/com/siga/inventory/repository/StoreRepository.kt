package com.siga.inventory.repository

import com.siga.inventory.entity.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for stores/locations.
 */
@Repository
interface StoreRepository : JpaRepository<Store, UUID> {
    fun findByCommercialUserId(userId: UUID): List<Store>
}
