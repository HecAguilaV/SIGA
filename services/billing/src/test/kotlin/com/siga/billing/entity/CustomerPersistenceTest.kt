package com.siga.billing.entity

import com.siga.billing.repository.CustomerRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@DataJpaTest
@ActiveProfiles("test")
class CustomerPersistenceTest @Autowired constructor(
    private val customerRepository: CustomerRepository
) {

    @Test
    fun `given new customer when save then id should be UUID`() {
        val customer = Customer(
            email = "test@siga.com",
            passwordHash = "hashed",
            name = "Test Customer"
        )

        val saved = customerRepository.save(customer)

        assertNotNull(saved.id)
        // This will fail to compile or fail at runtime because id is currently Int
        assertTrue(saved.id is UUID)
    }
}
