package com.siga.inventory.repository

import com.siga.inventory.entity.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for stores/locations.
 */
@Repository
interface StoreRepository : JpaRepository<Store, Int> {
    fun findByCommercialUserId(userId: Int): List<Store>
}
