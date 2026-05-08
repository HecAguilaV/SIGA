package com.siga.sales.controller

import com.siga.sales.application.usecase.ManageCashShiftUseCase
import com.siga.sales.domain.model.CashShift
import com.siga.sales.domain.model.ShiftStatus
import com.siga.sales.domain.port.CashShiftRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Unit tests for [CashShiftController].
 * Pure hexagonal: controller depends only on ports — test with mocks, no Spring.
 */
class CashShiftControllerTest : DescribeSpec({

    val cashShiftRepositoryPort = mockk<CashShiftRepositoryPort>()
    val manageCashShiftUseCase = mockk<ManageCashShiftUseCase>()
    val controller = CashShiftController(cashShiftRepositoryPort, manageCashShiftUseCase)

    val openShift = CashShift(
        id = UUID.randomUUID(),
        storeId = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        openedAt = Instant.now(),
        closedAt = null,
        initialAmount = BigDecimal("500000.00"),
        finalAmount = null,
        status = ShiftStatus.OPEN
    )

    beforeEach {
        clearAllMocks()
    }

    describe("GET /api/v1/sales/cash-shifts") {

        it("returns all shifts") {
            val shifts = listOf(openShift)
            every { cashShiftRepositoryPort.findAll() } returns shifts

            val response: ResponseEntity<List<CashShift>> = controller.getAllShifts()

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe shifts
        }

        it("returns empty list when no shifts") {
            every { cashShiftRepositoryPort.findAll() } returns emptyList()

            val response: ResponseEntity<List<CashShift>> = controller.getAllShifts()

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe emptyList()
        }
    }

    describe("GET /api/v1/sales/cash-shifts/{id}") {

        it("returns 200 when shift exists") {
            every { cashShiftRepositoryPort.findById(openShift.id) } returns openShift

            val response: ResponseEntity<CashShift> = controller.getShiftById(openShift.id)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe openShift
        }

        it("returns 404 when shift not found") {
            val id = UUID.randomUUID()
            every { cashShiftRepositoryPort.findById(id) } returns null

            val response: ResponseEntity<CashShift> = controller.getShiftById(id)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    }

    describe("GET /api/v1/sales/cash-shifts/store/{storeId}") {

        it("returns shifts for given store") {
            val storeId = UUID.randomUUID()
            val shifts = listOf(openShift)
            every { cashShiftRepositoryPort.findByStoreId(storeId) } returns shifts

            val response: ResponseEntity<List<CashShift>> = controller.getShiftsByStore(storeId)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe shifts
        }
    }

    describe("GET /api/v1/sales/cash-shifts/user/{userId}/open") {

        it("returns 200 when open shift exists") {
            val userId = UUID.randomUUID()
            every { manageCashShiftUseCase.getOpenShiftByUser(userId) } returns openShift

            val response: ResponseEntity<CashShift> = controller.getOpenShiftByUser(userId)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe openShift
        }

        it("returns 404 when no open shift for user") {
            val userId = UUID.randomUUID()
            every { manageCashShiftUseCase.getOpenShiftByUser(userId) } returns null

            val response: ResponseEntity<CashShift> = controller.getOpenShiftByUser(userId)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    }

    describe("POST /api/v1/sales/cash-shifts") {

        it("calls use case and returns created shift") {
            val shiftInput = CashShift(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                openedAt = Instant.now(),
                closedAt = null,
                initialAmount = BigDecimal("300000.00"),
                finalAmount = null,
                status = ShiftStatus.OPEN
            )

            every {
                manageCashShiftUseCase.openShift(shiftInput.storeId, shiftInput.userId, shiftInput.initialAmount)
            } returns openShift

            val response: ResponseEntity<CashShift> = controller.createShift(shiftInput)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe openShift
            verify(exactly = 1) {
                manageCashShiftUseCase.openShift(shiftInput.storeId, shiftInput.userId, shiftInput.initialAmount)
            }
        }
    }

    describe("PUT /api/v1/sales/cash-shifts/{id}") {

        it("returns 200 and updates shift when exists") {
            val updateData = openShift.copy(
                initialAmount = BigDecimal("600000.00"),
                status = ShiftStatus.CLOSED
            )

            every { cashShiftRepositoryPort.findById(openShift.id) } returns openShift
            every { cashShiftRepositoryPort.save(any()) } answers { firstArg() }

            val response: ResponseEntity<CashShift> = controller.updateShift(openShift.id, updateData)

            response.statusCode shouldBe HttpStatus.OK
            response.body?.id shouldBe openShift.id
            response.body?.status shouldBe ShiftStatus.CLOSED
        }

        it("returns 404 when shift not found for update") {
            val id = UUID.randomUUID()
            every { cashShiftRepositoryPort.findById(id) } returns null

            val response: ResponseEntity<CashShift> = controller.updateShift(id, openShift)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
            verify(exactly = 0) { cashShiftRepositoryPort.save(any()) }
        }
    }
})
