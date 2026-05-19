package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.port.MovementRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.UUID

class TransferMovementHistoryUseCaseTest : DescribeSpec({

    val movementPort = mockk<MovementRepositoryPort>()
    val useCase = TransferMovementHistoryUseCase(movementPort)

    val storeId = UUID.randomUUID()
    val destStoreId = UUID.randomUUID()
    val now = Instant.now()
    val correlationId = UUID.randomUUID()

    fun makeMovement(storeId: UUID, type: MovementType = MovementType.TRANSFER): Movement = Movement(
        id = UUID.randomUUID(),
        productId = UUID.randomUUID(),
        storeId = storeId,
        type = type,
        quantity = 50,
        previousQuantity = 200,
        newQuantity = 150,
        userId = UUID.randomUUID(),
        saleId = null,
        observations = null,
        correlationId = correlationId,
        destinationStoreId = destStoreId
    )

    describe("execute") {

        it("should delegate to port with all filters") {
            val movements = listOf(makeMovement(storeId))
            val pageable: Pageable = PageRequest.of(0, 20)
            val page: Page<Movement> = PageImpl(movements)

            every {
                movementPort.findByFilters(storeId, MovementType.TRANSFER, now, now, pageable)
            } returns page

            val result = useCase.execute(
                storeId = storeId,
                type = MovementType.TRANSFER,
                from = now,
                to = now,
                pageable = pageable
            )

            result.content.size shouldBe 1
            result.content[0].correlationId shouldBe correlationId
            result.content[0].destinationStoreId shouldBe destStoreId

            verify {
                movementPort.findByFilters(storeId, MovementType.TRANSFER, now, now, pageable)
            }
        }

        it("should return empty page when no movements match") {
            val pageable: Pageable = PageRequest.of(0, 20)
            val page: Page<Movement> = PageImpl(emptyList())

            every {
                movementPort.findByFilters(null, null, null, null, pageable)
            } returns page

            val result = useCase.execute(
                storeId = null,
                type = null,
                from = null,
                to = null,
                pageable = pageable
            )

            result.content.size shouldBe 0
        }

        it("should filter by destination store ID") {
            val pageable: Pageable = PageRequest.of(0, 20)
            val movements = listOf(
                makeMovement(storeId),
                makeMovement(UUID.randomUUID())
            )
            val page: Page<Movement> = PageImpl(movements)

            every {
                movementPort.findByFilters(storeId, null, null, null, pageable)
            } returns page

            val result = useCase.execute(
                storeId = storeId,
                type = null,
                from = null,
                to = null,
                pageable = pageable
            )

            result.content.size shouldBe 2
        }
    }
})
