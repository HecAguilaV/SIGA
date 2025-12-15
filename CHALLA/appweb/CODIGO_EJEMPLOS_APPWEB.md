# Código de Ejemplo - App Web

**Para**: Equipo de desarrollo App Web  
**Uso**: Copiar y adaptar estos ejemplos según tu estructura de proyecto

---

## 1. Cliente API Completo

**Archivo**: `src/lib/api-client.js`

```javascript
// src/lib/api-client.js
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 
  'https://siga-backend-production.up.railway.app';

class ApiClient {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  getToken() {
    return localStorage.getItem('accessToken');
  }

  setTokens(accessToken, refreshToken) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  clearTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

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
      
      if (response.status === 401 && options.requireAuth !== false) {
        const refreshed = await this.refreshToken();
        if (refreshed) {
          config.headers['Authorization'] = `Bearer ${this.getToken()}`;
          const retryResponse = await fetch(url, config);
          return this.handleResponse(retryResponse);
        } else {
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

  async handleResponse(response) {
    const data = await response.json();
    
    if (!response.ok) {
      throw new Error(data.message || `HTTP ${response.status}`);
    }
    
    return data;
  }

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

export const apiClient = new ApiClient();
```

---

## 2. Servicios de Autenticación

**Archivo**: `src/lib/services/auth.js`

```javascript
// src/lib/services/auth.js
import { apiClient } from '../api-client.js';

export async function login(email, password) {
  try {
    const response = await apiClient.post('/api/auth/login', {
      email,
      password
    }, { requireAuth: false });

    if (response.success) {
      apiClient.setTokens(response.accessToken, response.refreshToken);
      return {
        user: response.user,
        token: response.accessToken
      };
    }
    
    throw new Error(response.message || 'Error al iniciar sesión');
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}

export async function register(email, password, nombre, apellido = null, rol = 'OPERADOR') {
  try {
    const response = await apiClient.post('/api/auth/register', {
      email,
      password,
      nombre,
      apellido,
      rol
    }, { requireAuth: false });

    if (response.success) {
      apiClient.setTokens(response.accessToken, response.refreshToken);
      return {
        user: response.user,
        token: response.accessToken
      };
    }
    
    throw new Error(response.message || 'Error al registrar');
  } catch (error) {
    console.error('Register error:', error);
    throw error;
  }
}

export function logout() {
  apiClient.clearTokens();
  window.location.href = '/login';
}

export function isAuthenticated() {
  return !!apiClient.getToken();
}

export function getCurrentUser() {
  // Obtener usuario del token (decodificar JWT) o guardarlo en store
  // Por ahora, retornar null y obtener desde el backend cuando sea necesario
  return null;
}
```

---

## 3. Servicios de Locales (NUEVO)

**Archivo**: `src/lib/services/locales.js`

```javascript
// src/lib/services/locales.js
import { apiClient } from '../api-client.js';

export async function obtenerLocales() {
  try {
    const response = await apiClient.get('/api/saas/locales');
    return response.locales || [];
  } catch (error) {
    console.error('Error al obtener locales:', error);
    throw error;
  }
}

export async function obtenerLocal(id) {
  try {
    const response = await apiClient.get(`/api/saas/locales/${id}`);
    return response.local;
  } catch (error) {
    console.error('Error al obtener local:', error);
    throw error;
  }
}

export async function crearLocal(local) {
  try {
    const response = await apiClient.post('/api/saas/locales', {
      nombre: local.nombre,
      direccion: local.direccion,
      ciudad: local.ciudad
    });
    return response.local;
  } catch (error) {
    console.error('Error al crear local:', error);
    throw error;
  }
}

export async function actualizarLocal(id, local) {
  try {
    const response = await apiClient.put(`/api/saas/locales/${id}`, {
      nombre: local.nombre,
      direccion: local.direccion,
      ciudad: local.ciudad
    });
    return response.local;
  } catch (error) {
    console.error('Error al actualizar local:', error);
    throw error;
  }
}

export async function eliminarLocal(id) {
  try {
    await apiClient.delete(`/api/saas/locales/${id}`);
    return true;
  } catch (error) {
    console.error('Error al eliminar local:', error);
    throw error;
  }
}
```

