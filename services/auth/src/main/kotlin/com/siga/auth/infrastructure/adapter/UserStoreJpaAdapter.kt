package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.UserStore
import com.siga.auth.domain.port.UserStoreRepositoryPort
import com.siga.auth.infrastructure.mapper.UserStoreMapper
import com.siga.auth.repository.UserStoreRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing UserStoreRepositoryPort.
 * Delegates to Spring Data JPA repository + mapper.
 */
@Component
class UserStoreJpaAdapter(
    private val userStoreRepository: UserStoreRepository
) : UserStoreRepositoryPort {

    override fun findByUserId(userId: UUID): List<UserStore> {
        return userStoreRepository.findById_UserId(userId).map { UserStoreMapper.toDomain(it) }
    }

    override fun findByStoreId(storeId: UUID): List<UserStore> {
        return userStoreRepository.findById_StoreId(storeId).map { UserStoreMapper.toDomain(it) }
    }

    override fun save(userStore: UserStore): UserStore {
        val entity = UserStoreMapper.toEntity(userStore)
        val saved = userStoreRepository.save(entity)
        return UserStoreMapper.toDomain(saved)
    }
}
