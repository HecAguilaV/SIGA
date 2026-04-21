# Specifications: Arquitectura Microservicios SIGA v2.1

## 1. Gobernanza de Datos (Esquemas)

### Requirement: Aislamiento por Esquema
Cada microservicio MUST operar exclusivamente sobre su esquema PostgreSQL asignado.

#### Scenario: Acceso a Inventario
- GIVEN el microservicio `siga-inventario`
- WHEN realiza una operación de persistencia
- THEN debe utilizar el esquema `siga_inventario` y NUNCA `siga_saas` o `public`.

---

## 2. Agente de IA y Resiliencia

### Requirement: Herencia de Permisos del Agente
El Agente de IA MUST ejecutar acciones solo si el usuario humano tiene el permiso granular correspondiente.

#### Scenario: Ejecución de Acción CRUD
- GIVEN un usuario con permiso `PRODUCTO_CREAR`
- WHEN el usuario solicita al Agente: "Crea un producto nuevo llamado 'Aceite'"
- THEN el Agente debe validar el permiso del usuario antes de ejecutar la acción en `siga-inventario`.

### Requirement: Fallback de Consultas
El sistema MUST proporcionar una respuesta válida incluso si el servicio de IA falla.

#### Scenario: Fallo de Inferencia
- GIVEN el servicio `siga-agente` no responde o retorna un error
- WHEN el usuario hace una pregunta sobre stock
- THEN el servicio `siga-fallback` debe interceptar la falla, ejecutar una Query SQL directa y retornar el dato real con un mensaje de cortesía.

---

## 3. Microservicio de Ventas (POS)

### Requirement: Descuento de Stock en Tiempo Real
Cada venta realizada en el POS MUST gatillar una actualización de stock en el servicio de inventario.

#### Scenario: Venta Exitosa
- GIVEN una venta de 5 unidades de "Producto X"
- WHEN la transacción se confirma en `siga-ventas`
- THEN se debe enviar un evento/petición a `siga-inventario` para descontar las 5 unidades del local correspondiente.
