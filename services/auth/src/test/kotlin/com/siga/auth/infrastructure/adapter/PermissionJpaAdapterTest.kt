package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.Permission
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

/**
 * Integration test for [PermissionJpaAdapter].
 * Verifies Permission persistence through the hexagonal port with H2.
 * Uses Int ID (IDENTITY strategy).
 */
@SpringBootTest
@ActiveProfiles("test")
class PermissionJpaAdapterTest @Autowired constructor(
    private val adapter: PermissionJpaAdapter
) {

    @Test
    fun `save and find by id`() {
        val uniqueName = "perm_save_${UUID.randomUUID()}"
        val permission = Permission(
            id = null,
            code = uniqueName,
            name = uniqueName,
            description = "Test permission for save and find",
            category = "TEST",
            isActive = true
        )

        val saved = adapter.save(permission)
        assertNotNull(saved.id)
        assertEquals(uniqueName, saved.name)
        assertEquals("Test permission for save and find", saved.description)

        val found = adapter.findById(saved.id!!)
        assertNotNull(found)
        assertEquals(uniqueName, found?.name)
    }

    @Test
    fun `findById returns null when permission does not exist`() {
        val found = adapter.findById(999999)
        assertNull(found)
    }

    @Test
    fun `findByName finds permission by name`() {
        val uniqueName = "find_by_name_${UUID.randomUUID()}"
        val permission = Permission(
            id = null,
            code = uniqueName,
            name = uniqueName,
            description = null,
            category = "TEST",
            isActive = true
        )
        val saved = adapter.save(permission)

        val found = adapter.findByName(uniqueName)
        assertNotNull(found)
        assertEquals(saved.id, found?.id)
        assertEquals(uniqueName, found?.name)
    }

    @Test
    fun `findAll returns all permissions`() {
        val initialCount = adapter.findAll().size

        val uniqueName1 = "perm1_${UUID.randomUUID()}"
        val uniqueName2 = "perm2_${UUID.randomUUID()}"
        
        val permission1 = Permission(
            id = null,
            code = uniqueName1,
            name = uniqueName1,
            description = null,
            category = "TEST",
            isActive = true
        )
        val permission2 = Permission(
            id = null,
            code = uniqueName2,
            name = uniqueName2,
            description = "Desc 2",
            category = "TEST",
            isActive = true
        )
        adapter.save(permission1)
        adapter.save(permission2)

        val allPermissions = adapter.findAll()
        assertTrue(allPermissions.size >= initialCount + 2)
        assertTrue(allPermissions.any { it.name == uniqueName1 })
        assertTrue(allPermissions.any { it.name == uniqueName2 })
    }

    @Test
    fun `save with description null`() {
        val uniqueName = "null_desc_${UUID.randomUUID()}"
        val permission = Permission(
            id = null,
            code = uniqueName,
            name = uniqueName,
            description = null,
            category = "TEST",
            isActive = true
        )

        val saved = adapter.save(permission)
        assertNotNull(saved.id)
        assertNull(saved.description)

        val found = adapter.findById(saved.id!!)
        assertNotNull(found)
        assertNull(found?.description)
    }

    @Test
    fun `update permission by saving with same id`() {
        val uniqueName = "original_perm_${UUID.randomUUID()}"
        val permission = Permission(
            id = null,
            code = uniqueName,
            name = uniqueName,
            description = "Original description",
            category = "TEST",
            isActive = true
        )
        val saved = adapter.save(permission)
        val savedId = saved.id!!

        val updated = saved.copy(
            description = "Updated description",
            isActive = false
        )
        adapter.save(updated)

        val found = adapter.findById(savedId)
        assertNotNull(found)
        assertEquals("Updated description", found?.description)
        assertFalse(found?.isActive!!)
    }
}
