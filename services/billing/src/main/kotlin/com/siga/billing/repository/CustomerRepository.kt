package com.siga.billing.repository

import com.siga.billing.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for commercial customers.
 */
@Repository
interface CustomerRepository : JpaRepository<Customer, UUID> {
    fun findByEmail(email: String): Customer?
    fun existsByEmail(email: String): Boolean
}
