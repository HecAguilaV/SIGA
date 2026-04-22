package com.siga.inventory.controller

import com.siga.inventory.entity.Category
import com.siga.inventory.repository.CategoryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage product categories.
 */
@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryRepository: CategoryRepository
) {
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Int): ResponseEntity<Category> {
        val category = categoryRepository.findById(id)
        return if (category.isPresent) {
            ResponseEntity.ok(category.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createCategory(@RequestBody category: Category): ResponseEntity<Category> {
        return ResponseEntity.ok(categoryRepository.save(category))
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: Int, @RequestBody category: Category): ResponseEntity<Category> {
        return if (categoryRepository.existsById(id)) {
            category.id = id
            ResponseEntity.ok(categoryRepository.save(category))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
