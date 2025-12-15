# Especificación Técnica del Frontend SIGA

**Versión:** 1.0  
**Fecha:** Diciembre 2024  
**Propósito:** Documentación técnica completa para compatibilizar con backend y migración a base de datos real

---

## 1. Arquitectura Actual

### 1.1 Stack Tecnológico

| Componente | Tecnología | Versión | Propósito |
|------------|-----------|---------|-----------|
| **Framework** | SvelteKit | 5.0+ | Framework principal con SSR/SSG |
| **Build Tool** | Vite | 5.0+ | Bundler y dev server |
| **Lenguaje** | JavaScript/TypeScript | ES2020+ | Desarrollo |
| **UI Framework** | Bulma CSS | 1.0+ | Estilos y componentes |
| **Gráficos** | Chart.js | 4.4+ | Visualización de datos |
| **IA** | Google Gemini 2.5 Flash | API | Procesamiento de lenguaje natural |
| **Testing** | Karma + Jasmine | 6.4.2 + 5.1.0 | Tests unitarios e integración |
| **Despliegue** | Vercel | Serverless | Hosting y CI/CD |
| **Adapter** | @sveltejs/adapter-vercel | 5.10.3 | Adaptador para Vercel |

### 1.2 Estructura del Proyecto

```
SIGA_APPWEB/
├── src/
│   ├── lib/
│   │   ├── components/          # Componentes Svelte reutilizables
│   │   │   ├── AsistenteContextual.svelte
│   │   │   ├── Navbar.svelte
│   │   │   ├── GraficoBarras.svelte
│   │   │   ├── GraficoLineas.svelte
│   │   │   ├── GraficoLineasMultiple.svelte
│   │   │   └── GraficoTorta.svelte
│   │   ├── estado-compartido.js # Estado global en memoria
│   │   ├── datosSimulados.js    # Datos de ejemplo (writable store)
│   │   ├── openapi-spec.js      # Especificación OpenAPI
│   │   └── openapi.js            # Utilidades OpenAPI
│   │
│   ├── routes/
│   │   ├── +layout.svelte       # Layout principal
│   │   ├── +page.svelte         # Página principal (Inventario)
│   │   ├── acerca/+page.svelte
│   │   ├── analisis/+page.svelte
│   │   ├── asistente/+page.svelte
│   │   ├── api-docs/+page.svelte # Swagger UI
│   │   └── api/                  # Endpoints API (SvelteKit routes)
│   │       ├── chat/+server.js
│   │       ├── productos/
│   │       │   ├── +server.js    # GET - Listar productos
│   │       │   ├── crear/+server.js
│   │       │   ├── editar/+server.js
│   │       │   └── eliminar/+server.js
│   │       ├── inventario/
│   │       │   └── actualizar/+server.js
│   │       └── openapi/+server.js
│   │
│   ├── tests/                    # Tests unitarios e integración
│   ├── app.html                  # Template HTML base
│   └── app.css                   # Estilos globales
│
├── static/                       # Archivos estáticos
├── package.json
├── svelte.config.js
├── vite.config.js
└── .env.local                    # Variables de entorno (no versionado)
```

### 1.3 Patrón de Arquitectura

**Actual:** Monolito con estado en memoria
- Estado compartido en `estado-compartido.js` (objeto JavaScript en memoria)
- Endpoints API de SvelteKit que modifican el estado directamente
- Sin persistencia de datos
- Sin autenticación/autorización
- Sin separación frontend/backend

**Objetivo:** Arquitectura SaaS con backend separado
- Frontend como SPA consumiendo API REST externa
- Backend con base de datos PostgreSQL
- Autenticación JWT
- Multi-tenancy (suscripciones)

---

## 2. Modelos de Datos Actuales

### 2.1 Estructura de Datos en Memoria

#### 2.1.1 Local
```javascript
{
  id: number,           // 1, 2, 3...
  nombre: string        // "ITR", "Presidente Ibañez", "Serena"
}
```

**Ejemplo actual:**
```javascript
{ id: 1, nombre: 'ITR' }
{ id: 2, nombre: 'Presidente Ibañez' }
{ id: 3, nombre: 'Serena' }
```

