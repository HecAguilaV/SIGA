package com.siga.inventory.domain.service

import com.siga.inventory.domain.port.SkuSequencePort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SkuGeneratorTest : DescribeSpec({

    val sequencePort = mockk<SkuSequencePort>()
    val generator = SkuGenerator(sequencePort)

    val tenantId = 1L

    beforeEach {
        // No global mock reset — each test sets its own expectations
    }

    describe("extractPrefix") {

        it("should extract first 3 uppercase letters from category name") {
            val prefix = generator.extractPrefix("Galletas")
            prefix shouldBe "GAL"
        }

        it("should return GEN for null category name") {
            val prefix = generator.extractPrefix(null)
            prefix shouldBe "GEN"
        }

        it("should return GEN for blank category name") {
            val prefix = generator.extractPrefix("")
            prefix shouldBe "GEN"
        }

        it("should strip non-alpha characters from prefix") {
            val prefix = generator.extractPrefix("Café 200g")
            prefix shouldBe "CAF"
        }

        it("should uppercase the extracted prefix") {
            val prefix = generator.extractPrefix("bebidas")
            prefix shouldBe "BEB"
        }

        it("should take at most 3 characters") {
            val prefix = generator.extractPrefix("ABCDEF")
            prefix shouldBe "ABC"
        }
    }

    describe("nextSku") {

        it("should generate SKU with prefix and zero-padded sequence") {
            every { sequencePort.nextSequence(tenantId, "GAL") } returns 1

            val sku = generator.nextSku(tenantId, "Galletas")

            sku shouldBe "GAL-0001"
            verify { sequencePort.nextSequence(tenantId, "GAL") }
        }

        it("should pad sequence with leading zeros to 4 digits") {
            every { sequencePort.nextSequence(tenantId, "GAL") } returns 42

            val sku = generator.nextSku(tenantId, "Galletas")

            sku shouldBe "GAL-0042"
        }

        it("should use GEN prefix when category name is null") {
            every { sequencePort.nextSequence(tenantId, "GEN") } returns 5

            val sku = generator.nextSku(tenantId, null)

            sku shouldBe "GEN-0005"
            verify { sequencePort.nextSequence(tenantId, "GEN") }
        }

        it("should handle three-digit sequence values") {
            every { sequencePort.nextSequence(tenantId, "CAF") } returns 999

            val sku = generator.nextSku(tenantId, "Café")

            sku shouldBe "CAF-0999"
        }

        it("should handle four-digit sequence values without truncation") {
            every { sequencePort.nextSequence(tenantId, "BEB") } returns 1234

            val sku = generator.nextSku(tenantId, "Bebidas")

            sku shouldBe "BEB-1234"
        }
    }
})
