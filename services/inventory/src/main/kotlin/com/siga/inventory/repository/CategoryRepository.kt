package com.siga.inventory.repository

import com.siga.inventory.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for categories.
 */
@Repository
interface CategoryRepository : JpaRepository<Category, UUID> {
    fun findByName(name: String): Category?
    fun findByCommercialUserId(userId: UUID): List<Category>
}
