package com.siga.auth.repository

import com.siga.auth.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for customers (business owners).
 */
@Repository
interface CustomerRepository : JpaRepository<Customer, Int> {
    fun findByEmail(email: String): Customer?
    fun findByVerificationToken(token: String): Customer?
    fun existsByEmail(email: String): Boolean
}
