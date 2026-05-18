# Historias de Usuario — SIGA

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../en/USER_STORIES.md)*

> **Propósito**: Capturar la perspectiva del usuario final como complemento al Manifiesto Core y las especificaciones SDD.
> Cada historia incluye criterios de aceptación en formato **Given/When/Then** listos para traducirse a Kotest BehaviorSpec.
>
> **Origen**: Estas historias nacen de la experiencia directa de trabajo en una PYME chilena del rubro de alimentación y concesiones — no de plantillas genéricas. Los actores son personas reales.

---

## Personas

| Persona | Rol | Contexto Real | Pain Point |
|---------|-----|---------------|------------|
| **Elizabeth** | Dueña de PYME | Administra múltiples casinos licitados, eventos de banquetería, kioskos y una cocina central. Trabaja de 6:00 a 00:00. No es nativa digital. Todo lo gestiona con libreta, anotaciones y WhatsApp. Excel ni hablar. Los domingos por la tarde intenta armar listas de distribución de mercadería. | Pierde horas comparando precios de proveedores con su libreta en mano, preguntando por WhatsApp qué falta en cada punto. No tiene visibilidad del stock real. |
| **Héctor** | Chofer repartidor, administrador de kioskos, encargado de inventario | 95% de su tiempo repartiendo mercadería. Sin tiempo para gestionar kioskos ni ingresar stock. Encontró desfases masivos: productos que el sistema mostraba con stock, en la realidad no existían; y productos que figuraban como agotados tenían sobrestock y caducados. El ERP era lento, con mala UX: filtros se perdían al guardar, búsquedas case-sensitive. Sin política de códigos, se duplicaban productos con distintos códigos y precios. | El sistema no refleja la realidad. Perpetúa el desfase en vez de corregirlo. |
| **Yesenia** | Operaria/cajera de kiosko | Registra ventas, solicita agregar productos nuevos que la dueña compra sin informar. Debe trackear manualmente cuánto stock queda. Encontraba discrepancias por productos duplicados con distintos códigos. Búsquedas case-sensitive la confundían. | No puede vender lo que no está en el sistema, y no puede encontrar lo que sí está. |
| **Luis** | Chofer repartidor y comprador | Trabaja lunes a viernes. Todo se coordina por voz o WhatsApp con Elizabeth. Algunas cocinas le avisan directamente cuando falta algo. Él pide a la bodeguera. | Sin registro formal de lo que entrega ni lo que le piden. Todo es verbal. |
| **Antonia** | Encargada de bodega | Gestiona la bodega central, entrega productos a casinos, cocina y kioskos. Sin gestión en papel ni digital: todo de boca a boca. Los productos existían en el ERP pero nadie los gestionaba. | Cero trazabilidad. Si le preguntan qué entregó la semana pasada, no lo sabe. |
| **Héctor** *(como Admin SIGA)* | Administrador de la plataforma | Construye, opera y monitorea SIGA. Su 5% restante lo dedica a mantener la plataforma funcionando: gestiona tenants, revisa salud de microservicios, y se asegura de que Elizabeth, Yesenia, Luis y Antonia puedan trabajar sin fricción. | Necesita visibilidad del estado del sistema para detectar problemas antes de que los usuarios los reporten. |

---

## Epic 1: Identidad y Acceso

> *Pilar: Arquitectura de Microservicios Soberanos — `siga-auth`*

### US-1.1 — Onboarding SaaS de la dueña

**Como** Elizabeth (dueña de PYME),
**quiero** registrarme en SIGA con mi correo y datos de empresa,
**para** empezar a gestionar mis casinos y kioskos sin depender de nadie.

