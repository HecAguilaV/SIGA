package com.siga.inventory.controller

import com.siga.inventory.entity.Category
import com.siga.inventory.repository.CategoryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage product categories.
 */
@RestController
@RequestMapping("/api/v1/inventory/categories")
class CategoryController(
    private val categoryRepository: CategoryRepository
) {
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: UUID): ResponseEntity<Category> {
        val category = categoryRepository.findById(id)
        return if (category.isPresent) {
            ResponseEntity.ok(category.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/company/{companyId}")
    fun getCategoriesByCompany(@PathVariable companyId: UUID): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryRepository.findByCommercialUserId(companyId))
    }

    @PostMapping
    fun createCategory(@RequestBody category: Category): ResponseEntity<Category> {
        return ResponseEntity.status(201).body(categoryRepository.save(category))
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: UUID, @RequestBody category: Category): ResponseEntity<Category> {
        return if (categoryRepository.existsById(id)) {
            category.id = id
            ResponseEntity.ok(categoryRepository.save(category))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
