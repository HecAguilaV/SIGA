package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class EntityCoverageTest {

    @Test
    fun `test enums coverage`() {
        // Test InvoiceStatus
        InvoiceStatus.values().forEach {
            assertEquals(it, InvoiceStatus.valueOf(it.name))
        }

        // Test PaymentStatus
        PaymentStatus.values().forEach {
            assertEquals(it, PaymentStatus.valueOf(it.name))
        }

        // Test SaleInvoiceStatus
        SaleInvoiceStatus.values().forEach {
            assertEquals(it, SaleInvoiceStatus.valueOf(it.name))
        }

        // Test BillingPeriod
        BillingPeriod.values().forEach {
            assertEquals(it, BillingPeriod.valueOf(it.name))
        }

        // Test SubscriptionStatus
        SubscriptionStatus.values().forEach {
            assertEquals(it, SubscriptionStatus.valueOf(it.name))
        }
    }

    @Test
    fun `test equals and hashCode edge cases`() {
        val customer = CustomerEntity(email = "e", passwordHash = "p", name = "n")
        val plan = PlanEntity(name = "n", monthlyPrice = BigDecimal.ZERO)
        val invoice = Invoice(invoiceNumber = "1", customerId = UUID.randomUUID(), userName = "u", userEmail = "e", planId = UUID.randomUUID(), planName = "p", priceUF = BigDecimal.ZERO)
        val payment = PaymentEntity(subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal.ZERO)
        val saleInvoice = SaleInvoiceEntity(saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal.ZERO)
        val cart = ShoppingCart(customerId = UUID.randomUUID())
        val subscription = SubscriptionEntity(customerId = UUID.randomUUID(), planId = UUID.randomUUID())

        val entities = listOf(customer, plan, invoice, payment, saleInvoice, cart, subscription)

        entities.forEach { entity ->
            // hashCode when id is null
            assertEquals(0, entity.hashCode())

            // equals with null
            assertFalse(entity.equals(null))

            // equals with different type
            assertFalse(entity.equals("not an entity"))

            // equals with itself
            assertTrue(entity.equals(entity))

            // equals when both ids are null (returns false because id != null is checked)
            val other = when(entity) {
                is CustomerEntity -> CustomerEntity(email = "e", passwordHash = "p", name = "n")
                is PlanEntity -> PlanEntity(name = "n", monthlyPrice = BigDecimal.ZERO)
                is Invoice -> Invoice(invoiceNumber = "1", customerId = UUID.randomUUID(), userName = "u", userEmail = "e", planId = UUID.randomUUID(), planName = "p", priceUF = BigDecimal.ZERO)
                is PaymentEntity -> PaymentEntity(subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal.ZERO)
                is SaleInvoiceEntity -> SaleInvoiceEntity(saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal.ZERO)
                is ShoppingCart -> ShoppingCart(customerId = UUID.randomUUID())
                is SubscriptionEntity -> SubscriptionEntity(customerId = UUID.randomUUID(), planId = UUID.randomUUID())
                else -> throw IllegalStateException("Unknown entity")
            }
            assertFalse(entity.equals(other))
        }
    }

    @Test
    fun `explicitly call all getters and setters`() {
        // CustomerEntity
        CustomerEntity(email = "e", passwordHash = "p", name = "n").apply {
            id = UUID.randomUUID()
            email = "new@e.com"
            passwordHash = "new-p"
            name = "new-n"
            lastName = "l"
            taxId = "t"
            phone = "p"
            companyName = "c"
            isActive = false
            isOnTrial = true
            trialStartAt = null
            trialEndAt = null
            role = "r"
            planId = UUID.randomUUID()
            
            assertEquals(email, "new@e.com")
            assertEquals(passwordHash, "new-p")
            assertEquals(name, "new-n")
            assertEquals(lastName, "l")
            assertEquals(taxId, "t")
            assertEquals(phone, "p")
            assertEquals(companyName, "c")
            assertFalse(isActive)
            assertTrue(isOnTrial)
            assertEquals(role, "r")
        }

        // PlanEntity
        PlanEntity(name = "n", monthlyPrice = BigDecimal.ZERO).apply {
            id = UUID.randomUUID()
            name = "new-n"
            description = "d"
            storeLimit = 10
            userLimit = 20
            productLimit = 30
            monthlyPrice = BigDecimal.ONE
            yearlyPrice = BigDecimal.TEN
            displayOrder = 5
            isActive = false

            assertEquals(name, "new-n")
            assertEquals(description, "d")
            assertEquals(storeLimit, 10)
            assertEquals(userLimit, 20)
            assertEquals(productLimit, 30)
            assertEquals(monthlyPrice, BigDecimal.ONE)
            assertEquals(yearlyPrice, BigDecimal.TEN)
            assertEquals(displayOrder, 5)
            assertFalse(isActive)
        }

        // Invoice
        Invoice(invoiceNumber = "1", customerId = UUID.randomUUID(), userName = "u", userEmail = "e", planId = UUID.randomUUID(), planName = "p", priceUF = BigDecimal.ZERO).apply {
            id = UUID.randomUUID()
            invoiceNumber = "new-1"
            customerId = UUID.randomUUID()
            userName = "new-u"
            userEmail = "new-e"
            planId = UUID.randomUUID()
            planName = "new-p"
            priceUF = BigDecimal.ONE
            priceCLP = BigDecimal.TEN
            unit = "CLP"
            purchasedAt = java.time.Instant.now()
            dueDate = null
            status = InvoiceStatus.PENDING
            paymentMethod = "m"
            last4Digits = "1"
            subscriptionId = UUID.randomUUID()
            paymentId = UUID.randomUUID()
            tax = BigDecimal.ZERO
            
            assertEquals(invoiceNumber, "new-1")
            assertEquals(userName, "new-u")
            assertEquals(userEmail, "new-e")
            assertEquals(planName, "new-p")
            assertEquals(priceUF, BigDecimal.ONE)
            assertEquals(priceCLP, BigDecimal.TEN)
            assertEquals(unit, "CLP")
            assertEquals(status, InvoiceStatus.PENDING)
            assertEquals(paymentMethod, "m")
            assertEquals(last4Digits, "1")
        }

        // PaymentEntity
        PaymentEntity(subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal.ZERO).apply {
            id = UUID.randomUUID()
            subscriptionId = UUID.randomUUID()
            customerId = UUID.randomUUID()
            amount = BigDecimal.ONE
            paymentMethod = "m"
            status = PaymentStatus.COMPLETED
            reference = "r"
            paidAt = java.time.Instant.now()

            assertEquals(amount, BigDecimal.ONE)
            assertEquals(paymentMethod, "m")
            assertEquals(status, PaymentStatus.COMPLETED)
            assertEquals(reference, "r")
        }

        // SaleInvoiceEntity
        SaleInvoiceEntity(saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal.ZERO).apply {
            id = UUID.randomUUID()
            saleId = UUID.randomUUID()
            storeId = UUID.randomUUID()
            userId = UUID.randomUUID()
            total = BigDecimal.ONE
            items = "{}"
            status = SaleInvoiceStatus.CANCELLED
            
            assertEquals(total, BigDecimal.ONE)
            assertEquals(items, "{}")
            assertEquals(status, SaleInvoiceStatus.CANCELLED)
        }

        // ShoppingCart
        ShoppingCart(customerId = UUID.randomUUID()).apply {
            id = UUID.randomUUID()
            customerId = UUID.randomUUID()
            planId = UUID.randomUUID()
            period = BillingPeriod.ANNUAL
            
            assertEquals(period, BillingPeriod.ANNUAL)
        }

        // SubscriptionEntity
        SubscriptionEntity(customerId = UUID.randomUUID(), planId = UUID.randomUUID()).apply {
            id = UUID.randomUUID()
            customerId = UUID.randomUUID()
            planId = UUID.randomUUID()
            period = BillingPeriod.ANNUAL
            status = SubscriptionStatus.SUSPENDED
            startsAt = java.time.Instant.now()
            endsAt = null
            
            assertEquals(period, BillingPeriod.ANNUAL)
            assertEquals(status, SubscriptionStatus.SUSPENDED)
        }
    }
}
