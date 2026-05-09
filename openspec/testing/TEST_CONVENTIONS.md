# ═══════════════════════════════════════════════════════════════════════════
# SIGA - Convenciones de Testing
# Estándares para escribir tests consistentes y mantenibles
# ═══════════════════════════════════════════════════════════════════════════

# Tabla de Contenidos
1. [Filosofía](#filosofía)
2. [Estructura de Archivos](#estructura-de-archivos)
3. [Nomenclatura](#nomenclatura)
4. [Anatomía de un Test](#anatomía-de-un-test)
5. [Patrones Common](#patrones-comunes)
6. [Testing en Kotlin](#testing-en-kotlin)
7. [Testing en Svelte](#testing-en-svelte)
8. [Testing en React](#testing-en-react)
9. [Cobertura](#cobertura)

---

## Filosofía

> "Tests son documentación ejecutable. Si no testeas, no documentas."

### Principios:

1. **AAA Pattern**: Arrange → Act → Assert
2. **Test isolation**: Cada test es independiente
3. **One assertion focus**: Un test, una idea
4. **Descriptive names**: El nombre dice qué hace el test
5. **No implementation details**: Testea comportamiento, no implementación

---

## Estructura de Archivos

### Kotlin (Spring Boot)

```
services/
├── auth/
│   ├── src/main/kotlin/com/siga/auth/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   └── src/test/kotlin/com/siga/auth/
│       ├── AuthApplicationTests.kt           # Smoke test
│       ├── controller/
│       │   └── AuthControllerTest.kt
│       ├── service/
│       │   ├── JwtServiceTest.kt
│       │   └── UserServiceTest.kt
│       ├── repository/
│       │   └── UserRepositoryTest.kt
│       ├── integration/
│       │   └── AuthIntegrationTest.kt
│       └── security/                          # Tests de seguridad
│           ├── AuthenticationTest.kt
│           └── AccessControlTest.kt
```

### SvelteKit

```
services/webapp/
├── src/
│   ├── lib/
│   │   ├── components/
│   │   │   └── UserForm.svelte
│   │   └── services/
│   │       └── api.ts
│   └── routes/
│       └── users/
│           └── +page.svelte
└── tests/
    ├── unit/
    │   ├── components/
    │   │   └── UserForm.test.ts
    │   └── services/
    │       └── api.test.ts
    ├── integration/
    │   └── users.test.ts
    └── e2e/
        └── auth.spec.ts
```

### React

```
services/commercial/
├── src/
│   ├── components/
│   │   └── LoginForm.tsx
│   └── pages/
│       └── Login.tsx
└── tests/
    ├── unit/
    │   ├── components/
    │   │   └── LoginForm.test.tsx
    │   └── hooks/
    │       └── useAuth.test.ts
    ├── integration/
    │   └── Login.test.tsx
    └── e2e/
        └── login.spec.ts
```

---

## Nomenclatura

### Formato Given-When-Then

```kotlin
// ✅ CORRECTO - Kotlin
fun `given user exists when getting by id then should return user`()
fun `given invalid email when registering then should throw ValidationException`()
fun `given expired token when validating then should throw TokenExpiredException`()

// ❌ INCORRECTO
fun test1()
fun testGetUser()
fun getUserById()
```

### Svelte/React

```typescript
// ✅ CORRECTO - TypeScript
describe('UserForm', () => {
  it('should display validation error when email is invalid')
  it('should submit form with valid data')
  it('should disable submit button while loading')
})

// ❌ INCORRECTO
it('test1')
it('test submit')
it('click handler')
```

### Archivos de test

```
// Kotlin
UserServiceTest.kt          → Test para UserService
JwtServiceTests.kt          → Tests para JwtService

// Svelte/React
api.test.ts                 → Tests para api.ts
UserForm.test.ts            → Tests para UserForm.svelte
auth.test.tsx               → Tests para auth.tsx
```

---

## Anatomía de un Test

### Template Básico (Kotest)

```kotlin
class UserServiceTest : DescribeSpec({

    describe("UserService") {

        // ─────────────────────────────────────────────────────────
        // Contexto compartido
        // ─────────────────────────────────────────────────────────
        val userRepository = mockk<UserRepository>()
        val passwordService = mockk<PasswordService>()
        val userService = UserService(userRepository, passwordService)

        // ─────────────────────────────────────────────────────────
        // Grupo de tests relacionados
        // ─────────────────────────────────────────────────────────
        describe("create") {

            it("given valid data when creating user then should return user with id") {
                // Arrange (Given)
                val userDto = UserDto(
                    email = "test@siga.cl",
                    name = "Test User",
                    password = "SecureP@ss123"
                )
                every { userRepository.existsByEmail(userDto.email) } returns false
                every { passwordService.hash(userDto.password) } returns "hashed"
                every { userRepository.save(any()) } returns User(id = 1, email = userDto.email)

                // Act (When)
                val result = userService.create(userDto)

                // Assert (Then)
                result.id shouldBe 1
                result.email shouldBe userDto.email
                result.password shouldNotBe userDto.password  // Nunca plain text
            }

            it("given duplicate email when creating user then should throw DuplicateEmailException") {
                // Arrange
                val userDto = UserDto(email = "existing@siga.cl", ...)
                every { userRepository.existsByEmail(userDto.email) } returns true

                // Act & Assert
                shouldThrow<DuplicateEmailException> {
                    userService.create(userDto)
                }
            }
        }

        describe("getById") {

            it("given existing user when getting by id then should return user") {
                // Given
                val userId = 1L
                val expectedUser = User(id = userId, email = "test@siga.cl")
                every { userRepository.findById(userId) } returns Optional.of(expectedUser)

                // When
                val result = userService.getById(userId)

                // Then
                result shouldBe expectedUser
            }

            it("given non-existing user when getting by id then should throw UserNotFoundException") {
                // Given
                every { userRepository.findById(any()) } returns Optional.empty()

                // When & Assert
                shouldThrow<UserNotFoundException> {
                    userService.getById(999)
                }
            }
        }
    }
})
```

### Template Controller (MockMvc)

```kotlin
class UserControllerTest : DescribeSpec({

    describe("UserController") {

        val mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        val objectMapper = ObjectMapper()
        val jwtService = JwtService(secret)

        describe("POST /api/users") {

            it("given valid data when creating user then should return 201 Created") {
                // Given
                val request = CreateUserRequest(
                    email = "new@siga.cl",
                    name = "New User",
                    password = "SecureP@ss123"
                )

                // When/Then
                mockMvc.post("/api/users") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated }
                    jsonPath("$.id") { exists() }
                    jsonPath("$.email") { value("new@siga.cl") }
                    jsonPath("$.password") { doesNotExist() }
                }
            }

            it("given invalid email when creating user then should return 400 Bad Request") {
                // Given
                val request = CreateUserRequest(
                    email = "not-an-email",
                    name = "Test",
                    password = "SecureP@ss123"
                )

                // When/Then
                mockMvc.post("/api/users") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest }
                    jsonPath("$.errors.email") { exists() }
                }
            }
        }
    }
})
```

---

## Patrones Comunes

### 1. Test de CRUD

```kotlin
describe("CRUD operations") {

    var createdUser: User? = null

    it("should create user") {
        createdUser = userService.create(testUserDto)
        createdUser?.id shouldNotBe null
    }

    it("should get created user by id") {
        val user = userService.getById(createdUser!!.id)
        user.email shouldBe testUserDto.email
    }

    it("should update user") {
        val updated = userService.update(
            createdUser!!.id,
            testUserDto.copy(name = "Updated Name")
        )
        updated.name shouldBe "Updated Name"
    }

    it("should delete user") {
        userService.delete(createdUser!!.id)

        shouldThrow<UserNotFoundException> {
            userService.getById(createdUser!!.id)
        }
    }
}
```

### 2. Test de Validación

```kotlin
describe("Validation") {

    it("should reject empty email") {
        shouldThrow<ValidationException> {
            userService.create(testUserDto.copy(email = ""))
        }
    }

    it("should reject invalid email format") {
        val invalidEmails = listOf("notemail", "@nodomain", "no@")
        invalidEmails.forEach { email ->
            shouldThrow<ValidationException> {
                userService.create(testUserDto.copy(email = email))
            }
        }
    }

    it("should reject weak password") {
        val weakPasswords = listOf("12345678", "password", "abc")
        weakPasswords.forEach { password ->
            shouldThrow<ValidationException> {
                userService.create(testUserDto.copy(password = password))
            }
        }
    }
}
```

### 3. Test de Autorización

```kotlin
describe("Authorization") {

    it("should allow admin to access all users") {
        val adminToken = jwtService.generateToken("admin@siga.cl", role = "ADMIN")

        mockMvc.get("/api/users")
            .header("Authorization", "Bearer $adminToken")
            .andExpect { status { isOk } }
    }

    it("should deny regular user access to admin endpoint") {
        val userToken = jwtService.generateToken("user@siga.cl", role = "USER")

        mockMvc.get("/api/admin/users")
            .header("Authorization", "Bearer $userToken")
            .andExpect { status { isForbidden } }
    }

    it("should deny access without authentication") {
        mockMvc.get("/api/users")
            .andExpect { status { isUnauthorized } }
    }
}
```

### 4. Test de Edge Cases

```kotlin
describe("Edge cases") {

    it("should handle empty list") {
        every { userRepository.findAll() } returns emptyList()

        val result = userService.getAll()

        result shouldBe emptyList()
    }

    it("should handle paginated results") {
        val page = PageImpl(listOf(testUser, testUser2))
        every { userRepository.findAll(any<Pageable>()) } returns page

        val result = userService.getPage(PageRequest.of(0, 10))

        result.content shouldHaveSize 2
        result.totalPages shouldBe 1
    }

    it("should handle concurrent modifications") {
        // Given
        val user = userService.create(testUserDto)

        // When: Simulamos que alguien modificó el usuario entre读取 y更新
        every { userRepository.findById(user.id) } returnsMany listOf(
            Optional.of(user.copy(version = 0)),
            Optional.of(user.copy(version = 1))
        )

        // Then: Optimistic locking debería funcionar
        shouldThrow<OptimisticLockException> {
            userService.update(user.id, testUserDto.copy(name = "Updated"))
        }
    }
}
```

---

## Testing en Kotlin

### Dependencias (build.gradle.kts)

```kotlin
dependencies {
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:DbSetup:2.1.0")  // Test DB
    testRuntimeOnly("com.h2database:h2")  // In-memory DB
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

### Ejemplo completo con Kotest

```kotlin
package com.siga.auth.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.mockk
import io.mockk.every

class UserServiceTest : DescribeSpec({

    val userRepository = mockk<UserRepository>()
    val passwordService = mockk<PasswordService>()
    val mailService = mockk<MailService>()
    val userService = UserService(userRepository, passwordService, mailService)

    describe("UserService") {

        describe("create") {

            val validUserDto = UserDto(
                email = "test@siga.cl",
                name = "Test User",
                password = "SecureP@ss123"
            )

            it("given valid data when creating user then should return user with id") {
                // Arrange
                every { userRepository.existsByEmail(validUserDto.email) } returns false
                every { passwordService.hash(validUserDto.password) } returns "hashed123"
                every { userRepository.save(any()) } answers {
                    firstArg<User>().copy(id = 1)
                }

                // Act
                val result = userService.create(validUserDto)

                // Assert
                result.id shouldBe 1
                result.email shouldBe validUserDto.email
                result.password shouldNotBe validUserDto.password
            }

            it("given duplicate email when creating user then should throw DuplicateEmailException") {
                // Arrange
                every { userRepository.existsByEmail(validUserDto.email) } returns true

                // Act & Assert
                shouldThrow<DuplicateEmailException> {
                    userService.create(validUserDto)
                }
            }

            it("given weak password when creating user then should throw WeakPasswordException") {
                // Arrange
                val weakPassword = "12345678"
                every { userRepository.existsByEmail(any()) } returns false

                // Act & Assert
                shouldThrow<WeakPasswordException> {
                    userService.create(validUserDto.copy(password = weakPassword))
                }
            }
        }

        describe("getById") {

            it("given existing user when getting by id then should return user") {
                // Arrange
                val userId = 1L
                val expectedUser = User(id = userId, email = "test@siga.cl", name = "Test")
                every { userRepository.findById(userId) } returns java.util.Optional.of(expectedUser)

                // Act
                val result = userService.getById(userId)

                // Assert
                result shouldBe expectedUser
            }

            it("given non-existing user when getting by id then should throw UserNotFoundException") {
                // Arrange
                every { userRepository.findById(any()) } returns java.util.Optional.empty()

                // Act & Assert
                shouldThrow<UserNotFoundException> {
                    userService.getById(999)
                }
            }
        }

        describe("delete") {

            it("given existing user when deleting then should remove from database") {
                // Arrange
                val userId = 1L
                every { userRepository.existsById(userId) } returns true
                every { userRepository.deleteById(userId) } returns Unit

                // Act
                userService.delete(userId)

                // Assert
                every { userRepository.findById(userId) } returns java.util.Optional.empty()
            }

            it("given non-existing user when deleting then should throw UserNotFoundException") {
                // Arrange
                every { userRepository.existsById(any()) } returns false

                // Act & Assert
                shouldThrow<UserNotFoundException> {
                    userService.delete(999)
                }
            }
        }
    }
})
```

---

### BehaviorSpec

Kotest provides `BehaviorSpec` as a first-class spec style that maps directly to Given-When-Then (GWT) notation. Unlike `DescribeSpec` (nested `describe`-`it` blocks), `BehaviorSpec` uses `given`-`` `When` ``-`then` chains — identical to GWT scenario structure.

#### Import

```kotlin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
```

#### Template

```kotlin
package com.siga.bdd.auth

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class LoginBehaviorSpec : BehaviorSpec({

    given("a registered user") {
        `When`("logging in with valid credentials") {
            then("should return a JWT token") {
                pending { }
            }
            then("should set a session cookie") {
                pending { }
            }
        }
    }

    given("a registered user") {
        `When`("logging in with wrong password") {
            then("should return 401 Unauthorized") {
                pending { }
            }
        }
    }

    given("an unregistered email") {
        `When`("attempting to log in") {
            then("should return 404 Not Found") {
                pending { }
            }
        }
    }
})
```

#### File naming convention

```
openspec/changes/{change-name}/specs/{domain}/{Feature}BehaviorSpec.kts
```

Generated stubs live in `openspec/` alongside spec markdown, NOT in the source test tree. This signals they are scaffolded stubs, not hand-written tests.

#### GWT markdown → BehaviorSpec mapping

| GWT in spec | BehaviorSpec code |
|-------------|-------------------|
| `- GIVEN {precondition}` | `given("{precondition}") {` |
| `- WHEN {action}` | `` `When`("{action}") { `` |
| `- THEN {outcome}` | `then("{outcome}") { pending { } }` |
| `- AND {outcome}` | `then("{outcome}") { pending { } }` (also `pending { }`) |

> **Note:** `` `When` `` is backtick-quoted because `When` is a Kotlin soft keyword. Kotest exposes it as a valid DSL function when quoted.

#### Coexistence with DescribeSpec

`BehaviorSpec` and `DescribeSpec` live in the **same project, same test suite, same Gradle module**. They are both Kotest spec styles — no additional configuration or dependency is needed:

| Spec style | Use case | Structure |
|-----------|----------|-----------|
| `DescribeSpec` | Unit tests with nested contexts | `describe` → `it` |
| `BehaviorSpec` | BDD scenarios mirroring GWT specs | `given` → `` `When` `` → `then` |

Both run under `kotest-runner-junit5`. A project can mix freely:

```kotlin
// Unit test — DescribeSpec
class UserServiceTest : DescribeSpec({
    describe("create") {
        it("should return user when data is valid") { ... }
    }
})

// BDD scenario — BehaviorSpec
class LoginBehaviorSpec : BehaviorSpec({
    given("valid credentials") {
        `When`("logging in") {
            then("should succeed") { pending { } }
        }
    }
})
```

There is no need to convert existing `DescribeSpec` tests. `BehaviorSpec` is purely additive — new scenarios go in `BehaviorSpec`, existing unit tests stay in `DescribeSpec`.

---

## Testing en Svelte

### Setup (vitest.config.ts)

```typescript
import { defineConfig } from 'vitest/config'
import { svelteTesting } from '@testing-library/svelte'

export default defineConfig({
  plugins: [svelteTesting()],
  test: {
    environment: 'jsdom',
    include: ['tests/**/*.test.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
    },
  },
})
```

### Ejemplo de test

```typescript
// tests/unit/components/UserForm.test.ts
import { render, screen, fireEvent } from '@testing-library/svelte'
import { vi } from 'vitest'
import UserForm from '$lib/components/UserForm.svelte'

describe('UserForm', () => {
  const mockSubmit = vi.fn()

  it('should display validation error for invalid email', async () => {
    // Render component
    render(UserForm, {
      props: {
        onSubmit: mockSubmit,
      },
    })

    // Get elements
    const emailInput = screen.getByLabelText(/email/i)
    const submitButton = screen.getByRole('button', { name: /submit/i })

    // Fill invalid email
    await fireEvent.input(emailInput, { target: { value: 'not-an-email' } })
    await fireEvent.click(submitButton)

    // Assert
    expect(screen.getByText(/invalid email/i)).toBeInTheDocument()
    expect(mockSubmit).not.toHaveBeenCalled()
  })

  it('should submit form with valid data', async () => {
    render(UserForm, {
      props: {
        onSubmit: mockSubmit,
      },
    })

    const emailInput = screen.getByLabelText(/email/i)
    const nameInput = screen.getByLabelText(/name/i)
    const submitButton = screen.getByRole('button', { name: /submit/i })

    await fireEvent.input(emailInput, { target: { value: 'test@siga.cl' } })
    await fireEvent.input(nameInput, { target: { value: 'Test User' } })
    await fireEvent.click(submitButton)

    expect(mockSubmit).toHaveBeenCalledWith({
      email: 'test@siga.cl',
      name: 'Test User',
    })
  })

  it('should disable submit button while loading', async () => {
    const slowSubmit = vi.fn().mockImplementation(() => new Promise(r => setTimeout(r, 1000)))

    render(UserForm, {
      props: {
        onSubmit: slowSubmit,
      },
    })

    const submitButton = screen.getByRole('button', { name: /submit/i })

    await fireEvent.click(submitButton)

    expect(submitButton).toBeDisabled()
    expect(submitButton).toHaveTextContent(/loading/i)
  })
})
```

---

## Testing en React

### Setup (vitest.config.ts)

```typescript
import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    include: ['tests/**/*.test.{ts,tsx}'],
  },
})
```

### Ejemplo de test

```typescript
// tests/unit/components/LoginForm.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { vi, describe, it, expect } from 'vitest'
import { LoginForm } from '@/components/LoginForm'

describe('LoginForm', () => {
  const mockLogin = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should display validation errors for empty fields', async () => {
    render(<LoginForm onLogin={mockLogin} />)

    const submitButton = screen.getByRole('button', { name: /ingresar/i })
    fireEvent.click(submitButton)

    expect(screen.getByText(/el email es requerido/i)).toBeInTheDocument()
    expect(screen.getByText(/la contraseña es requerida/i)).toBeInTheDocument()
  })

  it('should call onLogin with valid credentials', async () => {
    render(<LoginForm onLogin={mockLogin} />)

    const emailInput = screen.getByLabelText(/email/i)
    const passwordInput = screen.getByLabelText(/contraseña/i)
    const submitButton = screen.getByRole('button', { name: /ingresar/i })

    fireEvent.change(emailInput, { target: { value: 'test@siga.cl' } })
    fireEvent.change(passwordInput, { target: { value: 'SecureP@ss123' } })
    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({
        email: 'test@siga.cl',
        password: 'SecureP@ss123',
      })
    })
  })

  it('should show error message on login failure', async () => {
    mockLogin.mockRejectedValueOnce(new Error('Credenciales inválidas'))

    render(<LoginForm onLogin={mockLogin} />)

    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: 'test@siga.cl' },
    })
    fireEvent.change(screen.getByLabelText(/contraseña/i), {
      target: { value: 'wrong-password' },
    })
    fireEvent.click(screen.getByRole('button', { name: /ingresar/i }))

    await waitFor(() => {
      expect(screen.getByText(/credenciales inválidas/i)).toBeInTheDocument()
    })
  })
})
```

---

## Cobertura

### Objetivos

| Servicio | Cobertura mínima |
|----------|------------------|
| auth | 90% |
| inventory | 80% |
| sales | 80% |
| billing | 80% |
| gateway | 70% |
| webapp | 70% |
| commercial | 70% |

### Configuración Jacoco (build.gradle.kts)

```kotlin
plugins {
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test> {
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/entity/*",
                    "**/dto/*",
                    "**/*Application*",
                    "**/config/*"
                )
            }
        }))
    }
}