```gherkin
Scenario: Registro exitoso con verificación de correo
  Given Elizabeth ingresa su correo, nombre de empresa y contraseña en el customer-portal
  When envía el formulario de registro
  Then se crea un Customer con estado PENDING_VERIFICATION
  And se envía un correo de verificación con token de 24 horas

Scenario: Verificación y primer acceso
  Given Elizabeth hace click en el enlace de verificación dentro de 24 horas
  Then su cuenta pasa a ACTIVE, se genera su User con rol OWNER y se crea su tenant
```

**Ref SDD**: `openspec/specs/customer-auth/spec.md`

---

### US-1.2 — Permisos granulares para empleados polifuncionales

**Como** Elizabeth (dueña de PYME),
**quiero** asignar permisos específicos a cada empleado según sus responsabilidades reales,
**para** que Héctor gestione kioskos, Yesenia venda, y Luis solo vea lo que necesita para repartir.

```gherkin
Scenario: Asignar múltiples roles a un empleado polifuncional
  Given Héctor es User con rol EMPLOYEE en el tenant de Elizabeth
  When Elizabeth le asigna INVENTORY_READ, INVENTORY_WRITE y KIOSK_ADMIN
  Then Héctor puede ver stock, agregar productos y gestionar kioskos
  And no puede eliminar productos (requiere INVENTORY_DELETE)

Scenario: Permisos mínimos para repartidor
  Given Luis es User con rol EMPLOYEE
  When Elizabeth le asigna INVENTORY_READ y DELIVERY_VIEW
  Then Luis puede ver qué productos entregar y a dónde
  And no puede modificar stock ni precios

Scenario: El agente IA hereda los permisos del usuario
  Given Héctor tiene INVENTORY_WRITE
  When le pide al agente "agrega 50 unidades de servilletas al kiosko Norte"
  Then el agente ejecuta la acción porque Héctor tiene permiso
  And registra que fue en nombre de Héctor
```

---

## Epic 2: Gestión de Inventario — El Corazón de SIGA

> *Pilar: Gestión de Activos como Centro — `siga-inventory`*
> *Este es el punto crítico. Aquí es donde la PYME sangra tiempo y dinero.*

### US-2.1 — Visibilidad de stock real multi-punto

**Como** Elizabeth (dueña con casinos, kioskos y cocina central),
**quiero** ver el stock de todos mis puntos en una sola pantalla,
**para** no tener que preguntar por WhatsApp qué falta en cada lugar.

```gherkin
Scenario: Vista consolidada de stock
  Given Elizabeth tiene 3 kioskos, 2 casinos y 1 cocina central
  And el producto "Servilleta 100u" tiene: Kiosko Norte (20), Kiosko Sur (5), Bodega (200)
  When accede al dashboard de inventario
  Then ve "Servilleta 100u" con stock total: 225
  And puede expandir el desglose por punto

Scenario: Filtro por punto operativo
  Given Elizabeth está viendo el inventario consolidado
  When filtra por "Kiosko Norte"
  Then solo ve productos y cantidades de ese punto
```

---

### US-2.2 — Ingreso rápido de productos sin fricción

**Como** Héctor (admin de kioskos, siempre contra el reloj),
**quiero** agregar productos al sistema de forma rápida y sin pasos innecesarios,
**para** no perpetuar el desfase entre lo que hay en el sistema y lo que hay en la realidad.

```gherkin
Scenario: Agregar producto con código automático
  Given Héctor necesita ingresar un producto nuevo comprado de emergencia
  When crea el producto con nombre "Galleta Surtida 250g" y el campo SKU está vacío
  Then el sistema genera un SKU automático basado en categoría + secuencial
  And el producto queda disponible inmediatamente para venta en los kioskos

Scenario: Detección de producto duplicado
  Given ya existe "Galleta Surtida 250g" con SKU "GAL-001"
  When Héctor intenta crear "Galletas Surtidas 250g" (variación de nombre)
  Then el sistema advierte: "Producto similar encontrado: Galleta Surtida 250g (GAL-001). ¿Es el mismo?"
  And ofrece opciones: "Usar existente" / "Crear como nuevo"
```

---

