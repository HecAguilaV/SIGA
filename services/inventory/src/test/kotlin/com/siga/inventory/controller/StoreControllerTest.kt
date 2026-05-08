package com.siga.inventory.controller

import com.siga.inventory.entity.Store
import com.siga.inventory.repository.StoreRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.springframework.http.HttpStatus
import java.util.UUID

class StoreControllerTest : DescribeSpec({

    val storeRepository = mockk<StoreRepository>()
    val controller = StoreController(storeRepository)

    val storeId = UUID.randomUUID()
    val companyId = UUID.randomUUID()

    describe("StoreController") {

        describe("getAllStores") {

            it("given existing stores when getting all then should return 200 OK with list") {
                val stores = listOf(
                    Store(id = storeId, name = "Store 1", commercialUserId = companyId)
                )
                every { storeRepository.findAll() } returns stores

                val response = controller.getAllStores()

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }

            it("given no stores when getting all then should return 200 OK with empty list") {
                every { storeRepository.findAll() } returns emptyList()

                val response = controller.getAllStores()

                response.statusCode shouldBe HttpStatus.OK
                response.body?.isEmpty() shouldBe true
            }
        }

        describe("getStoreById") {

            it("given existing store when getting by id then should return 200 OK") {
                val store = Store(id = storeId, name = "Test Store", commercialUserId = companyId)
                every { storeRepository.findById(storeId) } returns java.util.Optional.of(store)

                val response = controller.getStoreById(storeId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.name shouldBe "Test Store"
            }

            it("given non-existent store when getting by id then should return 404 Not Found") {
                every { storeRepository.findById(storeId) } returns java.util.Optional.empty()

                val response = controller.getStoreById(storeId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        describe("createStore") {

            it("given valid store when creating then should return 201 Created") {
                val store = Store(name = "New Store", commercialUserId = companyId)
                every { storeRepository.save(any()) } answers { firstArg() }

                val response = controller.createStore(store)

                response.statusCode shouldBe HttpStatus.CREATED
            }
        }

        describe("updateStore") {

            it("given existing store when updating then should return 200 OK") {
                val store = Store(id = storeId, name = "Updated", commercialUserId = companyId)
                every { storeRepository.existsById(storeId) } returns true
                every { storeRepository.save(any()) } answers { firstArg() }

                val response = controller.updateStore(storeId, store)

                response.statusCode shouldBe HttpStatus.OK
            }

            it("given non-existent store when updating then should return 404 Not Found") {
                val store = Store(id = storeId, name = "Updated", commercialUserId = companyId)
                every { storeRepository.existsById(storeId) } returns false

                val response = controller.updateStore(storeId, store)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        describe("deleteStore") {

            it("given existing store when deleting then should return 204 No Content") {
                every { storeRepository.existsById(storeId) } returns true
                every { storeRepository.deleteById(storeId) } just runs

                val response = controller.deleteStore(storeId)

                response.statusCode shouldBe HttpStatus.NO_CONTENT
            }

            it("given non-existent store when deleting then should return 404 Not Found") {
                every { storeRepository.existsById(storeId) } returns false

                val response = controller.deleteStore(storeId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
})
