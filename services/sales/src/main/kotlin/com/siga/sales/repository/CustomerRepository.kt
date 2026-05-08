package com.siga.sales.repository

import com.siga.sales.entity.CustomerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for SME customers (JPA).
 */
@Repository
interface CustomerRepository : JpaRepository<CustomerEntity, UUID> {
    fun findByTaxId(taxId: String): CustomerEntity?
}
