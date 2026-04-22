package com.siga.inventory.controller

import com.siga.inventory.entity.Store
import com.siga.inventory.repository.StoreRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage stores/locations.
 */
@RestController
@RequestMapping("/api/stores")
class StoreController(
    private val storeRepository: StoreRepository
) {
    @GetMapping
    fun getAllStores(): ResponseEntity<List<Store>> {
        return ResponseEntity.ok(storeRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getStoreById(@PathVariable id: Int): ResponseEntity<Store> {
        val store = storeRepository.findById(id)
        return if (store.isPresent) {
            ResponseEntity.ok(store.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/company/{companyId}")
    fun getStoresByCompany(@PathVariable companyId: Int): ResponseEntity<List<Store>> {
        return ResponseEntity.ok(storeRepository.findByCommercialUserId(companyId))
    }

    @PostMapping
    fun createStore(@RequestBody store: Store): ResponseEntity<Store> {
        return ResponseEntity.ok(storeRepository.save(store))
    }

    @PutMapping("/{id}")
    fun updateStore(@PathVariable id: Int, @RequestBody store: Store): ResponseEntity<Store> {
        return if (storeRepository.existsById(id)) {
            store.id = id
            ResponseEntity.ok(storeRepository.save(store))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
