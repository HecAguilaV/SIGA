package com.siga.sales.controller

import com.siga.sales.application.usecase.ManageCashShiftUseCase
import com.siga.sales.domain.model.CashShift
import com.siga.sales.domain.port.CashShiftRepositoryPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage cash shifts.
 * Uses ManageCashShiftUseCase for business logic.
 */
@RestController
@RequestMapping("/api/v1/sales/cash-shifts")
class CashShiftController(
    private val cashShiftRepositoryPort: CashShiftRepositoryPort,
    private val manageCashShiftUseCase: ManageCashShiftUseCase
) {
    @GetMapping
    fun getAllShifts(): ResponseEntity<List<CashShift>> {
        return ResponseEntity.ok(cashShiftRepositoryPort.findAll())
    }

    @GetMapping("/{id}")
    fun getShiftById(@PathVariable id: UUID): ResponseEntity<CashShift> {
        val shift = cashShiftRepositoryPort.findById(id)
        return if (shift != null) {
            ResponseEntity.ok(shift)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/store/{storeId}")
    fun getShiftsByStore(@PathVariable storeId: UUID): ResponseEntity<List<CashShift>> {
        return ResponseEntity.ok(cashShiftRepositoryPort.findByStoreId(storeId))
    }

    @GetMapping("/user/{userId}/open")
    fun getOpenShiftByUser(@PathVariable userId: UUID): ResponseEntity<CashShift> {
        val shift = manageCashShiftUseCase.getOpenShiftByUser(userId)
        return if (shift != null) {
            ResponseEntity.ok(shift)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createShift(@RequestBody shift: CashShift): ResponseEntity<CashShift> {
        val savedShift = manageCashShiftUseCase.openShift(
            storeId = shift.storeId,
            userId = shift.userId,
            initialAmount = shift.initialAmount
        )
        return ResponseEntity.ok(savedShift)
    }

    @PutMapping("/{id}")
    fun updateShift(@PathVariable id: UUID, @RequestBody shift: CashShift): ResponseEntity<CashShift> {
        return if (cashShiftRepositoryPort.findById(id) != null) {
            val updatedShift = shift.copy(id = id)
            ResponseEntity.ok(cashShiftRepositoryPort.save(updatedShift))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
