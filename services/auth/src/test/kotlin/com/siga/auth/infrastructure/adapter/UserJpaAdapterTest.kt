package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

/**
 * Integration test for [UserJpaAdapter].
 * Verifies User persistence through the hexagonal port with H2.
 * 
 * Note: Use id=null for creates (follows real domain flow). 
 * The UUID is returned by save().
 */
@SpringBootTest
@ActiveProfiles("test")
class UserJpaAdapterTest @Autowired constructor(
    private val adapter: UserJpaAdapter
) {

    @Test
    fun `save and find by id`() {
        val uniqueEmail = "savefind_${UUID.randomUUID()}@test.com"
        val user = User(
            id = null,  // Let JPA generate the UUID
            email = uniqueEmail,
            passwordHash = "hash123",
            firstName = "John",
            lastName = "Doe",
            role = UserRole.ADMINISTRATOR,
            isActive = true
        )

        val saved = adapter.save(user)
        assertNotNull(saved.id)  // UUID was generated
        assertEquals(uniqueEmail, saved.email)
        assertEquals("John", saved.firstName)

        val found = adapter.findById(saved.id!!)
        assertNotNull(found)
        assertEquals(uniqueEmail, found?.email)
        assertEquals("John", found?.firstName)
    }

    @Test
    fun `findById returns null when user does not exist`() {
        val found = adapter.findById(UUID.randomUUID())
        assertNull(found)
    }

    @Test
    fun `findByEmail finds user by email`() {
        val uniqueEmail = "unique_email_${UUID.randomUUID()}@test.com"
        val user = User(
            id = null,
            email = uniqueEmail,
            passwordHash = "hash123",
            firstName = "Email",
            lastName = "Test",
            role = UserRole.OPERATOR,
            isActive = true
        )
        val saved = adapter.save(user)

        val found = adapter.findByEmail(uniqueEmail)
        assertNotNull(found)
        assertEquals(saved.id, found?.id)
        assertEquals(uniqueEmail, found?.email)
    }

    @Test
    fun `findByEmail returns null when email does not exist`() {
        val found = adapter.findByEmail("nonexistent_${UUID.randomUUID()}@test.com")
        assertNull(found)
    }

    @Test
    fun `existsByEmail works correctly`() {
        val uniqueEmail = "exists_check_${UUID.randomUUID()}@test.com"

        assertFalse(adapter.existsByEmail(uniqueEmail))

        val user = User(
            id = null,
            email = uniqueEmail,
            passwordHash = "hash123",
            firstName = "Exists Test",
            role = UserRole.OPERATOR,
            isActive = true
        )
        adapter.save(user)

        assertTrue(adapter.existsByEmail(uniqueEmail))
    }

    @Test
    fun `findAll returns all users`() {
        val initialCount = adapter.findAll().size

        val uniqueEmail1 = "user1_${UUID.randomUUID()}@test.com"
        val uniqueEmail2 = "user2_${UUID.randomUUID()}@test.com"

        val user1 = User(
            id = null,
            email = uniqueEmail1,
            passwordHash = "hash1",
            firstName = "User",
            lastName = "One",
            role = UserRole.CASHIER,
            isActive = true
        )
        val user2 = User(
            id = null,
            email = uniqueEmail2,
            passwordHash = "hash2",
            firstName = "User",
            lastName = "Two",
            role = UserRole.ADMINISTRATOR,
            isActive = true
        )
        adapter.save(user1)
        adapter.save(user2)

        val allUsers = adapter.findAll()
        assertTrue(allUsers.size >= initialCount + 2)
        assertTrue(allUsers.any { it.email == uniqueEmail1 })
        assertTrue(allUsers.any { it.email == uniqueEmail2 })
    }

    @Test
    fun `save creates a user with UUID`() {
        val uniqueEmail = "uuid_test_${UUID.randomUUID()}@test.com"
        val user = User(
            id = null,  // null means create
            email = uniqueEmail,
            passwordHash = "hash123",
            firstName = "UUID",
            lastName = "Test",
            role = UserRole.OPERATOR,
            isActive = true
        )

        val saved = adapter.save(user)
        assertNotNull(saved.id)

        // Verify it's a valid UUID
        val uuidFromString = UUID.fromString(saved.id.toString())
        assertEquals(saved.id, uuidFromString)

        val found = adapter.findById(saved.id!!)
        assertNotNull(found)
        assertEquals(saved.id, found?.id)
    }

    @Test
    fun `update user by saving with same id`() {
        val uniqueEmail = "original_${UUID.randomUUID()}@test.com"
        val user = User(
            id = null,
            email = uniqueEmail,
            passwordHash = "original_hash",
            firstName = "Original",
            lastName = "Name",
            role = UserRole.OPERATOR,
            isActive = true
        )
        val saved = adapter.save(user)
        val savedId = saved.id!!

        val updated = saved.copy(
            firstName = "Updated",
            lastName = "NewLastName",
            role = UserRole.ADMINISTRATOR,
            isActive = false
        )
        val savedAfterUpdate = adapter.save(updated)

        val found = adapter.findById(savedId)
        assertNotNull(found)
        assertEquals("Updated", found?.firstName)
        assertEquals("NewLastName", found?.lastName)
        assertEquals(UserRole.ADMINISTRATOR, found?.role)
        assertFalse(found?.isActive!!)
    }
}
