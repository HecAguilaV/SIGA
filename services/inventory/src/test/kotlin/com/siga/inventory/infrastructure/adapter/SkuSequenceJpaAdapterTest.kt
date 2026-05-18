package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.port.SkuSequencePort
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean

/**
 * Integration test for [SkuSequenceJpaAdapter].
 * Verifies SKU sequence generation through the hexagonal port with H2.
 *
 * NOTE: The sku_sequences table uses prefix as PK (not tenant+prefix).
 * Each prefix has a single counter shared across tenants.
 */
@SpringBootTest
@ActiveProfiles("test")
class SkuSequenceJpaAdapterTest : DescribeSpec() {

    @Autowired
    private lateinit var adapter: SkuSequencePort

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("SkuSequenceJpaAdapter") {

            val tenantId = 1L
            val prefix = "CAF"

            it("nextSequence returns 1 for first call with new prefix") {
                val seq = adapter.nextSequence(tenantId, prefix)
                seq shouldBe 1
            }

            it("nextSequence increments on each call for same prefix") {
                val pfx = "LAP"

                val seq1 = adapter.nextSequence(tenantId, pfx)
                seq1 shouldBe 1

                val seq2 = adapter.nextSequence(tenantId, pfx)
                seq2 shouldBe 2

                val seq3 = adapter.nextSequence(tenantId, pfx)
                seq3 shouldBe 3
            }

            it("nextSequence tracks sequences independently per prefix") {
                val seqA = adapter.nextSequence(tenantId, "AAA")
                val seqB = adapter.nextSequence(tenantId, "BBB")

                seqA shouldBe 1
                seqB shouldBe 1

                val seqA2 = adapter.nextSequence(tenantId, "AAA")
                seqA2 shouldBe 2

                val seqB2 = adapter.nextSequence(tenantId, "BBB")
                seqB2 shouldBe 2
            }
        }
    }
}
