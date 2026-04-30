package com.siga.inventory.controller

import com.siga.inventory.entity.Store
import com.siga.inventory.repository.StoreRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage stores/locations.
 */
@RestController
@RequestMapping("/api/v1/inventory/stores")
class StoreController(
    private val storeRepository: StoreRepository
) {
    @GetMapping
    fun getAllStores(): ResponseEntity<List<Store>> {
        return ResponseEntity.ok(storeRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getStoreById(@PathVariable id: UUID): ResponseEntity<Store> {
        val store = storeRepository.findById(id)
        return if (store.isPresent) {
            ResponseEntity.ok(store.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/company/{companyId}")
    fun getStoresByCompany(@PathVariable companyId: UUID): ResponseEntity<List<Store>> {
        return ResponseEntity.ok(storeRepository.findByCommercialUserId(companyId))
    }

    @PostMapping
    fun createStore(@RequestBody store: Store): ResponseEntity<Store> {
        return ResponseEntity.status(201).body(storeRepository.save(store))
    }

    @PutMapping("/{id}")
    fun updateStore(@PathVariable id: UUID, @RequestBody store: Store): ResponseEntity<Store> {
        return if (storeRepository.existsById(id)) {
            store.id = id
            ResponseEntity.ok(storeRepository.save(store))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteStore(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (storeRepository.existsById(id)) {
            storeRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