### US-2.3 — Búsqueda que funcione para todos

**Como** Yesenia (cajera, no nativa digital),
**quiero** buscar productos sin que el sistema me castigue por mayúsculas o tildes,
**para** encontrar lo que necesito sin frustrarme.

```gherkin
Scenario: Búsqueda case-insensitive y sin tildes
  Given existe el producto "Café Instantáneo 200g"
  When Yesenia escribe "cafe instantaneo" en la barra de búsqueda
  Then el sistema encuentra "Café Instantáneo 200g"
  And los resultados aparecen en menos de 500ms

Scenario: Búsqueda parcial por nombre
  Given existen "Galleta Surtida", "Galleta Salada" y "Galleta de Agua"
  When Yesenia escribe "galle"
  Then ve las 3 galletas en los resultados
```

---

### US-2.4 — Reconciliación de stock (cerrar el desfase)

**Como** Héctor (encargado de inventario),
**quiero** registrar el stock real que veo físicamente y que el sistema detecte las diferencias,
**para** corregir el desfase entre lo que dice el sistema y lo que realmente hay.

```gherkin
Scenario: Conteo físico con detección de desfase
  Given el sistema dice que "Jugo Caja 1L" tiene 45 unidades en Kiosko Norte
  When Héctor registra un conteo físico de 12 unidades
  Then el sistema marca un desfase de -33 unidades
  And solicita un motivo: "Merma" / "Robo" / "Caducado" / "Error de ingreso" / "Otro"
  And ajusta el stock al valor real (12)
  And registra el evento en el log de auditoría

Scenario: Detección de productos caducados vía conteo
  Given Héctor marca 8 unidades de "Yogurt Natural" como "Caducado" durante el conteo
  Then el stock se reduce en 8 unidades
  And se genera una alerta para Elizabeth: "8 unidades de Yogurt Natural caducadas en Kiosko Norte"
```

---

### US-2.5 — Gestión de bodega con trazabilidad

**Como** Antonia (encargada de bodega),
**quiero** registrar qué productos salen de bodega y a dónde van,
**para** que cuando me pregunten qué entregué, pueda responder con certeza.

```gherkin
Scenario: Registro de salida de bodega a punto operativo
  Given Antonia tiene 200 unidades de "Servilleta 100u" en bodega
  When registra una salida de 50 unidades hacia "Casino Colegio"
  Then el stock de bodega baja a 150
  And el stock de Casino Colegio sube en 50
  And queda un registro: fecha, producto, cantidad, origen, destino, responsable (Antonia)

Scenario: Consulta de historial de entregas
    Given Luis pregunta "Anto, ¿qué entregaste la semana pasada al Casino Colegio?"
  When Antonia consulta el historial filtrado por destino y fecha
  Then ve la lista completa de entregas con fecha, producto, cantidad
```

---

## Epic 3: Ventas y POS

> *Pilar: Módulo Ventas con Propósito — `siga-sales` + SAGA vía Kafka*

### US-3.1 — Venta con descuento de stock automático

**Como** Yesenia (cajera de kiosko),
**quiero** registrar una venta y que el stock se descuente solo,
**para** no tener que avisar a Héctor cada vez que vendo algo.

```gherkin
Scenario: Venta exitosa con stock suficiente
  Given Yesenia está en el POS del Kiosko Norte
  And "Jugo Caja 1L" tiene 12 unidades en stock
  When registra una venta de 3 unidades
  Then siga-sales crea la transacción, publica "sale.completed" vía Kafka
  And siga-inventory descuenta 3 unidades → stock queda en 9

Scenario: Venta rechazada por stock insuficiente
  Given "Galleta Surtida" tiene 1 unidad en Kiosko Norte
  When Yesenia intenta vender 3 unidades
  Then el sistema rechaza: "Stock insuficiente. Disponible: 1"
  And el stock no se modifica
```

