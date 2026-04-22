package com.siga.auth.repository

import com.siga.auth.entity.UserStore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for user-store assignments.
 */
@Repository
interface UserStoreRepository : JpaRepository<UserStore, Int> {
    fun findByUserId(userId: Int): List<UserStore>
    fun findByStoreId(storeId: Int): List<UserStore>
}
