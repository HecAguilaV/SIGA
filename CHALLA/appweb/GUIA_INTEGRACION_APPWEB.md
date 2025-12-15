# Guía de Integración - App Web (siga-appweb)

**Para**: Equipo de desarrollo App Web  
**Fecha**: Diciembre 2024  
**Versión Backend**: 1.0.0  
**Estado Backend**: ✅ **LISTO Y FUNCIONANDO**

---

## 📋 Resumen Ejecutivo

Esta guía contiene **todas las instrucciones necesarias** para migrar la App Web de su arquitectura actual (endpoints propios de SvelteKit) a una arquitectura que consume el backend real de SIGA.

**Estado Actual**: ⚠️ Endpoints propios, datos en memoria  
**Estado Objetivo**: ✅ Consume backend real, datos en PostgreSQL

**Tiempo Estimado**: 3-5 días

---

## 🎯 Objetivos de la Migración

1. ✅ Eliminar endpoints propios de SvelteKit (`/api/productos`, `/api/inventario`, etc.)
2. ✅ Consumir backend real (`https://siga-backend-production.up.railway.app`)
3. ✅ Implementar autenticación JWT
4. ✅ Ajustar modelos de datos al esquema de base de datos
5. ✅ Eliminar datos hardcodeados/en memoria

---

## 🔴 Cambios Críticos Requeridos

### 1. Arquitectura: De Endpoints Propios a Backend Real

**Problema Actual**:
```
Frontend → SvelteKit API Routes → Estado en Memoria
```

**Solución**:
```
Frontend → Backend REST API → PostgreSQL
```

**Acción**: Eliminar todos los endpoints propios de SvelteKit y reemplazarlos por llamadas al backend.

---

### 2. Configuración de API Base URL

**Crear archivo de configuración** (`src/lib/config.js` o similar):

```javascript
// src/lib/config.js
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 
  'https://siga-backend-production.up.railway.app';

export const API_ENDPOINTS = {
  // Autenticación
  LOGIN: `${API_BASE_URL}/api/auth/login`,
  REGISTER: `${API_BASE_URL}/api/auth/register`,
  REFRESH: `${API_BASE_URL}/api/auth/refresh`,
  
  // Productos
  PRODUCTOS: `${API_BASE_URL}/api/saas/productos`,
  PRODUCTO: (id) => `${API_BASE_URL}/api/saas/productos/${id}`,
  
  // Locales (NUEVO)
  LOCALES: `${API_BASE_URL}/api/saas/locales`,
  LOCAL: (id) => `${API_BASE_URL}/api/saas/locales/${id}`,
  
  // Categorías (NUEVO)
  CATEGORIAS: `${API_BASE_URL}/api/saas/categorias`,
  CATEGORIA: (id) => `${API_BASE_URL}/api/saas/categorias/${id}`,
  
  // Stock
  STOCK: `${API_BASE_URL}/api/saas/stock`,
  STOCK_ESPECIFICO: (productoId, localId) => 
    `${API_BASE_URL}/api/saas/stock/${productoId}/${localId}`,
  
  // Ventas
  VENTAS: `${API_BASE_URL}/api/saas/ventas`,
  VENTA: (id) => `${API_BASE_URL}/api/saas/ventas/${id}`,
};
```

**Variable de entorno** (`.env` o `.env.local`):
```env
VITE_API_BASE_URL=https://siga-backend-production.up.railway.app
```

---

### 3. Cliente API con Autenticación JWT

**Crear cliente API** (`src/lib/api-client.js`):