**Ref SDD**: `openspec/changes/saga-sales-inventory/spec.md`

---

### US-3.2 — Solicitud de producto no registrado

**Como** Yesenia (cajera de kiosko),
**quiero** solicitar que se agregue un producto que Elizabeth compró y no informó,
**para** poder venderlo sin esperar a que Héctor esté disponible.

```gherkin
Scenario: Solicitud de alta de producto nuevo
  Given Elizabeth compró "Chips Nuevos Sabor BBQ" y los dejó en el kiosko sin avisar
  And el producto no existe en el sistema
  When Yesenia crea una solicitud de alta con: nombre, precio sugerido y foto
  Then la solicitud queda pendiente de aprobación por un usuario con INVENTORY_WRITE
  And Héctor recibe una notificación: "Yesenia solicita agregar: Chips Nuevos Sabor BBQ"

Scenario: Aprobación rápida de solicitud
  Given Héctor recibe la solicitud de Yesenia
  When aprueba la solicitud revisando nombre y precio
  Then el sistema crea el producto con SKU automático
  And Yesenia ya puede registrar ventas de ese producto
```

---

## Epic 4: El Agente IA — La Magia de SIGA

> *Pilar: Agentes de IA Operativos — `siga-agent` + A2UI*
> *"No gestiones tu inventario, gestiona tu tiempo."*
>
> *Elizabeth gestiona todo con libreta y WhatsApp. El agente IA es su evolución natural: la misma conversación, pero con un sistema inteligente detrás.*

### US-4.1 — Ingreso de stock conversacional durante reparto

**Como** Héctor (repartidor que no tiene tiempo de abrir formularios),
**quiero** decirle al agente qué estoy entregando mientras reparto,
**para** que el stock se actualice sin tener que sentarme en un escritorio.

```gherkin
Scenario: Registro conversacional de entrega
  Given Héctor tiene Plan Avanzado y permiso INVENTORY_WRITE
  When escribe al agente: "Acabo de dejar 30 jugos caja y 20 galletas surtidas en el kiosko Norte"
  Then el agente interpreta: Jugo Caja 1L (+30, Kiosko Norte), Galleta Surtida (+20, Kiosko Norte)
  And pide confirmación mostrando un resumen A2UI
  When Héctor confirma
  Then el stock se actualiza en ambos productos para Kiosko Norte
```

---

### US-4.2 — Lista de compras inteligente

**Como** Elizabeth (dueña que arma listas los domingos por la tarde),
**quiero** pedirle al agente que me arme la lista de compras de la semana,
**para** no hacerlo a mano mirando la libreta.

```gherkin
Scenario: Generación de lista de compras basada en stock
  Given Elizabeth tiene Plan Avanzado
  When escribe: "¿Qué necesito comprar esta semana para los kioskos?"
  Then el agente analiza stock actual vs consumo promedio semanal por producto
  And genera una lista A2UI con: producto, cantidad sugerida, punto que más consume
  And Elizabeth puede editar cantidades y confirmar la lista
```

---

### US-4.3 — Detección de anomalías

**Como** Elizabeth (dueña),
**quiero** que el agente me avise cuando detecte algo raro en el inventario,
**para** enterarme antes de que sea un problema.

```gherkin
Scenario: Alerta proactiva de posibles caducados
  Given el agente analiza el inventario periódicamente
  And detecta que "Yogurt Natural" lleva 15 días sin movimiento en Kiosko Sur con 40 unidades
  Then envía una alerta a Elizabeth: "Yogurt Natural en Kiosko Sur: 40 unidades sin movimiento en 15 días. Posible riesgo de caducidad."

Scenario: Detección de desfase anómalo
  Given el sistema muestra 50 unidades de "Bebida 500ml" en Kiosko Norte
  And las ventas registradas en la última semana son solo 5
  And el último conteo físico (hace 3 días) registró 20 unidades
  Then el agente alerta: "Desfase detectado en Bebida 500ml — el sistema dice 50 pero el último conteo registró 20"
```

