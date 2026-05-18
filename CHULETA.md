# 🥩 CHULETA SIGA (Cheat Sheet)

Guía rápida y al grano para no perderse en la arquitectura del monorepo SIGA.

## 🏗️ Arquitectura General
- **Patrón Principal**: Microservicios independientes.
- **Diseño Interno**: **Arquitectura Hexagonal** (Ports & Adapters) en los servicios de negocio.
  - *Dominio*: Reglas de negocio puras (cero dependencias externas).
  - *Puertos*: Interfaces (contratos) que dictan qué necesita el dominio.
  - *Adaptadores*: Implementaciones de los puertos (ej. JPA, Kafka, REST).
  - *Casos de Uso*: Orquestan el flujo de las operaciones.
- **Comunicación Sincrónica**: Vía REST API a través del API Gateway.
- **Comunicación Asincrónica**: Eventos emitidos por Kafka (Patrón SAGA).

---

## 🧩 Los Microservicios (¿Qué hace cada uno?)

| Servicio | Puerto | Función Principal | Base de Datos Propia |
|---|---|---|---|
| **`siga-registry`** | `8761` | **El Directorio Telefónico (Eureka)**. Todos los servicios le avisan "estoy vivo y esta es mi IP". El Gateway le pregunta dónde mandar el tráfico. | ❌ No tiene |
| **`siga-gateway`** | `8080` | **La Puerta de Entrada**. Valida los tokens JWT, maneja el CORS y enruta la petición al microservicio correcto. | ❌ No tiene |
| **`siga-auth`** | `8081` | **El Guardián (Identidad)**. Maneja login, registro, emite los JWT, valida emails y gestiona los permisos y roles (Dueños vs Empleados). | ✅ `siga_auth` |
| **`siga-inventory`** | `8082` | **La Bodega (Stock)**. Maestro de productos, controla las mermas, el stock, y las transferencias entre sucursales. | ✅ `siga_inventory` |
| **`siga-sales`** | `8083` | **La Caja Registradora (POS)**. Maneja el carrito, procesa ventas, cobros y emite boletas/facturas a los clientes finales. | ✅ `siga_sales` |
| **`siga-billing`** | `8084` | **La Suscripción (SaaS)**. Cobra la mensualidad de SIGA a las PYMEs, controla los planes (Básico, Premium). | ✅ `siga_billing` |
| **`siga-agent`** | `8000` | **El Cerebro (IA)**. Escrito en Python. Asistente RAG para responder preguntas estratégicas ("¿Qué producto se vende más?"). | ✅ `siga_agent` |

---

## 🕸️ Componentes de Infraestructura (Local)

### 🐘 Base de Datos (PostgreSQL)
- Levantamos **un solo contenedor** de Postgres, pero adentro cada microservicio tiene su **propia base de datos lógica** y su **propio esquema**.
- **Regla Estricta**: Ningún servicio puede leer las tablas de otro servicio. Si `Sales` necesita saber el nombre de un usuario, se lo pregunta a `Auth` vía API o evento.

### 📨 Apache Kafka (El Cartero de Eventos)
**¿Para qué sirve?**
Para que los servicios se avisen cosas sin quedarse esperando (Asincronismo) y para manejar transacciones distribuidas (SAGA).
- **Ejemplo Práctico**:
  1. Haces una venta en `siga-sales`.
  2. `Sales` manda un mensaje a Kafka: *"Hey, vendí 3 Coca-Colas (Evento: SALE_COMPLETED)"*.
  3. `Sales` le responde de inmediato al cliente "Venta exitosa", sin esperar al inventario.
  4. `Inventory`, que está escuchando a Kafka, recibe el mensaje y descuenta 3 Coca-Colas de su base de datos.
  5. *¿Por qué no directo?* Porque si `Inventory` está caído o muy lento, la venta NO se cae. El mensaje queda guardado en Kafka y cuando `Inventory` vuelva a encenderse, se pone al día y descuenta las Coca-Colas.

### 🐳 ContainerFlow (`siga-ops`)
- **El Vigía (Dashboard Local)**. Corre en `localhost:9470`. Sirve para ver qué contenedores están encendidos, cuánta RAM gastan y leer sus logs en tiempo real sin usar la consola. Súper ligero y vital para revisar fallos de arranque.
