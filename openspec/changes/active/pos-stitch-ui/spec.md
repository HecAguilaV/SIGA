# Specifications: POS UI & Integration (Stitch)

## 1. Cash Shifts (Turnos de Caja)

### Requirement: Shift-Required Operation
El sistema MUST impedir cualquier venta si no hay un turno de caja (cash shift) abierto para el usuario y local actual.

#### Scenario: Blocking sale without shift
- GIVEN un usuario con rol `CASHIER`
- AND el endpoint `GET /api/v1/sales/cash-shifts/active` retorna 404
- WHEN el usuario intenta agregar un item al carrito
- THEN el sistema debe mostrar un aviso de "Apertura de Caja Requerida"
- AND deshabilitar el botón de "Finalizar Venta".

#### Scenario: Opening a shift
- GIVEN un aviso de "Apertura de Caja Requerida"
- WHEN el usuario ingresa un monto inicial (ej: $50,000) y confirma
- THEN el sistema debe enviar `POST /api/v1/sales/cash-shifts`
- AND habilitar la interfaz de venta tras recibir una respuesta 201.

---

## 2. Checkout & SAGA Flow

### Requirement: Asynchronous Success Feedback
El POS MUST manejar el estado transaccional de la venta considerando que la persistencia y el descuento de stock son asíncronos (Kafka SAGA).

#### Scenario: Checkout processing
- GIVEN un carrito con 3 items
- WHEN el usuario presiona "Finalizar Venta"
- THEN el sistema envía `POST /api/v1/sales/checkout`
- AND muestra un estado de "Procesando venta..." con la animación de escaneo (Stitch).
- AND bloquea cambios en el carrito.

#### Scenario: Sale completed (via SSE/BFF)
- GIVEN una venta en estado "Procesando"
- WHEN el BFF recibe un evento `StockReserved` de Kafka y lo propaga vía SSE
- THEN el POS debe mostrar "Venta Exitosa", vaciar el carrito y permitir una nueva venta.

---

## 3. Stitch Design Language (Isolated)

### Requirement: Isolated Theming
La interfaz del POS MUST aplicar el estándar visual "Stitch" sin afectar los componentes globales del dashboard.

#### Scenario: POS Styling
- GIVEN la ruta `/pos`
- WHEN el componente carga
- THEN debe inyectar las fuentes `Hanken Grotesk` y `Material Symbols`
- AND aplicar los colores Teal (`#009579`) y Dark Blue (`#070a61`) definidos en el mockup.
- AND usar bordes redondeados `xl` y efectos de glassmorphism en los paneles.