---

## 4. Servicios de Categorías (NUEVO)

**Archivo**: `src/lib/services/categorias.js`

```javascript
// src/lib/services/categorias.js
import { apiClient } from '../api-client.js';

export async function obtenerCategorias() {
  try {
    const response = await apiClient.get('/api/saas/categorias');
    return response.categorias || [];
  } catch (error) {
    console.error('Error al obtener categorías:', error);
    throw error;
  }
}

export async function obtenerCategoria(id) {
  try {
    const response = await apiClient.get(`/api/saas/categorias/${id}`);
    return response.categoria;
  } catch (error) {
    console.error('Error al obtener categoría:', error);
    throw error;
  }
}

export async function crearCategoria(categoria) {
  try {
    const response = await apiClient.post('/api/saas/categorias', {
      nombre: categoria.nombre,
      descripcion: categoria.descripcion
    });
    return response.categoria;
  } catch (error) {
    console.error('Error al crear categoría:', error);
    throw error;
  }
}

export async function actualizarCategoria(id, categoria) {
  try {
    const response = await apiClient.put(`/api/saas/categorias/${id}`, {
      nombre: categoria.nombre,
      descripcion: categoria.descripcion
    });
    return response.categoria;
  } catch (error) {
    console.error('Error al actualizar categoría:', error);
    throw error;
  }
}

export async function eliminarCategoria(id) {
  try {
    await apiClient.delete(`/api/saas/categorias/${id}`);
    return true;
  } catch (error) {
    console.error('Error al eliminar categoría:', error);
    throw error;
  }
}
```

---

## 5. Servicios de Productos

**Archivo**: `src/lib/services/productos.js`

```javascript
// src/lib/services/productos.js
import { apiClient } from '../api-client.js';

export async function obtenerProductos() {
  try {
    const response = await apiClient.get('/api/saas/productos');
    return response.productos || [];
  } catch (error) {
    console.error('Error al obtener productos:', error);
    throw error;
  }
}

export async function obtenerProducto(id) {
  try {
    const response = await apiClient.get(`/api/saas/productos/${id}`);
    return response.producto;
  } catch (error) {
    console.error('Error al obtener producto:', error);
    throw error;
  }
}

export async function crearProducto(producto) {
  try {
    const response = await apiClient.post('/api/saas/productos', {
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      categoriaId: producto.categoriaId,  // ✅ ID, no string
      codigoBarras: producto.codigoBarras,  // ✅ Usar este campo (no sku)
      precioUnitario: producto.precioUnitario?.toString()  // ✅ String
    });
    return response.producto;
  } catch (error) {
    console.error('Error al crear producto:', error);
    throw error;
  }
}

export async function actualizarProducto(id, producto) {
  try {
    const response = await apiClient.put(`/api/saas/productos/${id}`, {
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      categoriaId: producto.categoriaId,
      codigoBarras: producto.codigoBarras,
      precioUnitario: producto.precioUnitario?.toString()
    });
    return response.producto;
  } catch (error) {
    console.error('Error al actualizar producto:', error);
    throw error;
  }
}

export async function eliminarProducto(id) {
  try {
    await apiClient.delete(`/api/saas/productos/${id}`);
    return true;
  } catch (error) {
    console.error('Error al eliminar producto:', error);
    throw error;
  }
}
```

---

## 6. Servicios de Stock

**Archivo**: `src/lib/services/stock.js`

