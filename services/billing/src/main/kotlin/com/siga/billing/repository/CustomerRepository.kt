package com.siga.billing.repository

import com.siga.billing.entity.CustomerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA Repository for Customer.
 * This is an infrastructure detail, used by CustomerJpaAdapter.
 */
@Repository
interface CustomerRepository : JpaRepository<CustomerEntity, java.util.UUID> {
    fun findByEmail(email: String): CustomerEntity?
}
