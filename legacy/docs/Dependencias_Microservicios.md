# Dependencias de Microservicios SIGA

Este documento detalla las dependencias identificadas en el ecosistema de microservicios del proyecto SIGA. Se han categorizado según su uso común en la arquitectura base y las exclusivas de cada servicio específico.

## 1. Ecosistema Spring Boot / Kotlin
Los servicios principales del backend (`auth`, `ventas`, `inventario`, `backend`, `gateway`, `registry`, `common`) están basados en Spring Boot 3.2.0 y Kotlin 1.9.22.

### 1.1 Dependencias Comunes (Transversales)
Estas dependencias se repiten en la mayoría o la totalidad de los microservicios Spring Boot:

- **Lenguaje y Core:**
  - `org.jetbrains.kotlin:kotlin-reflect`
  - `org.jetbrains.kotlin:kotlin-stdlib-jdk8`
- **Cloud & Descubrimiento:**
  - `org.springframework.cloud:spring-cloud-dependencies:2023.0.0`
  - `org.springframework.cloud:spring-cloud-starter-netflix-eureka-client` *(Presente en todos excepto en el Registry)*
- **Testing:**
  - `org.springframework.boot:spring-boot-starter-test`

### 1.2 Dependencias Comunes (Servicios de Negocio y Datos)
Compartidas entre `auth`, `ventas`, `inventario` y `backend`:

- **Web & API:**
  - `org.springframework.boot:spring-boot-starter-web`
  - `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0` (Swagger/OpenAPI)
- **Persistencia de Datos:**
  - `org.springframework.boot:spring-boot-starter-data-jpa`
  - `org.postgresql:postgresql:42.7.1`
- **Seguridad & Autenticación:**
  - `org.springframework.boot:spring-boot-starter-security`
  - `com.auth0:java-jwt:4.4.0`
- **Librería Interna:**
  - `com.siga:siga-common` (Audit Trail y utilidades compartidas)

### 1.3 Dependencias Exclusivas por Servicio

#### Gateway (`gateway`)
Enfocado en el enrutamiento reactivo:
- `org.springframework.cloud:spring-cloud-starter-gateway`
- `org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0` *(Variante WebFlux para Swagger)*

#### Service Registry (`registry`)
Servidor de descubrimiento de Eureka:
- `org.springframework.cloud:spring-cloud-starter-netflix-eureka-server`

#### Backend (Monolito Refactorizado / Core) (`backend`)
Al ser el núcleo principal/monolito en proceso de migración, posee dependencias adicionales complejas:
- **Validación:** `spring-boot-starter-validation`
- **Cliente HTTP Reactivo (Ej. Gemini API):** `spring-boot-starter-webflux`
- **Monitoreo:** `spring-boot-starter-actuator`
- **Concurrencia y Corrutinas:** `kotlinx-coroutines-reactor:1.7.3`, `kotlinx-coroutines-test`
- **Serialización:** `jackson-module-kotlin`
- **Criptografía de contraseñas:** `org.mindrot:jbcrypt:0.4`
- **Testing avanzado:** `mockito-kotlin:5.1.0`

#### Autenticación (`auth`)
- `org.springframework.boot:spring-boot-starter-oauth2-client`

#### Ventas (`ventas`)
- **Comunicación Síncrona entre servicios:** `spring-cloud-starter-openfeign`
- **Validación JWT (Resource Server):** `spring-boot-starter-oauth2-resource-server`

#### Inventario (`inventario`)
- **Validación JWT (Resource Server):** `spring-boot-starter-oauth2-resource-server`

---

## 2. Servicios con Stacks Diferentes

### 2.1 Agente IA (`agente`) - Python
Servicio de inteligencia artificial basado en Python:
- **Framework Web:** `fastapi==0.111.0`, `uvicorn==0.30.1`
- **Descubrimiento (Eureka para Python):** `py-eureka-client==0.13.3`
- **Datos y Embeddings:** `pydantic`, `psycopg[binary,pool]`, `pgvector`
- **Modelos de IA:** `sentence-transformers`, `strands-agents`, `strands-agents-tools`
- **Testing/HTTP:** `pytest`, `httpx`

### 2.2 Aplicación Móvil (`mobile`) - Kotlin Multiplatform/Android
Aplicación móvil nativa en Kotlin:
- **UI & Diseño:** `androidx.compose.material3`, `androidx.activity-compose`
- **Networking (Cliente HTTP):** `io.ktor:ktor-client-android`, `ktor-client-content-negotiation` (Ktor 3.0.1)
- **Serialización:** `kotlinx-serialization-json`

### 2.3 Web Comercial (`comercial`) - React
Frontend basado en Node.js (Vite + React):
- **Core UI:** `react`, `react-dom`, `react-router-dom`
- **Estilos y Gráficos:** `bootstrap`, `recharts`, `phosphor-react`
- **IA y Documentación:** `@google/generative-ai`, `swagger-ui-react`
- **Tooling:** `vite`, `karma` (Testing), `webpack`

### 2.4 WebApp Prototipo (`webapp`) - SvelteKit
Frontend alternativo / Dashboard basado en Svelte:
- **Core UI:** `svelte`, `@sveltejs/kit`
- **Estilos y Gráficos:** `bulma`, `chart.js`, `phosphor-svelte`
- **Tooling:** `vite`, `vitest` (Testing)