```javascript
// src/lib/services/stock.js
import { apiClient } from '../api-client.js';

export async function obtenerStock(localId = null) {
  try {
    const url = localId 
      ? `/api/saas/stock?localId=${localId}`
      : '/api/saas/stock';
    
    const response = await apiClient.get(url);
    return response.stock || [];
  } catch (error) {
    console.error('Error al obtener stock:', error);
    throw error;
  }
}

export async function obtenerStockEspecifico(productoId, localId) {
  try {
    const response = await apiClient.get(`/api/saas/stock/${productoId}/${localId}`);
    return response.stock;
  } catch (error) {
    console.error('Error al obtener stock específico:', error);
    throw error;
  }
}

export async function actualizarStock(productoId, localId, cantidad, cantidadMinima = 0) {
  try {
    const response = await apiClient.post('/api/saas/stock', {
      productoId,
      localId,  // ✅ REQUERIDO
      cantidad,
      cantidadMinima
    });
    return response.stock;
  } catch (error) {
    console.error('Error al actualizar stock:', error);
    throw error;
  }
}

// Helper para obtener stock de un producto en todos los locales
export async function obtenerStockPorProducto(productoId) {
  try {
    const stock = await obtenerStock();
    return stock.filter(s => s.productoId === productoId);
  } catch (error) {
    console.error('Error al obtener stock por producto:', error);
    throw error;
  }
}
```

---

## 7. Servicios de Ventas

**Archivo**: `src/lib/services/ventas.js`

```javascript
// src/lib/services/ventas.js
import { apiClient } from '../api-client.js';

export async function obtenerVentas() {
  try {
    const response = await apiClient.get('/api/saas/ventas');
    return response.ventas || [];
  } catch (error) {
    console.error('Error al obtener ventas:', error);
    throw error;
  }
}

export async function crearVenta(venta) {
  try {
    const response = await apiClient.post('/api/saas/ventas', {
      localId: venta.localId,  // ✅ REQUERIDO
      detalles: venta.detalles.map(d => ({
        productoId: d.productoId,
        cantidad: d.cantidad,
        precioUnitario: d.precioUnitario.toString()  // ✅ String
      })),
      observaciones: venta.observaciones
    });
    return response.venta;
  } catch (error) {
    console.error('Error al crear venta:', error);
    throw error;
  }
}
```

---

## 8. Ejemplo de Componente Svelte - Login

**Archivo**: `src/routes/login/+page.svelte`

```svelte
<script>
  import { login } from '$lib/services/auth.js';
  import { goto } from '$app/navigation';

  let email = '';
  let password = '';
  let error = '';
  let loading = false;

  async function handleLogin() {
    if (!email || !password) {
      error = 'Por favor completa todos los campos';
      return;
    }

    loading = true;
    error = '';

    try {
      const result = await login(email, password);
      // Redirigir al dashboard
      goto('/dashboard');
    } catch (err) {
      error = err.message || 'Error al iniciar sesión';
    } finally {
      loading = false;
    }
  }
</script>

<div class="login-container">
  <h1>Iniciar Sesión</h1>
  
  {#if error}
    <div class="error">{error}</div>
  {/if}

  <form on:submit|preventDefault={handleLogin}>
    <input
      type="email"
      bind:value={email}
      placeholder="Email"
      required
    />
    
    <input
      type="password"
      bind:value={password}
      placeholder="Contraseña"
      required
    />
    
    <button type="submit" disabled={loading}>
      {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
    </button>
  </form>
</div>
```

---

## 9. Ejemplo de Componente - Listar Productos

**Archivo**: `src/routes/productos/+page.svelte`

```svelte
<script>
  import { onMount } from 'svelte';
  import { obtenerProductos } from '$lib/services/productos.js';
  import { obtenerCategorias } from '$lib/services/categorias.js';

  let productos = [];
  let categorias = [];
  let loading = true;
  let error = '';

  onMount(async () => {
    try {
      // Cargar productos y categorías en paralelo
      [productos, categorias] = await Promise.all([
        obtenerProductos(),
        obtenerCategorias()
      ]);
    } catch (err) {
      error = err.message || 'Error al cargar productos';
    } finally {
      loading = false;
    }
  });

  function getCategoriaNombre(categoriaId) {
    const categoria = categorias.find(c => c.id === categoriaId);
    return categoria?.nombre || 'Sin categoría';
  }
</script>

<div class="productos-container">
  <h1>Productos</h1>

  {#if loading}
    <p>Cargando productos...</p>
  {:else if error}
    <div class="error">{error}</div>
  {:else}
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Categoría</th>
          <th>Precio</th>
          <th>Activo</th>
        </tr>
      </thead>
      <tbody>
        {#each productos as producto}
          <tr>
            <td>{producto.id}</td>
            <td>{producto.nombre}</td>
            <td>{getCategoriaNombre(producto.categoriaId)}</td>
            <td>${producto.precioUnitario || 'N/A'}</td>
            <td>{producto.activo ? 'Sí' : 'No'}</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
```