---

### US-4.4 — Modo dual: clásico ↔ agentivo

**Como** Elizabeth (dueña, no nativa digital),
**quiero** poder hablar con el agente cuando me resulte más fácil que navegar menús,
**para** usar SIGA como si fuera un WhatsApp inteligente.

```gherkin
Scenario: Transición de modo clásico a agentivo
  Given Elizabeth está navegando el inventario en modo clásico
  When hace click en "Ahorremos tiempo: SIGA"
  Then el agente se expande con contexto de la página actual
  And sugiere: "Estás en inventario. ¿Querés que analice qué falta en tus kioskos?"

Scenario: Respeto del plan del usuario
  Given Elizabeth tiene Plan Base (solo análisis)
  When escribe: "Agrega 50 servilletas al kiosko Norte"
  Then el agente responde: "Esa función requiere el Plan Avanzado. ¿Querés ver las opciones?"
  And no ejecuta la acción
```

**Ref SDD**: `openspec/specs/agent-service/spec.md`, `openspec/specs/ui-a2ui/spec.md`

---

## Epic 5: Facturación SaaS

> *Pilar: Modelo de Negocio — `siga-billing`*

### US-5.1 — Suscripción y planes

**Como** Elizabeth (dueña de PYME),
**quiero** elegir un plan según mis necesidades,
**para** pagar solo por lo que necesito y poder escalar cuando crezca.

```gherkin
Scenario: Selección de plan durante onboarding
  Given Elizabeth acaba de verificar su cuenta
  When accede al customer-portal por primera vez
  Then ve Plan Base ($X/mes: análisis IA) y Plan Avanzado ($Y/mes: IA operativa con CRUD)
  And puede seleccionar y proceder al pago

Scenario: Upgrade de plan activa IA operativa
  Given Elizabeth tiene Plan Base
  When solicita upgrade a Plan Avanzado
  Then las capacidades de IA Operativa se activan inmediatamente
  And el agente puede ejecutar acciones CRUD sobre el inventario
```

**Ref SDD**: `openspec/changes/billing-uuid-hexagonal/`

---

## Epic 6: Administración de Plataforma

### US-6.1 — Visibilidad de tenants y salud

**Como** Héctor (admin SIGA),
**quiero** ver el estado de todos los clientes y la salud de los microservicios,
**para** detectar problemas antes de que los usuarios los reporten.

```gherkin
Scenario: Dashboard de tenants
    Given Héctor accede al admin-portal
  Then ve lista de clientes con: empresa, plan, estado, fecha de registro
  And puede filtrar por plan y estado

Scenario: Monitor de salud
  Given Valentina accede al panel de salud
  Then ve el estado de cada microservicio (UP/DOWN) vía Eureka
  And si uno está DOWN, se muestra alerta visual
```

---

## Trazabilidad: User Story → BDD → SDD → Test

```
US-3.1 (Venta con descuento)
  → Scenario: "Venta exitosa con stock suficiente" (BDD)
  → openspec/changes/saga-sales-inventory/spec.md (SDD)
  → SaleCompletedBehaviorSpec.kt (Kotest BehaviorSpec)
```

> Cada historia es trazable hasta un test automatizado. El pipeline Harness ejecuta TDD → BDD → SDD en secuencia.

---

## Convención de IDs

| Prefijo | Epic |
|---------|------|
| US-1.x | Identidad y Acceso |
| US-2.x | Gestión de Inventario |
| US-3.x | Ventas y POS |
| US-4.x | Agente IA (A2UI) |
| US-5.x | Facturación SaaS |
| US-6.x | Administración |

---

*Historias deducidas del Manifiesto Core, la Visión SIGA, y la experiencia directa de trabajo en PYMEs chilenas del rubro alimentación.*
*Los actores son personas reales. Los pain points fueron vividos, no supuestos.*

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