```javascript
// src/lib/api-client.js
import { API_BASE_URL } from './config.js';

class ApiClient {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  // Obtener token del localStorage
  getToken() {
    return localStorage.getItem('accessToken');
  }

  // Guardar tokens
  setTokens(accessToken, refreshToken) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  // Limpiar tokens (logout)
  clearTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  // Headers con autenticación
  getHeaders(includeAuth = true) {
    const headers = {
      'Content-Type': 'application/json',
    };

    if (includeAuth) {
      const token = this.getToken();
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
    }

    return headers;
  }

  // Request genérico con manejo de errores
  async request(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${this.baseURL}${endpoint}`;
    
    const config = {
      ...options,
      headers: {
        ...this.getHeaders(options.requireAuth !== false),
        ...options.headers,
      },
    };

    try {
      const response = await fetch(url, config);
      
      // Si el token expiró, intentar refresh
      if (response.status === 401 && options.requireAuth !== false) {
        const refreshed = await this.refreshToken();
        if (refreshed) {
          // Reintentar request con nuevo token
          config.headers['Authorization'] = `Bearer ${this.getToken()}`;
          const retryResponse = await fetch(url, config);
          return this.handleResponse(retryResponse);
        } else {
          // Refresh falló, redirigir a login
          this.clearTokens();
          window.location.href = '/login';
          throw new Error('Sesión expirada');
        }
      }

      return await this.handleResponse(response);
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  }

  // Manejar respuesta
  async handleResponse(response) {
    const data = await response.json();
    
    if (!response.ok) {
      throw new Error(data.message || `HTTP ${response.status}`);
    }
    
    return data;
  }

  // Refresh token
  async refreshToken() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return false;

    try {
      const response = await fetch(`${this.baseURL}/api/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });

      if (response.ok) {
        const data = await response.json();
        this.setTokens(data.accessToken, data.refreshToken);
        return true;
      }
    } catch (error) {
      console.error('Refresh token failed:', error);
    }

    return false;
  }

  // Métodos HTTP
  async get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  }

  async post(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  }
}

// Exportar instancia singleton
export const apiClient = new ApiClient();
```

---

## 📊 Modelos de Datos Actualizados

### Producto

**❌ Modelo Actual (INCORRECTO)**:
```javascript
{
  id: number,
  nombre: string,
  sku: string,  // ⚠️ No existe en backend
  categoria: string,  // ⚠️ Debe ser ID
  activo: boolean,
  stock: {  // ⚠️ Embebido, debe ser separado
    [localId]: number
  }
}
```

**✅ Modelo Corregido (ALINEADO CON BACKEND)**:
```javascript
{
  id: number,
  nombre: string,
  descripcion: string | null,
  categoriaId: number | null,  // ✅ FK a categorias
  codigoBarras: string | null,  // ✅ Usar este campo para SKU
  precioUnitario: string | null,  // ✅ NUMERIC como string
  activo: boolean,
  fechaCreacion: string,  // ISO 8601
  fechaActualizacion: string  // ISO 8601
}
```

### Local (NUEVO)

**✅ Modelo**:
```javascript
{
  id: number,
  nombre: string,
  direccion: string | null,
  ciudad: string | null,
  activo: boolean,
  fechaCreacion: string
}
```

### Categoría (NUEVO)

**✅ Modelo**:
```javascript
{
  id: number,
  nombre: string,
  descripcion: string | null,
  activa: boolean,
  fechaCreacion: string
}
```

### Stock

**✅ Modelo**:
```javascript
{
  productoId: number,
  localId: number,
  cantidad: number,
  cantidadMinima: number,
  fechaActualizacion: string
}
```

**⚠️ IMPORTANTE**: El stock NO viene embebido en el producto. Debe obtenerse por separado.

### Venta

**✅ Modelo**:
```javascript
{
  id: number,
  localId: number,
  usuarioId: number,  // Se obtiene del token
  total: string,  // NUMERIC como string
  subtotal: string,
  observaciones: string | null,
  fechaCreacion: string,
  detalles: [
    {
      productoId: number,
      cantidad: number,
      precioUnitario: string,
      subtotal: string
    }
  ]
}
```

---

## 🔌 Endpoints Disponibles

### Autenticación

#### POST `/api/auth/login`

**Request**:
```javascript
{
  email: "usuario@example.com",
  password: "password123"
}
```

**Response**:
```javascript
{
  success: true,
  accessToken: "eyJhbGci...",
  refreshToken: "eyJhbGci...",
  user: {
    id: 1,
    email: "usuario@example.com",
    nombre: "Juan",
    apellido: "Pérez",
    rol: "OPERADOR"
  }
}
```

**Ejemplo de uso**:
```javascript
import { apiClient } from '$lib/api-client.js';

async function login(email, password) {
  const response = await apiClient.post('/api/auth/login', {
    email,
    password
  }, { requireAuth: false });

  if (response.success) {
    apiClient.setTokens(response.accessToken, response.refreshToken);
    return response.user;
  }
  
  throw new Error(response.message);
}
```

#### POST `/api/auth/register`

Similar a login, pero requiere también `nombre`, `apellido`, `rol`.

#### POST `/api/auth/refresh`

El cliente API maneja esto automáticamente.

---

### Locales (NUEVO - CRÍTICO)

#### GET `/api/saas/locales`

**Response**:
```javascript
{
  success: true,
  locales: [
    {
      id: 1,
      nombre: "Local Principal",
      direccion: "Calle Principal 123",
      ciudad: "Santiago",
      activo: true,
      fechaCreacion: "2025-01-15T10:30:00Z"
    }
  ],
  total: 1
}
```

**Ejemplo de uso**:
```javascript
async function obtenerLocales() {
  const response = await apiClient.get('/api/saas/locales');
  return response.locales;
}
```

**⚠️ IMPORTANTE**: Necesario para crear productos y gestionar stock.

---

### Categorías (NUEVO - CRÍTICO)

#### GET `/api/saas/categorias`

**Response**:
```javascript
{
  success: true,
  categorias: [
    {
      id: 1,
      nombre: "Electrónica",
      descripcion: "Productos electrónicos",
      activa: true,
      fechaCreacion: "2025-01-15T10:30:00Z"
    }
  ],
  total: 1
}
```

**Ejemplo de uso**:
```javascript
async function obtenerCategorias() {
  const response = await apiClient.get('/api/saas/categorias');
  return response.categorias;
}
```

**⚠️ IMPORTANTE**: Necesario para crear productos.

---

### Productos

#### GET `/api/saas/productos`

**Response**:
```javascript
{
  success: true,
  productos: [
    {
      id: 1,
      nombre: "Leche",
      descripcion: "Leche entera",
      categoriaId: 1,
      codigoBarras: "123456789",
      precioUnitario: "1500.00",
      activo: true,
      fechaCreacion: "2025-01-15T10:30:00Z",
      fechaActualizacion: "2025-01-15T10:30:00Z"
    }
  ],
  total: 1
}
```

**Ejemplo de uso**:
```javascript
async function obtenerProductos() {
  const response = await apiClient.get('/api/saas/productos');
  return response.productos;
}
```

#### POST `/api/saas/productos` (Solo Admin)

**Request**:
```javascript
{
  nombre: "Nuevo Producto",
  descripcion: "Descripción del producto",
  categoriaId: 1,  // ✅ ID de categoría
  codigoBarras: "987654321",
  precioUnitario: "2000.00"  // ✅ String
}
```

**Ejemplo de uso**:
```javascript
async function crearProducto(producto) {
  const response = await apiClient.post('/api/saas/productos', {
    nombre: producto.nombre,
    descripcion: producto.descripcion,
    categoriaId: producto.categoriaId,  // ✅ Usar ID, no string
    codigoBarras: producto.codigoBarras,
    precioUnitario: producto.precioUnitario?.toString()
  });
  
  return response.producto;
}
```

#### PUT `/api/saas/productos/{id}` (Solo Admin)

Similar a POST.

#### DELETE `/api/saas/productos/{id}` (Solo Admin)

Soft delete (marca `activo: false`).

---

### Stock

#### GET `/api/saas/stock?localId={localId}` (Opcional)

**Response**:
```javascript
{
  success: true,
  stock: [
    {
      productoId: 1,
      localId: 1,
      cantidad: 50,
      cantidadMinima: 10,
      fechaActualizacion: "2025-01-15T10:30:00Z"
    }
  ],
  total: 1
}
```

**Ejemplo de uso**:
```javascript
// Obtener stock de un local específico
async function obtenerStock(localId) {
  const response = await apiClient.get(`/api/saas/stock?localId=${localId}`);
  return response.stock;
}

// Obtener stock de todos los locales
async function obtenerTodoElStock() {
  const response = await apiClient.get('/api/saas/stock');
  return response.stock;
}
```

#### POST `/api/saas/stock`

**Request**:
```javascript
{
  productoId: 1,
  localId: 1,  // ✅ Requerido
  cantidad: 50,
  cantidadMinima: 10
}
```

**Ejemplo de uso**:
```javascript
async function actualizarStock(productoId, localId, cantidad) {
  const response = await apiClient.post('/api/saas/stock', {
    productoId,
    localId,  // ✅ Requerido
    cantidad,
    cantidadMinima: 0
  });
  
  return response.stock;
}
```

**⚠️ IMPORTANTE**: El stock requiere `localId`. No puede gestionarse sin un local.

---

### Ventas

#### GET `/api/saas/ventas`

**Response**:
```javascript
{
  success: true,
  ventas: [
    {
      id: 1,
      localId: 1,
      usuarioId: 1,
      total: "5000.00",
      subtotal: "5000.00",
      observaciones: null,
      fechaCreacion: "2025-01-15T10:30:00Z",
      detalles: [
        {
          productoId: 1,
          cantidad: 2,
          precioUnitario: "2500.00",
          subtotal: "5000.00"
        }
      ]
    }
  ],
  total: 1
}
```

#### POST `/api/saas/ventas`

**Request**:
```javascript
{
  localId: 1,  // ✅ Requerido
  detalles: [
    {
      productoId: 1,
      cantidad: 2,
      precioUnitario: "2500.00"
    }
  ],
  observaciones: "Venta al contado"  // Opcional
}
```

**Ejemplo de uso**:
```javascript
async function crearVenta(localId, detalles, observaciones = null) {
  const response = await apiClient.post('/api/saas/ventas', {
    localId,  // ✅ Requerido
    detalles: detalles.map(d => ({
      productoId: d.productoId,
      cantidad: d.cantidad,
      precioUnitario: d.precioUnitario.toString()
    })),
    observaciones
  });
  
  return response.venta;
}
```

**⚠️ IMPORTANTE**: 
- `localId` es requerido
- `usuarioId` se obtiene automáticamente del token
- `total` y `subtotal` se calculan automáticamente en el backend

---

## 📝 Checklist de Migración

### Fase 1: Preparación

- [ ] Crear archivo de configuración (`src/lib/config.js`)
- [ ] Crear cliente API (`src/lib/api-client.js`)
- [ ] Configurar variable de entorno `VITE_API_BASE_URL`
- [ ] Probar conexión al backend (health check)

### Fase 2: Autenticación

- [ ] Implementar login con backend
- [ ] Guardar tokens en localStorage
- [ ] Implementar refresh token automático
- [ ] Implementar logout (limpiar tokens)
- [ ] Proteger rutas que requieren autenticación
- [ ] Redirigir a login si token expira

### Fase 3: Locales y Categorías (NUEVO)

- [ ] Crear servicio para obtener locales
- [ ] Crear servicio para obtener categorías
- [ ] Mostrar locales en selector/dropdown
- [ ] Mostrar categorías en selector/dropdown
- [ ] Usar estos datos al crear productos

### Fase 4: Productos

- [ ] Eliminar endpoint propio `/api/productos`
- [ ] Reemplazar por llamada a `/api/saas/productos`
- [ ] Ajustar modelo de Producto (categoriaId, codigoBarras, etc.)
- [ ] Actualizar formulario de creación (usar categorías)
- [ ] Actualizar formulario de edición
- [ ] Eliminar campo `sku` (usar `codigoBarras`)
- [ ] Eliminar campo `categoria` string (usar `categoriaId` number)

### Fase 5: Stock

- [ ] Eliminar endpoint propio `/api/inventario` o similar
- [ ] Reemplazar por llamada a `/api/saas/stock`
- [ ] Obtener stock por separado (no embebido en producto)
- [ ] Actualizar componente de gestión de stock
- [ ] Incluir `localId` en todas las operaciones de stock
- [ ] Mostrar stock por local

### Fase 6: Ventas

- [ ] Eliminar endpoint propio `/api/ventas` o similar
- [ ] Reemplazar por llamada a `/api/saas/ventas`
- [ ] Incluir `localId` al crear venta
- [ ] Ajustar modelo de Venta
- [ ] Validar que detalles incluyan precioUnitario

### Fase 7: Limpieza

- [ ] Eliminar todos los endpoints propios de SvelteKit
- [ ] Eliminar `datosSimulados.js` o similar
- [ ] Eliminar estado en memoria
- [ ] Actualizar todos los componentes que usan datos hardcodeados
- [ ] Probar todos los flujos completos

---

## 🚨 Errores Comunes y Soluciones

### Error 401 Unauthorized

**Causa**: Token JWT inválido o expirado

**Solución**:
```javascript
// Verificar que el token se incluya en headers
headers: {
  'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
}
```

### Error 403 Forbidden

**Causa**: Usuario no tiene permisos (no es Admin)

**Solución**: Verificar rol del usuario antes de permitir crear/actualizar

### Error 400 Bad Request

**Causa**: Modelo de datos no coincide

**Solución**: 
- Verificar que `categoriaId` sea number, no string
- Verificar que `precioUnitario` sea string, no number
- Verificar que `localId` esté incluido en stock y ventas

### Error 404 Not Found

**Causa**: Endpoint no existe o ruta incorrecta

**Solución**: Verificar URL en Swagger UI: `https://siga-backend-production.up.railway.app/swagger-ui/index.html`

### Stock no se muestra

**Causa**: Intentando acceder a `producto.stock` (no existe)

**Solución**: Obtener stock por separado:
```javascript
// ❌ INCORRECTO
const stock = producto.stock;

// ✅ CORRECTO
const stock = await obtenerStock(producto.id, localId);
```

---

## 🧪 Pruebas Recomendadas

### 1. Autenticación
- [ ] Login con credenciales válidas
- [ ] Login con credenciales inválidas
- [ ] Refresh token automático
- [ ] Logout limpia tokens

### 2. Productos
- [ ] Listar productos
- [ ] Crear producto (con categoría)
- [ ] Actualizar producto
- [ ] Eliminar producto (soft delete)

### 3. Stock
- [ ] Listar stock por local
- [ ] Actualizar stock
- [ ] Verificar que requiere localId

### 4. Ventas
- [ ] Crear venta (con localId)
- [ ] Listar ventas
- [ ] Verificar cálculo de total

### 5. Integración Completa
- [ ] Flujo completo: Login → Productos → Stock → Venta
- [ ] Verificar que no hay datos hardcodeados
- [ ] Verificar que todos los datos vienen del backend

---

## 📚 Recursos Adicionales

### Documentación del Backend

- **Swagger UI**: `https://siga-backend-production.up.railway.app/swagger-ui/index.html`
- **API Docs**: `https://siga-backend-production.up.railway.app/v3/api-docs`
- **Health Check**: `https://siga-backend-production.up.railway.app/health`

### Documentos de Referencia

- `docs/API_FRONTEND_APPWEB.md` - Documentación técnica de APIs
- `docs/ESQUEMAS_DATABASE.md` - Esquemas de base de datos
- `CHALLA/docs/INSTRUCCIONES_APP_WEB.md` - Instrucciones detalladas (si existe)

---

## ✅ Criterios de Éxito

La migración se considera exitosa cuando:

1. ✅ Todos los endpoints propios han sido eliminados
2. ✅ Todos los datos vienen del backend
3. ✅ La autenticación JWT funciona correctamente
4. ✅ Los productos se crean con categorías (ID)
5. ✅ El stock se gestiona con locales (ID)
6. ✅ Las ventas se crean con localId
7. ✅ No hay datos hardcodeados en el código
8. ✅ Todos los flujos funcionan end-to-end

---

## 🆘 Soporte

Si tienes dudas o problemas:

1. Revisar Swagger UI para ver ejemplos de requests/responses
2. Verificar logs del backend (si tienes acceso)
3. Probar endpoints directamente con Postman/curl
4. Contactar al equipo de backend con:
   - URL del endpoint que falla
   - Request que estás enviando
   - Response que recibes
   - Código de error

---

**¡Éxito con la migración!** 🚀
