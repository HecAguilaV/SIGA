package com.siga.inventory.controller

import com.siga.inventory.entity.Product
import com.siga.inventory.repository.ProductRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage products.
 */
@RestController
@RequestMapping("/api/v1/inventory/products")
class ProductController(
    private val productRepository: ProductRepository
) {
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: UUID): ResponseEntity<Product> {
        val product = productRepository.findById(id)
        return if (product.isPresent) {
            ResponseEntity.ok(product.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/company/{companyId}")
    fun getProductsByCompany(@PathVariable companyId: UUID): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productRepository.findByCommercialUserId(companyId))
    }

    @GetMapping("/category/{categoryId}")
    fun getProductsByCategory(@PathVariable categoryId: UUID): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productRepository.findByCategoryId(categoryId))
    }

    @GetMapping("/barcode/{barcode}")
    fun getProductByBarcode(@PathVariable barcode: String): ResponseEntity<Product> {
        val product = productRepository.findByBarcode(barcode)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createProduct(@RequestBody product: Product): ResponseEntity<Product> {
        return ResponseEntity.status(201).body(productRepository.save(product))
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: UUID, @RequestBody product: Product): ResponseEntity<Product> {
        return if (productRepository.existsById(id)) {
            product.id = id
            ResponseEntity.ok(productRepository.save(product))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
