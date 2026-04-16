# Tasks: Descomposición de Monolito SIGA (Polyglot)

Este checklist detalla los pasos técnicos para extraer los 5 microservicios definitivos del backend actual.

## Fase 1: Infraestructura de Soporte
- [x] **1.1 Configurar Service Registry (Eureka)**
    - [x] Crear módulo `services/registry` (Kotlin).
    - [x] Configurar anotación `@EnableEurekaServer`.
    - [x] Añadir a `docker-compose.yml`.
- [x] **1.2 Configurar API Gateway**
    - [x] Crear módulo `services/gateway` (Kotlin).
    - [x] Implementar `DiscoveryClient` enrutando mediante Eureka.
    - [x] Configurar CORS global y delegación de headers JWT (`X-Tenant-Id`).

## Fase 2: Extracción de Dominios Core (Strangler Fig)

### 2.1 Dominio de Identidad (`siga-auth`)
- [x] Crear módulo `services/auth` (Kotlin).
- [x] Migrar entidades: `UsuarioSaas`, `UsuarioComercial`, `Permiso`.
- [x] Implementar emisión de JWT firmados y OAuth2 genérico.
- [ ] Implementar OIDC Client para **Google Login / Registro (SSO)** para `UsuarioComercial` (Dueños).
- [ ] Validar esquema de username/pwd estándar para `UsuarioSaas` (Empleados).

### 2.2 Dominio de Inventario (`siga-inventario`)
- [x] Crear módulo `services/inventario` (Kotlin).
- [x] Migrar DB Entities: `Producto`, `Categoria`, `Local`, `Stock`.
- [x] Exponer API REST filtrada por `tenant_id` (vía JWT).

### 2.3 Dominio Transaccional (`siga-ventas`)
- [x] Crear módulo `services/ventas` (Kotlin).
- [x] Integrar consumo REST síncrono a `siga-inventario` para validar stock.

## Fase 3: Polyglot AI (Strands + Python)

### 3.1 Agente Autónomo AI (`siga-agente`)
- [x] Crear contenedor y carpeta `services/agente` (Python/FastAPI).
- [x] Instalar cliente de Eureka (`py_eureka_client`).
- [x] Implementar framework Strands + Ollama Cloud.
- [x] **Bots Especializados**:
  - [x] Implementar el `Analista` (Herramientas KPI / Extracción de métricas de negocio para dueños).
  - [x] Implementar el `Operador` (Herramientas transaccionales: stock, registrar venta).

### 3.2 Microservicio de Resiliencia y Estabilidad (`siga-fallback`)
- [ ] Implementar Circuit Breakers (Resilience4j / Timeouts) en Gateway o servicios.
- [ ] Desarrollar respuestas heurísticas crudas en caso de falla o alta latencia del LLM en `siga-agente`.

## Fase 4: Integración UI y Verificación
- [ ] **4.1 Integración de Accesibilidad**
    - [ ] Convertir POJO A11Y Toolbar a componente Svelte (Webapp).
    - [ ] Convertir POJO A11Y Toolbar a componente React (Comercial).
- [ ] **4.2 Verificación de Comunicación Polyglot**
    - [ ] Comprobar flujo de Gateway -> Agente (Python) -> Inventario (Kotlin).
