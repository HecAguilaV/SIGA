package com.siga.sales.controller

import com.siga.sales.entity.CashShift
import com.siga.sales.repository.CashShiftRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage cash shifts.
 */
@RestController
@RequestMapping("/api/v1/sales/cash-shifts")
class CashShiftController(
    private val cashShiftRepository: CashShiftRepository
) {
    @GetMapping
    fun getAllShifts(): ResponseEntity<List<CashShift>> {
        return ResponseEntity.ok(cashShiftRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getShiftById(@PathVariable id: UUID): ResponseEntity<CashShift> {
        val shift = cashShiftRepository.findById(id)
        return if (shift.isPresent) {
            ResponseEntity.ok(shift.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/store/{storeId}")
    fun getShiftsByStore(@PathVariable storeId: UUID): ResponseEntity<List<CashShift>> {
        return ResponseEntity.ok(cashShiftRepository.findByStoreId(storeId))
    }

    @GetMapping("/user/{userId}/open")
    fun getOpenShiftByUser(@PathVariable userId: UUID): ResponseEntity<CashShift> {
        val shift = cashShiftRepository.findByUserId(userId)
        return if (shift != null) {
            ResponseEntity.ok(shift)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createShift(@RequestBody shift: CashShift): ResponseEntity<CashShift> {
        return ResponseEntity.ok(cashShiftRepository.save(shift))
    }

    @PutMapping("/{id}")
    fun updateShift(@PathVariable id: UUID, @RequestBody shift: CashShift): ResponseEntity<CashShift> {
        return if (cashShiftRepository.existsById(id)) {
            shift.id = id
            ResponseEntity.ok(cashShiftRepository.save(shift))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
