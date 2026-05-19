package com.siga.inventory.controller

import com.siga.inventory.application.usecase.CreateProductRequest
import com.siga.inventory.application.usecase.CreateProductResponse
import com.siga.inventory.application.usecase.CreateProductUseCase
import com.siga.inventory.application.usecase.SearchProductsUseCase
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.entity.Product
import com.siga.inventory.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage products.
 *
 * Existing endpoints use ProductRepository (JPA) directly.
 * New endpoints (Phase 4.2) inject use cases + port for hexagonal architecture.
 * POST /products was replaced to use [CreateProductUseCase].
 */
@RestController
@RequestMapping("/api/v1/inventory/products")
class ProductController(
    private val productRepository: ProductRepository,
    private val createProductUseCase: CreateProductUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val productRepositoryPort: ProductRepositoryPort
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

    /**
     * POST /api/v1/inventory/products
     * Creates a new product with optional auto-SKU generation and fuzzy duplicate detection.
     *
     * Replaced the previous direct JPA save with [CreateProductUseCase] (Phase 4.2).
     *
     * Flow:
     * 1. Pre-check for fuzzy duplicates via [ProductRepositoryPort.findByNameLike]
     * 2. If duplicates found and force=false → 409 Conflict
     * 3. If force=true or no duplicates → delegate to use case → 201 Created
     */
    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): ResponseEntity<*> {
        // Pre-check for duplicates before delegating to use case
        if (!request.force && !request.name.isNullOrBlank()) {
            val duplicates = productRepositoryPort.findByNameLike(request.name)
            if (duplicates.isNotEmpty()) {
                val dup = duplicates.first()
                return ResponseEntity.status(HttpStatus.CONFLICT).body<Map<String, Any>>(
                    mapOf(
                        "error" to "DUPLICATE_DETECTED",
                        "message" to "Similar product found",
                        "existingProduct" to mapOf(
                            "productId" to dup.id,
                            "name" to dup.name,
                            "sku" to (dup.sku ?: "")
                        )
                    )
                )
            }
        }

        val tenantId = 1L // TODO: extract tenant ID from JWT/security context
        val response = createProductUseCase.execute(request, tenantId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
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

    /**
     * GET /api/v1/inventory/products/search?q=X&page=0&size=20
     * Searches products by name with case/accent-insensitive partial matching.
     */
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam q: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<*> {
        return try {
            val result = searchProductsUseCase.execute(q, page, size)
            ResponseEntity.ok(SearchResponse.from(result, page, size))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "QUERY_TOO_SHORT", "message" to e.message)
            )
        }
    }

    /**
     * GET /api/v1/inventory/products/duplicate-check?name=X
     * Checks for existing products with similar names.
     */
    @GetMapping("/duplicate-check")
    fun duplicateCheck(
        @RequestParam name: String
    ): ResponseEntity<DuplicateCheckResponse> {
        val duplicates = productRepositoryPort.findByNameLike(name)
        return ResponseEntity.ok(
            DuplicateCheckResponse(
                duplicates = duplicates.map { DuplicateItem.from(it) }
            )
        )
    }
}
