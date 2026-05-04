package com.siga.billing.infrastructure.adapter

import com.siga.billing.entity.CustomerEntity
import com.siga.billing.repository.CustomerRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@DataJpaTest
@ActiveProfiles("test")
class CustomerPersistenceTest @Autowired constructor(
    private val customerRepository: CustomerRepository
) {

    @Test
    fun `given new customer entity when save then id should be UUID`() {
        val customer = CustomerEntity(
            email = "test@siga.com",
            passwordHash = "hashed",
            name = "Test Customer",
            lastName = null,
            taxId = null,
            phone = null,
            companyName = null,
            isActive = true,
            isOnTrial = false,
            trialStartAt = null,
            trialEndAt = null,
            role = "customer",
            planId = null
        )

        val saved = customerRepository.save(customer)

        assertNotNull(saved.id)
        assertTrue(saved.id is UUID)
    }
}
