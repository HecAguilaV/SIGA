package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserPermission
import com.siga.auth.domain.model.UserRole
import com.siga.auth.domain.port.PermissionRepositoryPort
import com.siga.auth.domain.model.Permission
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.util.*

/**
 * Integration test for [UserPermissionJpaAdapter].
 * Verifies UserPermission persistence through the hexagonal port with H2.
 * Uses embedded composite key (userId + permissionId).
 *
 * Note: User and Permission must be created first because of FK constraints.
 */
@SpringBootTest
@ActiveProfiles("test")
class UserPermissionJpaAdapterTest @Autowired constructor(
    private val userPermissionAdapter: UserPermissionJpaAdapter,
    private val userAdapter: UserJpaAdapter,
    private val permissionAdapter: PermissionJpaAdapter
) {

    private lateinit var testUser: User
    private lateinit var testPermission: Permission

    @BeforeEach
    fun setUp() {
        // Create a valid user for FK constraints
        testUser = userAdapter.save(
            User(
                id = null,
                email = "userperm_test_${UUID.randomUUID()}@test.com",
                passwordHash = "hash123",
                firstName = "UserPerm",
                lastName = "Test",
                role = UserRole.OPERATOR,
                isActive = true
            )
        )
        assertNotNull(testUser.id)

        // Create a valid permission for FK constraints
        testPermission = permissionAdapter.save(
            Permission(
                id = null,
                code = "TEST_PERM_${UUID.randomUUID()}",
                name = "Test Permission",
                description = "Permission for testing",
                category = "TEST",
                isActive = true
            )
        )
        assertNotNull(testPermission.id)
    }

    @Test
    fun `save and find by userId`() {
        val userId = testUser.id!!
        val permissionId = testPermission.id!!

        val userPermission = UserPermission(
            userId = userId,
            permissionId = permissionId,
            assignedAt = Instant.now(),
            assignedBy = UUID.randomUUID()
        )

        val saved = userPermissionAdapter.save(userPermission)
        assertEquals(userId, saved.userId)
        assertEquals(permissionId, saved.permissionId)

        val foundByUserId = userPermissionAdapter.findByUserId(userId)
        assertTrue(foundByUserId.isNotEmpty())
        assertTrue(foundByUserId.any { it.permissionId == permissionId })
    }

    @Test
    fun `findByPermissionId - find by permissionId`() {
        val userId = testUser.id!!
        val permissionId = testPermission.id!!

        val userPermission = UserPermission(
            userId = userId,
            permissionId = permissionId,
            assignedAt = Instant.now()
        )

        userPermissionAdapter.save(userPermission)

        val foundByPermId = userPermissionAdapter.findByPermissionId(permissionId)
        assertTrue(foundByPermId.isNotEmpty())
        assertTrue(foundByPermId.any { it.userId == userId })
    }

    @Test
    fun `findByUserId returns empty list when no permissions for user`() {
        val found = userPermissionAdapter.findByUserId(UUID.randomUUID())
        assertTrue(found.isEmpty())
    }

    @Test
    fun `findByPermissionId returns empty list when no users for permission`() {
        val found = userPermissionAdapter.findByPermissionId(UUID.randomUUID())
        assertTrue(found.isEmpty())
    }

    @Test
    fun `save multiple permissions for same user`() {
        val userId = testUser.id!!

        // Create additional permissions
        val perm2 = permissionAdapter.save(
            Permission(id = null, code = "TEST_PERM2_${UUID.randomUUID()}", name = "Perm2", category = "TEST")
        )
        val perm3 = permissionAdapter.save(
            Permission(id = null, code = "TEST_PERM3_${UUID.randomUUID()}", name = "Perm3", category = "TEST")
        )

        val up1 = UserPermission(userId = userId, permissionId = perm2.id!!, assignedAt = Instant.now())
        val up2 = UserPermission(userId = userId, permissionId = perm3.id!!, assignedAt = Instant.now())

        userPermissionAdapter.save(up1)
        userPermissionAdapter.save(up2)

        val found = userPermissionAdapter.findByUserId(userId)
        assertTrue(found.size >= 2)
        assertTrue(found.any { it.permissionId == perm2.id })
        assertTrue(found.any { it.permissionId == perm3.id })
    }

    @Test
    fun `existsByUserIdAndPermissionId returns true when exists`() {
        val userId = testUser.id!!
        val permissionId = testPermission.id!!

        val userPermission = UserPermission(
            userId = userId,
            permissionId = permissionId,
            assignedAt = Instant.now()
        )

        userPermissionAdapter.save(userPermission)

        assertTrue(userPermissionAdapter.existsByUserIdAndPermissionId(userId, permissionId))
    }

    @Test
    fun `existsByUserIdAndPermissionId returns false when does not exist`() {
        assertFalse(userPermissionAdapter.existsByUserIdAndPermissionId(UUID.randomUUID(), UUID.randomUUID()))
    }

    @Test
    fun `deleteByUserIdAndPermissionId removes the assignment`() {
        val userId = testUser.id!!
        val permissionId = testPermission.id!!

        val userPermission = UserPermission(
            userId = userId,
            permissionId = permissionId,
            assignedAt = Instant.now()
        )

        userPermissionAdapter.save(userPermission)
        assertTrue(userPermissionAdapter.existsByUserIdAndPermissionId(userId, permissionId))

        userPermissionAdapter.deleteByUserIdAndPermissionId(userId, permissionId)

        assertFalse(userPermissionAdapter.existsByUserIdAndPermissionId(userId, permissionId))
        assertTrue(userPermissionAdapter.findByUserId(userId).isEmpty())
    }
}
