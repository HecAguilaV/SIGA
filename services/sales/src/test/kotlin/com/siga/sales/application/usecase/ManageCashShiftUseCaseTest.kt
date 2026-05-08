package com.siga.sales.application.usecase

import com.siga.sales.domain.model.CashShift
import com.siga.sales.domain.model.ShiftStatus
import com.siga.sales.domain.port.CashShiftRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Unit tests for [ManageCashShiftUseCase].
 * Verifies cash shift opening, closing, and query logic.
 */
class ManageCashShiftUseCaseTest : DescribeSpec({

    val cashShiftRepositoryPort = mockk<CashShiftRepositoryPort>()
    val useCase = ManageCashShiftUseCase(cashShiftRepositoryPort)

    beforeEach {
        clearAllMocks()
    }

    describe("ManageCashShiftUseCase") {

        it("given_store_user_and_amount_when_open_shift_then_create_and_save_open_shift") {
            val storeId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val initialAmount = BigDecimal("500000.00")

            every { cashShiftRepositoryPort.save(any()) } answers { firstArg() }

            val result = useCase.openShift(storeId, userId, initialAmount)

            result.storeId shouldBe storeId
            result.userId shouldBe userId
            result.initialAmount shouldBe initialAmount
            result.status shouldBe ShiftStatus.OPEN
            result.closedAt shouldBe null
            result.finalAmount shouldBe null

            val slot = slot<CashShift>()
            verify(exactly = 1) { cashShiftRepositoryPort.save(capture(slot)) }
            slot.captured.storeId shouldBe storeId
            slot.captured.userId shouldBe userId
            slot.captured.initialAmount shouldBe initialAmount
            slot.captured.status shouldBe ShiftStatus.OPEN
        }

        it("given_open_shift_when_close_then_update_status_closed_and_set_final_amount") {
            val shiftId = UUID.randomUUID()
            val finalAmount = BigDecimal("520000.00")
            val existingShift = CashShift(
                id = shiftId,
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                openedAt = Instant.now(),
                closedAt = null,
                initialAmount = BigDecimal("500000.00"),
                finalAmount = null,
                status = ShiftStatus.OPEN
            )

            every { cashShiftRepositoryPort.findById(shiftId) } returns existingShift
            every { cashShiftRepositoryPort.save(any()) } answers { firstArg() }

            val result = useCase.closeShift(shiftId, finalAmount)

            result shouldNotBe null
            result?.id shouldBe shiftId
            result?.status shouldBe ShiftStatus.CLOSED
            result?.finalAmount shouldBe finalAmount
            result?.closedAt shouldNotBe null

            val slot = slot<CashShift>()
            verify(exactly = 1) { cashShiftRepositoryPort.save(capture(slot)) }
            slot.captured.status shouldBe ShiftStatus.CLOSED
            slot.captured.finalAmount shouldBe finalAmount
        }

        it("given_missing_shift_when_close_then_return_null") {
            val shiftId = UUID.randomUUID()
            val finalAmount = BigDecimal("1000.00")

            every { cashShiftRepositoryPort.findById(shiftId) } returns null

            val result = useCase.closeShift(shiftId, finalAmount)

            result shouldBe null
            verify(exactly = 0) { cashShiftRepositoryPort.save(any()) }
        }

        it("given_user_with_open_shift_when_get_open_then_return_shift") {
            val userId = UUID.randomUUID()
            val shift = CashShift(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = userId,
                openedAt = Instant.now(),
                closedAt = null,
                initialAmount = BigDecimal("300000.00"),
                finalAmount = null,
                status = ShiftStatus.OPEN
            )

            every { cashShiftRepositoryPort.findByUserId(userId) } returns shift

            val result = useCase.getOpenShiftByUser(userId)

            result shouldNotBe null
            result?.userId shouldBe userId
            result?.status shouldBe ShiftStatus.OPEN
        }

        it("given_user_without_open_shift_when_get_open_then_return_null") {
            val userId = UUID.randomUUID()

            every { cashShiftRepositoryPort.findByUserId(userId) } returns null

            val result = useCase.getOpenShiftByUser(userId)

            result shouldBe null
        }
    }
})