#### 2.1.2 Producto
```javascript
{
  id: number,                    // 101, 102, 103...
  nombre: string,                // "Leche con Chocolate 1L"
  sku: string,                   // "LCH-001" (formato: LETRAS-NÚMEROS)
  categoria: string,             // "Lácteos", "Bebidas", "Snacks"...
  activo: boolean,               // true/false (soft delete)
  stock: {                       // Objeto con stock por local
    [localId]: number            // { 1: 18, 2: 24, 3: 96 }
  }
}
```

**Ejemplo actual:**
```javascript
{
  id: 101,
  nombre: 'Leche con Chocolate 1L',
  sku: 'LCH-001',
  categoria: 'Lácteos',
  activo: true,
  stock: { 1: 18, 2: 24, 3: 96 }
}
```

#### 2.1.3 Venta Semanal
```javascript
{
  localId: number,      // Referencia a local.id
  productoId: number,   // Referencia a producto.id
  cantidad: number      // Unidades vendidas
}
```

**Ejemplo actual:**
```javascript
{ localId: 1, productoId: 107, cantidad: 58 }
```

### 2.2 Mapeo a Base de Datos Relacional

#### 2.2.1 Tabla: `locales`
```sql
CREATE TABLE locales (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  direccion TEXT,
  telefono VARCHAR(20),
  activo BOOLEAN DEFAULT true,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Campos adicionales recomendados:**
- `usuario_id` (FK) - Para multi-tenancy
- `suscripcion_id` (FK) - Para SaaS
- `codigo` VARCHAR(10) - Código único del local
- `latitud` DECIMAL(10,8)
- `longitud` DECIMAL(11,8)

#### 2.2.2 Tabla: `categorias`
```sql
CREATE TABLE categorias (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  activo BOOLEAN DEFAULT true,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Nota:** Actualmente `categoria` es un string en el producto. Se recomienda normalizar a tabla separada.

#### 2.2.3 Tabla: `productos`
```sql
CREATE TABLE productos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(200) NOT NULL,
  sku VARCHAR(50) NOT NULL UNIQUE,
  categoria_id INTEGER REFERENCES categorias(id),
  descripcion TEXT,
  precio_unitario DECIMAL(10,2),
  codigo_barras VARCHAR(50),
  activo BOOLEAN DEFAULT true,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  usuario_id INTEGER,  -- Para multi-tenancy
  suscripcion_id INTEGER  -- Para SaaS
);
```

**Campos adicionales recomendados:**
- `imagen_url` VARCHAR(500)
- `unidad_medida` VARCHAR(20) - "unidad", "kg", "litro"
- `stock_minimo` INTEGER
- `stock_maximo` INTEGER

#### 2.2.4 Tabla: `stock` (Normalizada)
```sql
CREATE TABLE stock (
  id SERIAL PRIMARY KEY,
  producto_id INTEGER NOT NULL REFERENCES productos(id),
  local_id INTEGER NOT NULL REFERENCES locales(id),
  cantidad INTEGER NOT NULL DEFAULT 0,
  cantidad_minima INTEGER DEFAULT 0,
  cantidad_maxima INTEGER,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(producto_id, local_id)
);
```

**Nota:** Actualmente el stock está embebido en el objeto producto como `stock: { 1: 18, 2: 24 }`. Se debe normalizar a tabla separada.

#### 2.2.5 Tabla: `ventas`
```sql
CREATE TABLE ventas (
  id SERIAL PRIMARY KEY,
  local_id INTEGER NOT NULL REFERENCES locales(id),
  usuario_id INTEGER NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(20) DEFAULT 'COMPLETADA',
  observaciones TEXT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2.2.6 Tabla: `venta_detalles`
```sql
CREATE TABLE venta_detalles (
  id SERIAL PRIMARY KEY,
  venta_id INTEGER NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
  producto_id INTEGER NOT NULL REFERENCES productos(id),
  cantidad INTEGER NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL
);
```

**Nota:** Actualmente `ventasSemana` es un array simple. Se debe normalizar a tablas `ventas` y `venta_detalles`.

---

## 3. Endpoints API Actuales

### 3.1 Estructura de Endpoints

Todos los endpoints están en `src/routes/api/` siguiendo el file-based routing de SvelteKit.

### 3.2 Endpoints Implementados

#### 3.2.1 GET `/api/productos`
**Archivo:** `src/routes/api/productos/+server.js`

**Descripción:** Lista todos los productos (actualmente retorna datos de ejemplo)

**Request:**
```http
GET /api/productos
```

**Response actual:**
```json
[
  {
    "id": "prod-001",
    "nombre": "Laptop desde API",
    "categoria": "Electrónica",
    "sku": "LAP-API-001",
    "estado": "activo"
  }
]
```

**Response esperado (con backend):**
```json
{
  "success": true,
  "productos": [
    {
      "id": 1,
      "nombre": "Leche con Chocolate 1L",
      "sku": "LCH-001",
      "categoria": "Lácteos",
      "precio_unitario": "1500.00",
      "activo": true,
      "stock": {
        "1": 18,
        "2": 24,
        "3": 96
      }
    }
  ],
  "total": 7
}
```

**Headers requeridos (futuro):**
```
Authorization: Bearer {jwt_token}
```

---

#### 3.2.2 POST `/api/productos/crear`
**Archivo:** `src/routes/api/productos/crear/+server.js`

**Descripción:** Crea un nuevo producto

**Request:**
```json
{
  "nombre": "Nuevo Producto",
  "categoria": "Bebidas",
  "sku": "NUE-001"  // Opcional, se genera si no se proporciona
}
```

**Response:**
```json
{
  "success": true,
  "producto": {
    "id": 108,
    "nombre": "Nuevo Producto",
    "sku": "NUE-001",
    "categoria": "Bebidas",
    "activo": true,
    "stock": { "1": 0, "2": 0, "3": 0 }
  },
  "datos": { /* estado completo actualizado */ },
  "mensaje": "Producto \"Nuevo Producto\" creado exitosamente en todos los locales"
}
```

**Validaciones actuales:**
- `nombre` y `categoria` son requeridos
- `sku` se genera automáticamente si no se proporciona: `SKU-{timestamp}`
- Se crea stock inicial en 0 para todos los locales

**Cambios necesarios para backend:**
- Validar `sku` único en base de datos
- Validar `categoria` existe en tabla `categorias`
- Crear registros en tabla `stock` para cada local
- Agregar `usuario_id` y `suscripcion_id` desde el token JWT

---

#### 3.2.3 POST `/api/productos/editar`
**Archivo:** `src/routes/api/productos/editar/+server.js`

**Descripción:** Edita un producto existente

**Request:**
```json
{
  "id": 101,
  "nombre": "Leche con Chocolate Premium 1L",  // Opcional
  "sku": "LCH-001-PREMIUM",                       // Opcional
  "categoria": "Lácteos Premium"                   // Opcional
}
```

**Response:**
```json
{
  "success": true,
  "producto": {
    "id": 101,
    "nombre": "Leche con Chocolate Premium 1L",
    "sku": "LCH-001-PREMIUM",
    "categoria": "Lácteos Premium",
    "activo": true,
    "stock": { "1": 18, "2": 24, "3": 96 }
  },
  "valoresAnteriores": {
    "nombre": "Leche con Chocolate 1L",
    "sku": "LCH-001",
    "categoria": "Lácteos"
  },
  "datos": { /* estado completo actualizado */ },
  "mensaje": "Producto actualizado exitosamente"
}
```

**Validaciones actuales:**
- `id` es requerido
- Solo actualiza campos proporcionados (PATCH parcial)
- Convierte `sku` a mayúsculas automáticamente

**Cambios necesarios para backend:**
- Validar permisos (solo ADMINISTRADOR puede editar)
- Validar `sku` único si se modifica
- Actualizar `fecha_actualizacion` automáticamente
- Validar `categoria_id` existe si se modifica

---

#### 3.2.4 POST `/api/productos/eliminar`
**Archivo:** `src/routes/api/productos/eliminar/+server.js`

**Descripción:** Desactiva un producto (soft delete)

**Request:**
```json
{
  "id": 101
}
```

**Response:**
```json
{
  "success": true,
  "mensaje": "Producto \"Leche con Chocolate 1L\" ha sido desactivado",
  "producto": {
    "id": 101,
    "nombre": "Leche con Chocolate 1L",
    "activo": false
  },
  "datos": { /* estado completo actualizado */ }
}
```

**Comportamiento actual:**
- Soft delete: marca `activo: false`
- No elimina físicamente (mantiene historial)

**Cambios necesarios para backend:**
- Validar permisos (solo ADMINISTRADOR)
- Verificar que no haya ventas pendientes con stock de este producto
- Actualizar `fecha_actualizacion`

---

#### 3.2.5 POST `/api/inventario/actualizar`
**Archivo:** `src/routes/api/inventario/actualizar/+server.js`

**Descripción:** Actualiza el stock de un producto en un local específico

**Request:**
```json
{
  "producto": "Leche con Chocolate 1L",  // Nombre o SKU
  "local": "ITR",                        // Nombre o ID
  "cantidad": 10,
  "accion": "agregar"                    // "agregar" | "reducir"
}
```

**Response:**
```json
{
  "success": true,
  "producto": "Leche con Chocolate 1L",
  "local": "ITR",
  "stockAnterior": 18,
  "stockNuevo": 28,
  "datos": { /* estado completo actualizado */ },
  "mensaje": "Stock de \"Leche con Chocolate 1L\" en ITR: 18 → 28"
}
```

**Validaciones actuales:**
- `producto`, `local`, `cantidad`, `accion` son requeridos
- Busca producto por nombre (case-insensitive) o SKU
- Busca local por nombre (case-insensitive) o ID
- `reducir` no permite valores negativos (Math.max(0, stock - cantidad))

**Cambios necesarios para backend:**
- Validar permisos (OPERADOR puede actualizar stock de sus locales)
- Validar que el local pertenece a la suscripción del usuario
- Crear registro de auditoría (historial de movimientos de stock)
- Validar stock mínimo/máximo
- Implementar transacciones para evitar condiciones de carrera

---

#### 3.2.6 POST `/api/chat`
**Archivo:** `src/routes/api/chat/+server.js`

**Descripción:** Procesa mensajes del usuario con IA (Gemini 2.5 Flash)

**Request:**
```json
{
  "mensaje": "Agrega 15 rollos de canela a ITR"
}
```

**Response:**
```json
{
  "respuesta": "Listo, agregué 15 rollos de canela a ITR"
}
```

**Flujo actual:**
1. Recibe mensaje del usuario
2. Construye prompt con contexto RAG (datos completos del inventario)
3. Envía a Gemini 2.5 Flash API
4. Parsea respuesta buscando bloques `[CRUD_START]...[CRUD_END]`
5. Retorna respuesta limpia al usuario

**Variables de entorno requeridas:**
```bash
GEMINI_API_KEY=tu_clave_api_de_gemini
```

**Prompt actual incluye:**
- Lista de locales
- Lista de productos existentes
- Total de ventas semanal
- Todos los datos en JSON (para contexto completo)

**Formato CRUD que la IA puede generar:**
```json
[CRUD_START]
{
  "accion": "crear_producto",
  "nombre": "Canela",
  "categoria": "Panadería"
}
[CRUD_END]

[CRUD_START]
{
  "accion": "agregar_stock",
  "producto": "Canela",
  "local": "ITR",
  "cantidad": 15
}
[CRUD_END]
```

**Cambios necesarios para backend:**
- Agregar autenticación JWT
- Filtrar datos por `suscripcion_id` del usuario
- Limitar contexto RAG a datos del tenant
- Implementar rate limiting para evitar abuso de la API de Gemini
- Agregar logging de interacciones con IA

---

### 3.3 Endpoints Faltantes (para SaaS completo)

#### 3.3.1 Autenticación
```
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
```

#### 3.3.2 Locales
```
GET /api/saas/locales
GET /api/saas/locales/{id}
POST /api/saas/locales
PUT /api/saas/locales/{id}
DELETE /api/saas/locales/{id}
```

#### 3.3.3 Categorías
```
GET /api/saas/categorias
POST /api/saas/categorias
PUT /api/saas/categorias/{id}
DELETE /api/saas/categorias/{id}
```

#### 3.3.4 Ventas
```
GET /api/saas/ventas
GET /api/saas/ventas/{id}
POST /api/saas/ventas
```

#### 3.3.5 Stock (endpoints adicionales)
```
GET /api/saas/stock
GET /api/saas/stock/{producto_id}/{local_id}
GET /api/saas/stock/bajo-minimo
```

#### 3.3.6 Reportes
```
GET /api/saas/reportes/ventas
GET /api/saas/reportes/stock
GET /api/saas/reportes/productos-mas-vendidos
```

---

## 4. Estado Compartido y Gestión de Datos

### 4.1 Estado Actual en Memoria

**Archivo:** `src/lib/estado-compartido.js`

```javascript
export let datosGlobales = {
  locales: [...],
  productos: [...],
  ventasSemana: [...]
};
```

**Características:**
- Objeto JavaScript global en memoria
- Modificado directamente por los endpoints
- Se pierde al reiniciar el servidor
- No hay sincronización entre instancias (si hay múltiples servidores)
- No hay persistencia

### 4.2 Store de Svelte (Frontend)

**Archivo:** `src/lib/datosSimulados.js`

```javascript
import { writable } from 'svelte/store';

export const datosNegocio = writable({
  locales: [...],
  productos: [...],
  ventasSemana: [...]
});
```

**Uso:**
- Componentes Svelte se suscriben al store
- Actualizaciones reactivas automáticas
- Solo en el cliente (navegador)

### 4.3 Migración a Backend

**Estrategia recomendada:**

1. **Crear servicio de API cliente:**
```javascript
// src/lib/api-client.js
export const apiClient = {
  async getProductos() {
    const token = localStorage.getItem('accessToken');
    const response = await fetch(`${API_URL}/api/saas/productos`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    return response.json();
  },
  // ... más métodos
};
```

2. **Reemplazar estado en memoria por llamadas API:**
   - Eliminar `estado-compartido.js`
   - Los endpoints de SvelteKit se convierten en proxies al backend
   - O mejor: eliminar endpoints de SvelteKit y llamar directamente al backend desde el cliente

3. **Mantener store de Svelte para estado local:**
   - Cache de datos en el cliente
   - Estado de UI (modales, formularios, etc.)
   - Optimistic updates

---

## 5. Integración con IA (Gemini)

### 5.1 Configuración Actual

**API utilizada:** Google Gemini 2.5 Flash  
**Endpoint:** `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent`

**Autenticación:** API Key en query parameter
```
?key=${apiKey}
```

**Variables de entorno:**
```bash
GEMINI_API_KEY=tu_clave_api_de_gemini
```

### 5.2 Flujo de Procesamiento

1. Usuario envía mensaje → `POST /api/chat`
2. Se construye prompt con contexto RAG:
   - Locales disponibles
   - Productos existentes
   - Ventas semanales
   - Datos completos en JSON
3. Se envía a Gemini con instrucciones específicas
4. Gemini responde con texto natural + bloques CRUD opcionales
5. Se parsea la respuesta buscando `[CRUD_START]...[CRUD_END]`
6. Se ejecutan las operaciones CRUD detectadas
7. Se retorna respuesta limpia al usuario

### 5.3 Prompt Engineering

**Estructura del prompt:**
```
Eres SIGA, asistente inteligente de gestión de inventario.

CONTEXTO:
- Locales: [lista]
- Productos existentes: [lista]
- Total ventas semanal: [número]
- Todos los datos: [JSON completo]

REGLAS IMPORTANTES:
1. Si piden agregar stock a producto inexistente → crear producto primero
2. Responde en máximo 2 líneas, amigable
3. CRUD entre [CRUD_START]...[CRUD_END]

FORMATOS CRUD:
- Crear: {"accion": "crear_producto", ...}
- Editar: {"accion": "editar_producto", ...}
- Agregar stock: {"accion": "agregar_stock", ...}
```

### 5.4 Consideraciones para Backend

1. **Rate Limiting:**
   - Limitar llamadas a Gemini por usuario/suscripción
   - Implementar cache de respuestas similares

2. **Contexto Multi-tenant:**
   - Filtrar datos por `suscripcion_id`
   - No exponer datos de otros tenants

3. **Logging:**
   - Registrar todas las interacciones con IA
   - Guardar prompts y respuestas para análisis

4. **Costos:**
   - Monitorear uso de tokens
   - Implementar límites por plan de suscripción

5. **Seguridad:**
   - Validar que las operaciones CRUD generadas por IA sean seguras
   - Sanitizar inputs antes de enviar a Gemini
   - Validar permisos antes de ejecutar CRUD

---

## 6. Autenticación y Autorización

### 6.1 Estado Actual

**❌ No implementado:**
- Sin autenticación
- Sin autorización
- Sin usuarios
- Sin roles
- Sin JWT

### 6.2 Requerimientos para SaaS

#### 6.2.1 Autenticación JWT

**Flujo:**
1. Usuario envía credenciales → `POST /api/auth/login`
2. Backend valida y retorna:
   ```json
   {
     "success": true,
     "accessToken": "eyJhbGci...",
     "refreshToken": "eyJhbGci...",
     "userId": 1,
     "email": "usuario@example.com",
     "rol": "ADMINISTRADOR"
   }
   ```
3. Frontend guarda tokens en `localStorage`
4. Todas las peticiones incluyen header:
   ```
   Authorization: Bearer {accessToken}
   ```

#### 6.2.2 Roles

**Roles propuestos:**
- `ADMINISTRADOR`: Acceso completo (crear/editar/eliminar productos, ver todos los locales)
- `OPERADOR`: Solo puede actualizar stock y crear ventas en sus locales asignados

#### 6.2.3 Multi-tenancy

**Estructura:**
- Cada usuario pertenece a una `suscripcion`
- Cada suscripción tiene múltiples `locales`
- Los datos están aislados por `suscripcion_id`

**Tablas necesarias:**
```sql
usuarios (id, email, password_hash, rol, suscripcion_id)
suscripciones (id, nombre_empresa, plan, activa, fecha_inicio)
```

---

## 7. Plan de Migración

### 7.1 Fase 1: Preparación del Frontend

**Objetivo:** Hacer el frontend compatible con backend externo sin romper funcionalidad actual.

**Tareas:**
1. ✅ Crear servicio `api-client.js` para llamadas HTTP
2. ✅ Agregar manejo de autenticación JWT
3. ✅ Implementar refresh token automático
4. ✅ Agregar manejo de errores HTTP (401, 402, 403, 404, 500)
5. ✅ Crear stores de Svelte para estado de autenticación
6. ✅ Implementar interceptores para agregar tokens a requests

**Archivos a crear/modificar:**
- `src/lib/api-client.js` (nuevo)
- `src/lib/stores/auth.js` (nuevo)
- `src/routes/api/**` (modificar para usar api-client o eliminar)
- `src/routes/+layout.svelte` (agregar manejo de auth)

### 7.2 Fase 2: Configuración de Variables de Entorno

**Archivo:** `.env.local`

```bash
# Backend API
VITE_API_URL=http://localhost:8080
# O en producción:
# VITE_API_URL=https://siga-backend.railway.app

# Gemini (mantener)
GEMINI_API_KEY=tu_clave_api_de_gemini
```

**Nota:** Variables con prefijo `VITE_` son expuestas al cliente. Variables sin prefijo solo en servidor.

### 7.3 Fase 3: Migración Gradual de Endpoints

**Estrategia:** Mantener endpoints de SvelteKit como proxies al backend durante la transición.

**Ejemplo:**
```javascript
// src/routes/api/productos/+server.js
export async function GET() {
  const apiUrl = process.env.VITE_API_URL || 'http://localhost:8080';
  const token = /* obtener de cookie o header */;
  
  const response = await fetch(`${apiUrl}/api/saas/productos`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  
  return json(await response.json());
}
```

**Ventajas:**
- Permite migración gradual
- Mantiene compatibilidad con código existente
- Facilita testing

**Desventajas:**
- Agrega latencia (doble hop)
- Más complejidad

**Alternativa:** Llamar directamente al backend desde el cliente.

### 7.4 Fase 4: Normalización de Datos

**Cambios necesarios:**

1. **Stock embebido → Tabla separada:**
   ```javascript
   // Antes:
   producto.stock = { 1: 18, 2: 24, 3: 96 }
   
   // Después:
   // GET /api/saas/stock?producto_id=101
   [
     { producto_id: 101, local_id: 1, cantidad: 18 },
     { producto_id: 101, local_id: 2, cantidad: 24 },
     { producto_id: 101, local_id: 3, cantidad: 96 }
   ]
   ```

2. **Categoría string → Tabla categorias:**
   ```javascript
   // Antes:
   producto.categoria = "Lácteos"
   
   // Después:
   producto.categoria_id = 1
   producto.categoria = { id: 1, nombre: "Lácteos" }  // Populado por backend
   ```

3. **Ventas semanales → Tablas normalizadas:**
   ```javascript
   // Antes:
   ventasSemana = [
     { localId: 1, productoId: 107, cantidad: 58 }
   ]
   
   // Después:
   // GET /api/saas/ventas?fecha_desde=2024-01-01&fecha_hasta=2024-01-07
   ventas = [
     {
       id: 1,
       local_id: 1,
       fecha: "2024-01-01T10:30:00Z",
       total: 58000.00,
       detalles: [
         { producto_id: 107, cantidad: 58, precio_unitario: 1000.00 }
       ]
     }
   ]
   ```

### 7.5 Fase 5: Implementación de Autenticación

**Tareas:**
1. Crear página de login (`/login`)
2. Implementar guard de rutas (proteger rutas que requieren auth)
3. Agregar logout
4. Manejar expiración de tokens
5. Implementar refresh token automático

**Archivos a crear:**
- `src/routes/login/+page.svelte`
- `src/lib/stores/auth.js`
- `src/hooks.server.js` (para validar tokens en servidor)

### 7.6 Fase 6: Testing y Validación

**Tests a crear:**
1. Tests de integración con backend mock
2. Tests de autenticación
3. Tests de permisos por rol
4. Tests de multi-tenancy

---

## 8. Especificaciones para Compatibilidad con Backend

### 8.1 Formato de Requests

**Headers estándar:**
```http
Content-Type: application/json
Authorization: Bearer {accessToken}
```

**Formato de errores esperado:**
```json
{
  "success": false,
  "error": {
    "codigo": "PRODUCTO_NO_ENCONTRADO",
    "mensaje": "El producto con ID 101 no existe",
    "detalles": {}
  }
}
```

### 8.2 Formato de Responses

**Éxito:**
```json
{
  "success": true,
  "data": { /* datos */ },
  "mensaje": "Operación exitosa"  // Opcional
}
```

**Lista paginada:**
```json
{
  "success": true,
  "data": [ /* items */ ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 100,
    "totalPages": 5
  }
}
```

### 8.3 Códigos HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| 200 | OK | Operación exitosa |
| 201 | Created | Recurso creado |
| 400 | Bad Request | Datos inválidos |
| 401 | Unauthorized | No autenticado o token inválido |
| 402 | Payment Required | Suscripción requerida o inactiva |
| 403 | Forbidden | Sin permisos (rol incorrecto) |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error del servidor |

### 8.4 Validaciones Esperadas

**Productos:**
- `nombre`: Requerido, mínimo 3 caracteres, máximo 200
- `sku`: Requerido, único, formato `LETRAS-NÚMEROS`
- `categoria_id`: Requerido, debe existir en tabla `categorias`
- `precio_unitario`: Opcional, debe ser >= 0

**Stock:**
- `cantidad`: Requerido, entero >= 0
- `cantidad_minima`: Opcional, entero >= 0
- `cantidad_maxima`: Opcional, entero > cantidad_minima

**Ventas:**
- `local_id`: Requerido, debe existir y pertenecer a la suscripción
- `detalles`: Requerido, array no vacío
- Cada detalle debe tener `producto_id`, `cantidad` > 0, `precio_unitario` > 0
- Validar stock suficiente antes de crear venta

---

## 9. Consideraciones Técnicas Importantes

### 9.1 CORS

**Configuración necesaria en backend:**
```javascript
// Permitir origen del frontend
Access-Control-Allow-Origin: https://siga-appweb.vercel.app
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Allow-Credentials: true
```

### 9.2 Manejo de Errores

**Estrategia recomendada:**
1. Interceptor global para errores HTTP
2. Mostrar mensajes amigables al usuario
3. Logging de errores en servicio externo (Sentry, LogRocket)
4. Retry automático para errores 5xx

### 9.3 Performance

**Optimizaciones:**
1. Cache de datos en el cliente (stores de Svelte)
2. Lazy loading de rutas
3. Paginación en listas grandes
4. Debounce en búsquedas
5. Optimistic updates para mejor UX

### 9.4 Seguridad

**Recomendaciones:**
1. Nunca exponer API keys en el cliente (Gemini key debe estar solo en backend)
2. Validar y sanitizar todos los inputs
3. Implementar CSRF protection
4. Rate limiting en backend
5. HTTPS obligatorio en producción

### 9.5 Variables de Entorno

**Desarrollo:**
```bash
VITE_API_URL=http://localhost:8080
GEMINI_API_KEY=dev_key_here
```

**Producción:**
```bash
VITE_API_URL=https://siga-backend.railway.app
# GEMINI_API_KEY solo en backend, no en frontend
```

**Nota:** Variables `VITE_*` son públicas en el bundle. No poner secrets ahí.

---

## 10. Checklist de Migración

### 10.1 Preparación
- [ ] Crear servicio `api-client.js`
- [ ] Configurar variables de entorno
- [ ] Implementar stores de autenticación
- [ ] Crear componentes de UI para login

### 10.2 Backend
- [ ] Backend con base de datos PostgreSQL
- [ ] Endpoints REST implementados
- [ ] Autenticación JWT funcionando
- [ ] Multi-tenancy implementado
- [ ] Validaciones y permisos

### 10.3 Integración
- [ ] Reemplazar estado en memoria por llamadas API
- [ ] Implementar autenticación en frontend
- [ ] Migrar endpoints de SvelteKit a proxies o eliminarlos
- [ ] Actualizar componentes para usar nuevos formatos de datos

### 10.4 Testing
- [ ] Tests de integración con backend
- [ ] Tests de autenticación
- [ ] Tests de permisos
- [ ] Tests end-to-end

### 10.5 Despliegue
- [ ] Configurar CORS en backend
- [ ] Variables de entorno en producción
- [ ] Monitoreo y logging
- [ ] Documentación actualizada

---

## 11. Decisiones Técnicas Pendientes

### 11.1 ¿Mantener endpoints de SvelteKit o llamar directamente al backend?

**Opción A: Proxies en SvelteKit**
- ✅ Mantiene compatibilidad con código existente
- ✅ Permite agregar lógica adicional (cache, transformaciones)
- ❌ Agrega latencia
- ❌ Más complejidad

**Opción B: Llamadas directas desde cliente**
- ✅ Menos latencia
- ✅ Más simple
- ✅ Mejor separación de concerns
- ❌ Requiere refactorizar más código

**Recomendación:** Opción B (llamadas directas) para mejor arquitectura a largo plazo.

### 11.2 ¿Dónde procesar la IA: Frontend o Backend?

**Opción A: Frontend (actual)**
- ✅ Menos carga en backend
- ❌ Expone API key de Gemini
- ❌ No hay control de rate limiting
- ❌ No hay logging centralizado

**Opción B: Backend (recomendado)**
- ✅ API key segura
- ✅ Rate limiting centralizado
- ✅ Logging de interacciones
- ✅ Mejor control de costos
- ❌ Más carga en backend

**Recomendación:** Opción B (backend) para producción.

### 11.3 ¿Cómo manejar el stock: Embebido o separado?

**Opción A: Embebido (actual)**
```javascript
producto.stock = { 1: 18, 2: 24, 3: 96 }
```
- ✅ Más simple para consultas
- ❌ No normalizado
- ❌ Dificulta queries complejas

**Opción B: Separado (recomendado)**
```sql
SELECT * FROM stock WHERE producto_id = 101
```
- ✅ Normalizado
- ✅ Facilita queries complejas
- ✅ Mejor para auditoría
- ❌ Requiere JOINs

**Recomendación:** Opción B (tabla separada) para base de datos.

---

## 12. Referencias y Recursos

### 12.1 Documentación de APIs

- **SvelteKit API Routes:** https://kit.svelte.dev/docs/routing#server
- **Google Gemini API:** https://ai.google.dev/docs
- **OpenAPI Specification:** https://swagger.io/specification/

### 12.2 Estándares

- **REST API Design:** https://restfulapi.net/
- **JWT:** https://jwt.io/
- **CORS:** https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS

### 12.3 Herramientas Recomendadas

- **Postman/Insomnia:** Para testing de APIs
- **Prisma/TypeORM:** Para ORM con PostgreSQL
- **Sentry:** Para error tracking
- **Vercel Analytics:** Para métricas de frontend

---

## 13. Contacto y Soporte

**Proyecto:** SIGA - Sistema Inteligente de Gestión de Activos  
**Repositorio:** https://github.com/HecAguilaV/SIGA_APPWEB  
**Versión del documento:** 1.0  
**Última actualización:** Diciembre 2024

---

**Este documento debe ser actualizado conforme avance la migración y se tomen decisiones técnicas.**

