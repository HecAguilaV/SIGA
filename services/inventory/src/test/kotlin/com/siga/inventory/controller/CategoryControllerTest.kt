package com.siga.inventory.controller

import com.siga.inventory.entity.Category
import com.siga.inventory.repository.CategoryRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.springframework.http.HttpStatus
import java.util.UUID

class CategoryControllerTest : DescribeSpec({

    val categoryRepository = mockk<CategoryRepository>()
    val controller = CategoryController(categoryRepository)

    val categoryId = UUID.randomUUID()
    val companyId = UUID.randomUUID()

    describe("CategoryController") {

        describe("getAllCategories") {

            it("given existing categories when getting all then should return 200 OK with list") {
                val categories = listOf(
                    Category(id = categoryId, name = "Category 1", commercialUserId = companyId)
                )
                every { categoryRepository.findAll() } returns categories

                val response = controller.getAllCategories()

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }

            it("given no categories when getting all then should return 200 OK with empty list") {
                every { categoryRepository.findAll() } returns emptyList()

                val response = controller.getAllCategories()

                response.statusCode shouldBe HttpStatus.OK
                response.body?.isEmpty() shouldBe true
            }
        }

        describe("getCategoryById") {

            it("given existing category when getting by id then should return 200 OK") {
                val category = Category(id = categoryId, name = "Beverages", commercialUserId = companyId)
                every { categoryRepository.findById(categoryId) } returns java.util.Optional.of(category)

                val response = controller.getCategoryById(categoryId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.name shouldBe "Beverages"
            }

            it("given non-existent category when getting by id then should return 404 Not Found") {
                every { categoryRepository.findById(categoryId) } returns java.util.Optional.empty()

                val response = controller.getCategoryById(categoryId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        describe("createCategory") {

            it("given valid category when creating then should return 201 Created") {
                val category = Category(name = "New Category", commercialUserId = companyId)
                every { categoryRepository.save(any()) } answers { firstArg() }

                val response = controller.createCategory(category)

                response.statusCode shouldBe HttpStatus.CREATED
            }
        }

        describe("updateCategory") {

            it("given existing category when updating then should return 200 OK") {
                val category = Category(id = categoryId, name = "Updated", commercialUserId = companyId)
                every { categoryRepository.existsById(categoryId) } returns true
                every { categoryRepository.save(any()) } answers { firstArg() }

                val response = controller.updateCategory(categoryId, category)

                response.statusCode shouldBe HttpStatus.OK
            }

            it("given non-existent category when updating then should return 404 Not Found") {
                val category = Category(id = categoryId, name = "Updated", commercialUserId = companyId)
                every { categoryRepository.existsById(categoryId) } returns false

                val response = controller.updateCategory(categoryId, category)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        describe("deleteCategory") {

            it("given existing category when deleting then should return 204 No Content") {
                every { categoryRepository.existsById(categoryId) } returns true
                every { categoryRepository.deleteById(categoryId) } just runs

                val response = controller.deleteCategory(categoryId)

                response.statusCode shouldBe HttpStatus.NO_CONTENT
            }

            it("given non-existent category when deleting then should return 404 Not Found") {
                every { categoryRepository.existsById(categoryId) } returns false

                val response = controller.deleteCategory(categoryId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
})
