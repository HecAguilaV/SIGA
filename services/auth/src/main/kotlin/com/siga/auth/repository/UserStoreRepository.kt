package com.siga.auth.repository

import com.siga.auth.entity.UserStore
import com.siga.auth.entity.UserStoreId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for user-store assignments.
 */
@Repository
interface UserStoreRepository : JpaRepository<UserStore, UserStoreId> {
    // Usar prefijo "id_" porque storeId/userId están en el EmbeddedId
    fun findById_UserId(userId: UUID): List<UserStore>
    fun findById_StoreId(storeId: UUID): List<UserStore>
}