// Verificación obligatoria
tasks.register<JacocoViolationCheckTask>("check") {
    mustHave = mapOf(
        "com.siga.auth" to 0.90,
        "com.siga.inventory" to 0.80,
        "com.siga.sales" to 0.80
    )
}
```

### Verificación en CI

```bash
# Verificar cobertura
./gradlew check

# Reporte HTML
open build/reports/jacoco/index.html

# Excluir de cobertura
jacoco {
    exclusion = [
        "**/entity/*",
        "**/dto/*",
        "**/*Application*.class",
        "**/config/*"
    ]
}
```

---

## Checklist de Quality Gates

### Antes de hacer PR:

- [ ] Todos los tests pasando
- [ ] Coverage >= mínimo requerido
- [ ] No new code smell (sonar)
- [ ] Imports organizados
- [ ] Sin console.log/println

### En reviews:

- [ ] Tests tienen buena nomenclatura
- [ ] Tests son independientes
- [ ] Tests cubren casos edge
- [ ] Tests verifican comportamiento, no implementación

---

## Recursos

- [Kotest Docs](https://kotest.io/docs/)
- [MockK Docs](https://mockk.io/)
- [Vitest Docs](https://vitest.dev/)
- [Testing Library](https://testing-library.com/)
- [Arrange Act Assert](https://arrangeactassert.com/)