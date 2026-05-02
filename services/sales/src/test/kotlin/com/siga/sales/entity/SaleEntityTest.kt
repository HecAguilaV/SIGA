package com.siga.sales.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal
import java.util.UUID

/**
 * Unit tests verifying UUID migration for Sales entities.
 * Ensures all entities use UUID as primary key and foreign key types.
 */
class SaleEntityTest : DescribeSpec({

    describe("Sale") {
        it("given a new sale when id is null then should accept UUID generation strategy") {
            val sale = Sale(
                storeId = UUID.randomUUID(),
                total = BigDecimal("100.00")
            )

            sale.id shouldBe null
            sale.storeId.shouldBeInstanceOf<UUID>()
            sale.status shouldBe SaleStatus.PENDING
        }

        it("given a sale with uuid when compared then should use id equality") {
            val id = UUID.randomUUID()
            val sale1 = Sale(storeId = UUID.randomUUID(), total = BigDecimal("50.00")).apply { this.id = id }
            val sale2 = Sale(storeId = UUID.randomUUID(), total = BigDecimal("75.00")).apply { this.id = id }

            sale1 shouldBe sale2
            sale1.hashCode() shouldBe sale2.hashCode()
        }

        it("given two sales with null ids then should not be equal") {
            val sale1 = Sale(storeId = UUID.randomUUID(), total = BigDecimal("50.00"))
            val sale2 = Sale(storeId = UUID.randomUUID(), total = BigDecimal("75.00"))

            (sale1 == sale2) shouldBe false
        }
    }

    describe("SaleItem") {
        it("given a new sale item when created then should use UUID types") {
            val item = SaleItem(
                saleId = UUID.randomUUID(),
                productId = UUID.randomUUID(),
                quantity = 2,
                unitPrice = BigDecimal("10.00"),
                subtotal = BigDecimal("20.00")
            )

            item.id shouldBe null
            item.saleId.shouldBeInstanceOf<UUID>()
            item.productId.shouldBeInstanceOf<UUID>()
        }
    }

    describe("CashShift") {
        it("given a new cash shift when created then should use UUID types") {
            val shift = CashShift(
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                initialAmount = BigDecimal("50000.00")
            )

            shift.id shouldBe null
            shift.storeId.shouldBeInstanceOf<UUID>()
            shift.userId.shouldBeInstanceOf<UUID>()
            shift.status shouldBe ShiftStatus.OPEN
        }
    }

    describe("PaymentMethod") {
        it("given a new payment method when created then should use UUID id") {
            val method = PaymentMethod(name = "Efectivo")

            method.id shouldBe null
            method.isActive shouldBe true
        }
    }

    describe("PosTransaction") {
        it("given a new transaction when created then should use UUID types for all references") {
            val tx = PosTransaction(
                saleId = UUID.randomUUID(),
                shiftId = UUID.randomUUID(),
                paymentMethodId = UUID.randomUUID(),
                amount = BigDecimal("25000.00")
            )

            tx.id shouldBe null
            tx.saleId.shouldBeInstanceOf<UUID>()
            tx.shiftId.shouldBeInstanceOf<UUID>()
            tx.paymentMethodId.shouldBeInstanceOf<UUID>()
        }
    }

    describe("PosCart") {
        it("given a new cart item when created then should use UUID types") {
            val cart = PosCart(
                productId = UUID.randomUUID(),
                unitPrice = BigDecimal("5000.00"),
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID()
            )

            cart.id shouldBe null
            cart.saleId shouldBe null
        }
    }

    describe("Customer") {
        it("given a new customer when created then should use UUID id") {
            val customer = Customer(name = "Juan Pérez", taxId = "12345678-9")

            customer.id shouldBe null
            customer.taxId shouldBe "12345678-9"
        }
    }

    describe("SaleDocument") {
        it("given a new sale document when created then should use UUID types") {
            val doc = SaleDocument(
                saleId = UUID.randomUUID(),
                type = DocumentType.BOLETA,
                folio = 12345L,
                totalAmount = BigDecimal("100000.00"),
                taxAmount = BigDecimal("19000.00")
            )

            doc.id shouldBe null
            doc.saleId.shouldBeInstanceOf<UUID>()
            doc.status shouldBe DocumentStatus.EMITTED
        }

        it("given a factura when customer is null then should still be valid entity") {
            val doc = SaleDocument(
                saleId = UUID.randomUUID(),
                customerId = null,
                type = DocumentType.FACTURA,
                folio = 1L,
                totalAmount = BigDecimal("50000.00"),
                taxAmount = BigDecimal("9500.00")
            )

            doc.customerId shouldBe null
        }
    }
})
