package com.siga.inventory.repository

import com.siga.inventory.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for categories.
 */
@Repository
interface CategoryRepository : JpaRepository<Category, Int> {
    fun findByCommercialUserId(userId: Int): List<Category>
}