---

## 10. Ejemplo de Componente - Crear Producto

**Archivo**: `src/routes/productos/nuevo/+page.svelte`

```svelte
<script>
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { crearProducto } from '$lib/services/productos.js';
  import { obtenerCategorias } from '$lib/services/categorias.js';

  let categorias = [];
  let nombre = '';
  let descripcion = '';
  let categoriaId = null;
  let codigoBarras = '';
  let precioUnitario = '';
  let loading = false;
  let error = '';

  onMount(async () => {
    try {
      categorias = await obtenerCategorias();
    } catch (err) {
      error = err.message || 'Error al cargar categorías';
    }
  });

  async function handleSubmit() {
    if (!nombre) {
      error = 'El nombre es requerido';
      return;
    }

    loading = true;
    error = '';

    try {
      await crearProducto({
        nombre,
        descripcion: descripcion || null,
        categoriaId: categoriaId || null,
        codigoBarras: codigoBarras || null,
        precioUnitario: precioUnitario || null
      });
      
      goto('/productos');
    } catch (err) {
      error = err.message || 'Error al crear producto';
    } finally {
      loading = false;
    }
  }
</script>

<div class="form-container">
  <h1>Nuevo Producto</h1>

  {#if error}
    <div class="error">{error}</div>
  {/if}

  <form on:submit|preventDefault={handleSubmit}>
    <label>
      Nombre *
      <input type="text" bind:value={nombre} required />
    </label>

    <label>
      Descripción
      <textarea bind:value={descripcion}></textarea>
    </label>

    <label>
      Categoría
      <select bind:value={categoriaId}>
        <option value={null}>Seleccionar categoría</option>
        {#each categorias as categoria}
          <option value={categoria.id}>{categoria.nombre}</option>
        {/each}
      </select>
    </label>

    <label>
      Código de Barras
      <input type="text" bind:value={codigoBarras} />
    </label>

    <label>
      Precio Unitario
      <input type="number" step="0.01" bind:value={precioUnitario} />
    </label>

    <button type="submit" disabled={loading}>
      {loading ? 'Creando...' : 'Crear Producto'}
    </button>
  </form>
</div>
```

---

## 11. Ejemplo de Store Svelte (Opcional)

**Archivo**: `src/lib/stores/productos.js`

```javascript
// src/lib/stores/productos.js
import { writable } from 'svelte/store';
import { obtenerProductos } from '../services/productos.js';

function createProductosStore() {
  const { subscribe, set, update } = writable([]);
  let loaded = false;

  return {
    subscribe,
    
    async load() {
      if (loaded) return;
      
      try {
        const productos = await obtenerProductos();
        set(productos);
        loaded = true;
      } catch (error) {
        console.error('Error loading productos:', error);
        throw error;
      }
    },
    
    async refresh() {
      loaded = false;
      await this.load();
    },
    
    add(producto) {
      update(list => [...list, producto]);
    },
    
    update(id, producto) {
      update(list => list.map(p => p.id === id ? producto : p));
    },
    
    remove(id) {
      update(list => list.filter(p => p.id !== id));
    }
  };
}

export const productos = createProductosStore();
```

---

## 12. Variable de Entorno

**Archivo**: `.env` o `.env.local`

```env
VITE_API_BASE_URL=https://siga-backend-production.up.railway.app
```

**En Vercel**: Configurar como variable de entorno en el dashboard.

---

## 📝 Notas Importantes

1. **Autenticación**: Todos los endpoints (excepto auth) requieren token JWT
2. **Locales**: CRÍTICO - Necesario para stock y ventas
3. **Categorías**: CRÍTICO - Necesario para productos
4. **Stock**: Obtener por separado, no viene embebido en producto
5. **Precios**: Siempre como string (NUMERIC de PostgreSQL)
6. **IDs**: Usar números, no strings

---

**¡Éxito con la implementación!** 🚀
