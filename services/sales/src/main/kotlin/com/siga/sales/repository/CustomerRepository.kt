package com.siga.sales.repository

import com.siga.sales.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for SME customers (end-clients of our PyME users).
 */
@Repository
interface CustomerRepository : JpaRepository<Customer, UUID> {
    fun findByTaxId(taxId: String): Customer?
}
