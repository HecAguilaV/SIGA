package com.siga.auth.infrastructure.mapper

import com.siga.auth.domain.model.UserStore as DomainUserStore
import com.siga.auth.entity.UserStore as UserStoreEntity
import com.siga.auth.entity.UserStoreId

/**
 * Mapper between UserStore JPA entity (with @EmbeddedId) and UserStore domain model.
 * Handles:
 * - Embedded UserStoreId (userId/storeId) ↔ top-level fields
 * - Nullable UUIDs from entity → non-nullable UUIDs in domain (throws if null)
 */
object UserStoreMapper {

    fun toDomain(entity: UserStoreEntity): DomainUserStore {
        return DomainUserStore(
            userId = entity.id.userId ?: throw IllegalStateException("UserStore userId cannot be null"),
            storeId = entity.id.storeId ?: throw IllegalStateException("UserStore storeId cannot be null"),
            assignedAt = entity.assignedAt
        )
    }

    fun toEntity(domain: DomainUserStore): UserStoreEntity {
        return UserStoreEntity(
            id = UserStoreId(userId = domain.userId, storeId = domain.storeId),
            assignedAt = domain.assignedAt
        )
    }
}
