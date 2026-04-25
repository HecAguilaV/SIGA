package com.siga.auth.repository

import com.siga.auth.entity.UserStore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for user-store assignments.
 */
@Repository
interface UserStoreRepository : JpaRepository<UserStore, Int> {
    // Usar prefijo "id_" porque storeId/userId están en el EmbeddedId
    fun findById_UserId(userId: Int): List<UserStore>
    fun findById_StoreId(storeId: Int): List<UserStore>
}
