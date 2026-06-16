package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.DocumentStatus
import com.siga.sales.domain.model.DocumentType
import com.siga.sales.domain.model.SaleDocument
import com.siga.sales.event.SaleEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Integration test for [SaleDocumentJpaAdapter].
 * Verifies SaleDocument persistence through the hexagonal port with H2.
 */
class SaleDocumentJpaAdapterTest : BaseSalesIntegrationTest() {

    @Autowired
    private lateinit var adapter: SaleDocumentJpaAdapter

    @MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    init {
        describe("SaleDocumentJpaAdapter") {

            it("save and find by id") {
                val doc = SaleDocument(
                    id = UUID.randomUUID(),
                    saleId = UUID.randomUUID(),
                    customerId = null,
                    type = DocumentType.BOLETA,
                    folio = 1001L,
                    totalAmount = BigDecimal("150.00"),
                    taxAmount = BigDecimal("28.50"),
                    status = DocumentStatus.EMITTED,
                    pdfUrl = null,
                    xmlUrl = null,
                    createdAt = Instant.now()
                )

                val saved = adapter.save(doc)
                saved.id shouldBe doc.id
                saved.saleId shouldBe doc.saleId
                saved.type shouldBe DocumentType.BOLETA
                saved.folio shouldBe 1001L

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.totalAmount shouldBe BigDecimal("150.00")
                found?.taxAmount shouldBe BigDecimal("28.50")
                found?.status shouldBe DocumentStatus.EMITTED
            }

            it("findById returns null when document does not exist") {
                val found = adapter.findById(UUID.randomUUID())
                found shouldBe null
            }

            it("findBySaleId returns document for a sale") {
                val saleId = UUID.randomUUID()
                val doc = SaleDocument(
                    id = UUID.randomUUID(), saleId = saleId, customerId = null,
                    type = DocumentType.BOLETA, folio = 2001L,
                    totalAmount = BigDecimal("300.00"), taxAmount = BigDecimal("57.00"),
                    status = DocumentStatus.EMITTED, pdfUrl = null, xmlUrl = null,
                    createdAt = Instant.now()
                )
                adapter.save(doc)

                val found = adapter.findBySaleId(saleId)
                found shouldNotBe null
                found?.id shouldBe doc.id
                found?.folio shouldBe 2001L
            }

            it("findBySaleId returns null when no document for sale") {
                val found = adapter.findBySaleId(UUID.randomUUID())
                found shouldBe null
            }

            it("save FACTURA with customer reference") {
                val doc = SaleDocument(
                    id = UUID.randomUUID(), saleId = UUID.randomUUID(), customerId = UUID.randomUUID(),
                    type = DocumentType.FACTURA, folio = 3001L,
                    totalAmount = BigDecimal("1000.00"), taxAmount = BigDecimal("190.00"),
                    status = DocumentStatus.EMITTED, pdfUrl = null, xmlUrl = null,
                    createdAt = Instant.now()
                )
                val saved = adapter.save(doc)
                saved.customerId shouldNotBe null
                saved.type shouldBe DocumentType.FACTURA

                val found = adapter.findById(saved.id)
                found?.customerId shouldNotBe null
            }

            it("update document status") {
                val doc = SaleDocument(
                    id = UUID.randomUUID(), saleId = UUID.randomUUID(), customerId = null,
                    type = DocumentType.BOLETA, folio = 4001L,
                    totalAmount = BigDecimal("500.00"), taxAmount = BigDecimal("95.00"),
                    status = DocumentStatus.EMITTED, pdfUrl = null, xmlUrl = null,
                    createdAt = Instant.now()
                )
                val saved = adapter.save(doc)

                val updated = saved.copy(status = DocumentStatus.ANNULLED)
                adapter.save(updated)

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.status shouldBe DocumentStatus.ANNULLED
            }
        }
    }
}
