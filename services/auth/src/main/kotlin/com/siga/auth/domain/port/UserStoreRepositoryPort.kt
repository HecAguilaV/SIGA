package com.siga.auth.domain.port

import com.siga.auth.domain.model.UserStore
import java.util.UUID

/**
 * Port for UserStore persistence (hexagonal architecture).
 */
interface UserStoreRepositoryPort {
    fun findByUserId(userId: UUID): List<UserStore>
    fun findByStoreId(storeId: UUID): List<UserStore>
    fun save(userStore: UserStore): UserStore
}
