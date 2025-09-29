# SIGA: Reglas, Historias de Usuario y Requisitos

Este documento define el alcance funcional y los criterios de calidad del sistema SIGA. Es el plano de construcción que guiará el desarrollo técnico del proyecto.

---

### 1. Reglas del Negocio

Son las "leyes" inquebrantables del sistema. El software debe respetar estas reglas en todo momento.

- **RN-01:** El stock de un producto nunca puede ser negativo.
- **RN-02:** Cada producto debe pertenecer a una categoría y a un local.
- **RN-03:** Cada movimiento de inventario (compra, venta, merma) debe estar asociado a un producto, una cantidad, un usuario y una fecha/hora.
- **RN-04:** Cada venta registrada en el Punto de Venta (POS) debe descontar automáticamente el stock del producto correspondiente en tiempo real.
- **RN-05:** Todo producto tiene un costo y un precio de venta para calcular la rentabilidad.
- **RN-06:** El sistema debe registrar un historial de todos los movimientos de stock (trazabilidad).
- **RN-07:** Una alerta de bajo stock se genera cuando la cantidad de un producto cae por debajo de un umbral predefinido o predicho.

---

### 2. Historias de Usuario

Definen las funcionalidades desde la perspectiva de quienes las usan, siguiendo el formato: `Como [rol], quiero [acción], para [beneficio]`.

#### Epic: Gestión de Catálogo e Inventario
- **HU-01:** **Como** Administrador, **quiero** crear un nuevo producto con su nombre, SKU, categoría, costo y precio de venta, **para** tenerlo disponible en mi catálogo.
- **HU-02:** **Como** Administrador, **quiero** registrar una entrada de stock (compra) para uno o más productos, **para** reflejar la mercadería recibida.
- **HU-03:** **Como** Administrador, **quiero** registrar mermas o pérdidas, **para** mantener la precisión del inventario y entender los costos.
- **HU-04:** **Como** Administrador, **quiero** poder realizar un ajuste manual de stock, **para** corregir discrepancias después de un conteo físico.

#### Epic: Punto de Venta (POS)
- **HU-05:** **Como** Vendedor, **quiero** escanear el código de barras de un producto o buscarlo por nombre, **para** agregarlo rápidamente a una venta.
- **HU-06:** **Como** Vendedor, **quiero** finalizar una venta registrando el método de pago (efectivo/tarjeta), **para** que el sistema descuente el stock y registre el ingreso.

#### Epic: Asistente Conversacional (Chatbot)
- **HU-07:** **Como** Administrador en ruta, **quiero** dictarle al chatbot "agrega 15 bebidas X compradas a [Proveedor Y]", **para** actualizar el inventario y registrar la compra sin usar menús.
- **HU-08:** **Como** Administrador en el mayorista, **quiero** preguntar "¿qué productos de [Categoría Z] necesito reponer para el Kiosko B?", **para** obtener una lista de compras filtrada y optimizada.
- **HU-09:** **Como** Vendedor en caja, **quiero** preguntar al chatbot "¿cuántas galletas Y quedan?", **para** saber si hay stock disponible sin moverme del mostrador.

#### Epic: Análisis y Reportes
- **HU-10:** **Como** Administrador, **quiero** recibir una alerta proactiva cuando el sistema prediga que un producto se agotará pronto, **para** poder reabastecerlo a tiempo y no perder ventas.
- **HU-11:** **Como** Administrador, **quiero** pedir al chatbot "dame el reporte de los productos más vendidos de esta semana", **para** tomar decisiones de compra basadas en datos.
- **HU-12:** **Como** Administrador, **quiero** que el reporte incluya una explicación en texto simple de los gráficos, **para** entender la información rápidamente sin ser un experto en datos.

#### Epic: Administración de Cuentas y Locales
- **HU-13:** **Como** Administrador, **quiero** invitar a Vendedores por email y asignarles un local específico, **para** delegar la operación de venta de forma segura.

---

### 3. Requisitos

#### Requisitos Funcionales (RF)
*Lo que el sistema **HACE***

- **RF-01:** El sistema permitirá el CRUD de productos, categorías y locales.
- **RF-02:** El sistema registrará transacciones de **compra** (entradas), **venta** (salidas desde el POS) y **ajuste** (mermas, correcciones).
- **RF-03:** El sistema proveerá una interfaz de Punto de Venta (POS) para buscar productos, gestionar un carrito de compras y registrar ventas.
- **RF-04:** El sistema proveerá una interfaz de chatbot que procesará lenguaje natural para ejecutar consultas y comandos de inventario.
- **RF-05:** El sistema deberá generar alertas automáticas de bajo stock basadas en un modelo predictivo.
- **RF-06:** El sistema generará reportes visuales y textuales sobre ventas, mermas y rotación de productos.
- **RF-07:** El sistema manejará dos roles de usuario: **Administrador** (acceso total) y **Vendedor** (acceso solo al POS y consultas de stock de su local asignado).

#### Requisitos No Funcionales (RNF)
*Cómo el sistema **ES***

- **RNF-01 (Rendimiento):** Las consultas del chatbot y las transacciones del POS deben completarse en menos de 2 segundos.
- **RNF-02 (Usabilidad):** La interfaz debe ser lo suficientemente intuitiva para que un nuevo usuario pueda realizar su primera venta en menos de 3 minutos sin entrenamiento.
- **RNF-03 (Disponibilidad):** El sistema deberá tener una disponibilidad del 99.5%.
- **RNF-04 (Seguridad):** El acceso a los datos estará restringido por rol. Un Vendedor de un local no podrá ver los datos de otro local.
- **RNF-05 (Compatibilidad):** La aplicación móvil deberá funcionar en las dos últimas versiones de iOS y Android. La aplicación web deberá ser compatible con las últimas versiones de Chrome, Firefox y Safari.