# Tasks: Descomposición de Monolito SIGA

Este checklist detalla los pasos técnicos para extraer los 5 microservicios definitivos del backend actual.

## Fase 1: Infraestructura de Soporte
- [ ] **1.1 Configurar Service Registry (Eureka)**
    - [ ] Crear módulo `services/registry`.
    - [ ] Configurar anotación `@EnableEurekaServer`.
    - [ ] Añadir a `docker-compose.yml`.
- [ ] **1.2 Configurar API Gateway**
    - [ ] Crear módulo `services/gateway`.
    - [ ] Implementar `DiscoveryClient` para ruteo dinámico.
    - [ ] Configurar CORS global y validación inicial de JWT.

## Fase 2: Extracción de Dominios (Strangler Fig)

### 2.1 Dominio de Identidad (`siga-auth`)
- [ ] Crear módulo `services/auth`.
- [ ] Migrar entidades: `UsuarioSaas`, `UsuarioComercial`, `Permiso`.
- [ ] Implementar endpoints de Login y Token Exchange.
- [ ] **Desafío**: Romper dependencia directa con el esquema del monolito.

### 2.2 Dominio de Inventario (`siga-inventario`)
- [ ] Crear módulo `services/inventario`.
- [ ] Migrar entidades: `Producto`, `Categoria`, `Local`, `Stock`.
- [ ] Implementar lógica de movimientos de stock.
- [ ] Exponer API para consulta desde el Asistente.

### 2.3 Dominio Transaccional (`siga-ventas`)
- [ ] Crear módulo `services/ventas`.
- [ ] Migrar entidades: `Venta`, `Factura`.
- [ ] **Integración**: Consumir `siga-inventario` vía REST para validar stock antes de la venta.

## Fase 3: Inteligencia y UX
- [ ] **3.1 Asistente AI (`siga-asistente`)**
    - [ ] Migrar lógica de integración con Google Gemini.
    - [ ] Implementar RAG consultando al servicio de Inventario.
- [ ] **3.2 Integración de Accesibilidad**
    - [ ] Convertir POJO A11Y Toolbar a componente Svelte (Webapp).
    - [ ] Convertir POJO A11Y Toolbar a componente React (Comercial).

## Fase 4: Verificación y Cierre
- [ ] Pruebas de integración entre Gateway y servicios.
- [ ] Verificación de flujo completo: Login -> Consulta Inventario -> Venta.
