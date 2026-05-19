package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.domain.service.SkuGenerator
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Use Case: Creates a new product with optional auto-SKU generation
 * and fuzzy duplicate detection.
 *
 * Flow:
 * 1. If SKU is not provided, auto-generate via [SkuGenerator]
 * 2. Check for fuzzy duplicates via [ProductRepositoryPort.findByNameLike]
 * 3. If duplicates found and not forced, return warning (do not block)
 * 4. Save the product
 * 5. Return response with optional warning
 */
@Service
class CreateProductUseCase(
    private val productPort: ProductRepositoryPort,
    private val skuGenerator: SkuGenerator
) {
    /**
     * Creates a new product.
     *
     * @param request The creation request with product details.
     * @param tenantId The tenant identifier for SKU generation.
     * @return [CreateProductResponse] with the created product info and optional duplicate warning.
     */
    fun execute(request: CreateProductRequest, tenantId: Long): CreateProductResponse {
        // Resolve SKU
        val sku = if (request.sku.isNullOrBlank()) {
            skuGenerator.nextSku(tenantId, request.categoryName)
        } else {
            request.sku
        }

        // Check for duplicates
        val duplicates = if (!request.name.isNullOrBlank()) {
            productPort.findByNameLike(request.name)
        } else {
            emptyList()
        }

        val warning = if (duplicates.isNotEmpty()) {
            val existing = duplicates.first()
            DuplicateWarning(
                existingProductId = existing.id,
                existingProductName = existing.name,
                existingSku = existing.sku ?: ""
            )
        } else {
            null
        }

        val now = Instant.now()
        val product = Product(
            id = UUID.randomUUID(),
            name = request.name,
            description = request.description,
            categoryId = request.categoryId,
            barcode = request.barcode,
            unitPrice = BigDecimal.ZERO,
            isActive = true,
            commercialUserId = null,
            sku = sku,
            unitType = request.unitType,
            createdAt = now,
            updatedAt = now
        )

        val saved = productPort.save(product)

        return CreateProductResponse(
            productId = saved.id,
            sku = saved.sku ?: "",
            name = saved.name,
            status = if (saved.isActive) "ACTIVE" else "INACTIVE",
            warning = warning
        )
    }
}

/**
 * Request DTO for product creation.
 */
data class CreateProductRequest(
    val name: String,
    val sku: String?,
    val categoryId: UUID?,
    val categoryName: String? = null,
    val description: String?,
    val unitType: String?,
    val barcode: String? = null,
    val force: Boolean = false
)

/**
 * Response DTO for product creation.
 */
data class CreateProductResponse(
    val productId: UUID,
    val sku: String,
    val name: String,
    val status: String,
    val warning: DuplicateWarning? = null
)

/**
 * Warning returned when a similar product name already exists.
 */
data class DuplicateWarning(
    val existingProductId: UUID,
    val existingProductName: String,
    val existingSku: String
)
