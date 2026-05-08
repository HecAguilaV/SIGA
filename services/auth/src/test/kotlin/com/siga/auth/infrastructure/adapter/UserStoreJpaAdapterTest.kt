package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserRole
import com.siga.auth.domain.model.UserStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.util.*

/**
 * Integration test for [UserStoreJpaAdapter].
 * Verifies UserStore persistence through the hexagonal port with H2.
 * Uses embedded composite key (userId + storeId).
 * 
 * Note: User must be created first because of FK constraint.
 */
@SpringBootTest
@ActiveProfiles("test")
class UserStoreJpaAdapterTest @Autowired constructor(
    private val userStoreAdapter: UserStoreJpaAdapter,
    private val userAdapter: UserJpaAdapter  // Need this to create users for FK
) {

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        // Create a valid user first for FK constraints
        testUser = userAdapter.save(
            User(
                id = null,
                email = "userstore_test_${UUID.randomUUID()}@test.com",
                passwordHash = "hash123",
                firstName = "UserStore",
                lastName = "Test",
                role = UserRole.OPERATOR,
                isActive = true
            )
        )
        assertNotNull(testUser.id)
    }

    @Test
    fun `save and find by userId`() {
        val userId = testUser.id!!
        val storeId = UUID.randomUUID()

        val userStore = UserStore(
            userId = userId,
            storeId = storeId,
            assignedAt = Instant.now()
        )

        val saved = userStoreAdapter.save(userStore)
        assertEquals(userId, saved.userId)
        assertEquals(storeId, saved.storeId)

        val foundByUserId = userStoreAdapter.findByUserId(userId)
        assertTrue(foundByUserId.isNotEmpty())
        assertTrue(foundByUserId.any { it.storeId == storeId })
    }

    @Test
    fun `findById_StoreId - find by storeId`() {
        val userId = testUser.id!!
        val storeId = UUID.randomUUID()

        val userStore = UserStore(
            userId = userId,
            storeId = storeId,
            assignedAt = Instant.now()
        )

        userStoreAdapter.save(userStore)

        val foundByStoreId = userStoreAdapter.findByStoreId(storeId)
        assertTrue(foundByStoreId.isNotEmpty())
        assertTrue(foundByStoreId.any { it.userId == userId })
    }

    @Test
    fun `findByUserId returns empty list when no stores for user`() {
        val found = userStoreAdapter.findByUserId(UUID.randomUUID())
        assertTrue(found.isEmpty())
    }

    @Test
    fun `findByStoreId returns empty list when no users for store`() {
        val found = userStoreAdapter.findByStoreId(UUID.randomUUID())
        assertTrue(found.isEmpty())
    }

    @Test
    fun `save multiple stores for same user`() {
        val userId = testUser.id!!
        val storeId1 = UUID.randomUUID()
        val storeId2 = UUID.randomUUID()
        val storeId3 = UUID.randomUUID()

        val userStore1 = UserStore(userId = userId, storeId = storeId1, assignedAt = Instant.now())
        val userStore2 = UserStore(userId = userId, storeId = storeId2, assignedAt = Instant.now())
        val userStore3 = UserStore(userId = userId, storeId = storeId3, assignedAt = Instant.now())

        userStoreAdapter.save(userStore1)
        userStoreAdapter.save(userStore2)
        userStoreAdapter.save(userStore3)

        val found = userStoreAdapter.findByUserId(userId)
        assertTrue(found.size >= 3)
        assertTrue(found.any { it.storeId == storeId1 })
        assertTrue(found.any { it.storeId == storeId2 })
        assertTrue(found.any { it.storeId == storeId3 })
    }

    @Test
    fun `save different users to same store`() {
        // Create a second user
        val user2 = userAdapter.save(
            User(
                id = null,
                email = "user2_${UUID.randomUUID()}@test.com",
                passwordHash = "hash456",
                firstName = "User2",
                role = UserRole.CASHIER,
                isActive = true
            )
        )

        val storeId = UUID.randomUUID()
        val assignment1 = UserStore(userId = testUser.id!!, storeId = storeId, assignedAt = Instant.now())
        val assignment2 = UserStore(userId = user2.id!!, storeId = storeId, assignedAt = Instant.now())

        userStoreAdapter.save(assignment1)
        userStoreAdapter.save(assignment2)

        val byStore = userStoreAdapter.findByStoreId(storeId)
        assertEquals(2, byStore.size)
        assertTrue(byStore.any { it.userId == testUser.id })
        assertTrue(byStore.any { it.userId == user2.id })
    }
}
