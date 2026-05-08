package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.entity.Product as ProductEntity
import com.siga.inventory.infrastructure.mapper.ProductMapper
import com.siga.inventory.repository.ProductRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing the ProductRepositoryPort.
 *
 * WHY HEXAGONAL: The Domain calls `ProductRepositoryPort`, but the implementation
 * uses Spring Data JPA. This class is the "Adapter" in Ports & Adapters.
 */
@Component
class ProductJpaAdapter(
    private val productRepository: ProductRepository
) : ProductRepositoryPort {

    override fun findById(id: UUID): Product? {
        val entity = productRepository.findById(id)
        return if (entity.isPresent) ProductMapper.toDomain(entity.get()) else null
    }

    override fun save(product: Product): Product {
        val entity = ProductMapper.toEntity(product)
        val savedEntity = productRepository.save(entity)
        return ProductMapper.toDomain(savedEntity)
    }

    override fun findByCommercialUserId(userId: UUID): List<Product> {
        return productRepository.findByCommercialUserId(userId).map { ProductMapper.toDomain(it) }
    }

    override fun findByCategoryId(categoryId: UUID): List<Product> {
        return productRepository.findByCategoryId(categoryId).map { ProductMapper.toDomain(it) }
    }

    override fun findByBarcode(barcode: String): Product? {
        val entity = productRepository.findByBarcode(barcode) ?: return null
        return ProductMapper.toDomain(entity)
    }
}
