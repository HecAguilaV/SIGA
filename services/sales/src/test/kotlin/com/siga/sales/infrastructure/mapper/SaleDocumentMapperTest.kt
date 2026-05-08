package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.DocumentStatus
import com.siga.sales.domain.model.DocumentType
import com.siga.sales.domain.model.SaleDocument
import com.siga.sales.entity.SaleDocumentEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Tests for [SaleDocumentMapper].
 */
class SaleDocumentMapperTest : DescribeSpec({

    val mapper = SaleDocumentMapper()
    val ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    describe("toDomain") {

        it("maps all fields from entity with non-null id") {
            val now = Instant.now()
            val entity = SaleDocumentEntity(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                type = DocumentType.FACTURA,
                folio = 12345L,
                totalAmount = BigDecimal("150000.00"),
                taxAmount = BigDecimal("28500.00"),
                status = DocumentStatus.EMITTED,
                pdfUrl = "https://docs.siga.cl/12345.pdf",
                xmlUrl = "https://docs.siga.cl/12345.xml",
                createdAt = now
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldBe entity.id
            domain.saleId shouldBe entity.saleId
            domain.customerId shouldBe entity.customerId
            domain.type shouldBe DocumentType.FACTURA
            domain.folio shouldBe 12345L
            domain.totalAmount shouldBe BigDecimal("150000.00")
            domain.taxAmount shouldBe BigDecimal("28500.00")
            domain.status shouldBe DocumentStatus.EMITTED
            domain.pdfUrl shouldBe "https://docs.siga.cl/12345.pdf"
            domain.xmlUrl shouldBe "https://docs.siga.cl/12345.xml"
            domain.createdAt shouldBe now
        }

        it("generates random UUID when entity id is null") {
            val entity = SaleDocumentEntity(
                saleId = UUID.randomUUID(),
                type = DocumentType.BOLETA,
                folio = 1L,
                totalAmount = BigDecimal("10000.00"),
                taxAmount = BigDecimal("1900.00")
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldNotBe null
            domain.id shouldNotBe ZERO_UUID
        }

        it("maps nullable fields as null") {
            val entity = SaleDocumentEntity(
                saleId = UUID.randomUUID(),
                type = DocumentType.BOLETA,
                folio = 1L,
                totalAmount = BigDecimal("10000.00"),
                taxAmount = BigDecimal("1900.00")
            )

            val domain = mapper.toDomain(entity)

            domain.customerId shouldBe null
            domain.pdfUrl shouldBe null
            domain.xmlUrl shouldBe null
        }
    }

    describe("toEntity") {

        it("maps all fields from domain") {
            val domain = SaleDocument(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                type = DocumentType.FACTURA,
                folio = 999L,
                totalAmount = BigDecimal("200000.00"),
                taxAmount = BigDecimal("38000.00"),
                status = DocumentStatus.ANNULLED,
                pdfUrl = "https://docs.siga.cl/999.pdf",
                xmlUrl = "https://docs.siga.cl/999.xml",
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe domain.id
            entity.saleId shouldBe domain.saleId
            entity.customerId shouldBe domain.customerId
            entity.type shouldBe DocumentType.FACTURA
            entity.folio shouldBe 999L
            entity.totalAmount shouldBe BigDecimal("200000.00")
            entity.taxAmount shouldBe BigDecimal("38000.00")
            entity.status shouldBe DocumentStatus.ANNULLED
            entity.pdfUrl shouldBe "https://docs.siga.cl/999.pdf"
            entity.xmlUrl shouldBe "https://docs.siga.cl/999.xml"
        }

        it("sets id to null when domain id is ZERO_UUID") {
            val domain = SaleDocument(
                id = ZERO_UUID,
                saleId = UUID.randomUUID(),
                customerId = null,
                type = DocumentType.BOLETA,
                folio = 1L,
                totalAmount = BigDecimal("5000.00"),
                taxAmount = BigDecimal("950.00"),
                status = DocumentStatus.EMITTED,
                pdfUrl = null,
                xmlUrl = null,
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe null
        }

        it("maps nullable fields as null") {
            val domain = SaleDocument(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                customerId = null,
                type = DocumentType.BOLETA,
                folio = 1L,
                totalAmount = BigDecimal("5000.00"),
                taxAmount = BigDecimal("950.00"),
                status = DocumentStatus.EMITTED,
                pdfUrl = null,
                xmlUrl = null,
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(domain)

            entity.customerId shouldBe null
            entity.pdfUrl shouldBe null
            entity.xmlUrl shouldBe null
        }
    }

    describe("roundtrip") {

        it("domain -> entity -> domain preserves all fields") {
            val original = SaleDocument(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                type = DocumentType.FACTURA,
                folio = 777L,
                totalAmount = BigDecimal("300000.00"),
                taxAmount = BigDecimal("57000.00"),
                status = DocumentStatus.EMITTED,
                pdfUrl = "https://docs.siga.cl/777.pdf",
                xmlUrl = "https://docs.siga.cl/777.xml",
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(original)
            val result = mapper.toDomain(entity)

            result shouldBe original
        }
    }
})
