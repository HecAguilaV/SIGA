# 🤖 Migración del Asistente IA - Datos Reales

## 🔴 Problema Actual

El asistente IA (`/api/chat`) usa **datos simulados** (`datosGlobales`) en lugar de datos reales del backend.

**Código actual**:
```javascript
// src/routes/api/chat/+server.js
const datos = datosGlobales; // ❌ Datos simulados
```

**Impacto**:
- ❌ El asistente no ve los datos reales del usuario
- ❌ No puede operar sobre productos/locales reales
- ❌ Respuestas incorrectas o irrelevantes
- ❌ Operaciones CRUD no funcionan con backend real

---

## ✅ Solución Propuesta

### 1. Cargar Datos Reales del Backend

**Crear función** para cargar datos del usuario autenticado:

```javascript
// src/routes/api/chat/+server.js

async function cargarDatosReales(accessToken) {
  // Obtener datos del backend usando el token
  const [productos, locales, categorias, stock] = await Promise.all([
    obtenerProductos(accessToken),
    obtenerLocales(accessToken),
    obtenerCategorias(accessToken),
    obtenerStock(accessToken)
  ]);

  return {
    productos,
    locales,
    categorias,
    stock,
    // Formato compatible con el prompt actual
    ventasSemana: [], // TODO: Implementar cuando backend tenga endpoint
    ventasPorDia: []  // TODO: Implementar cuando backend tenga endpoint
  };
}
```

### 2. Actualizar Construcción del Prompt

**Cambios en `construirPrompt()`**:

```javascript
const construirPrompt = (preguntaUsuario, datosReales) => {
  // Usar datos reales en lugar de datosGlobales
  const locales = datosReales.locales?.map((l) => `${l.nombre} (ID: ${l.id})`).join(', ') || 'N/A';
  const productos = datosReales.productos?.map((p) => `${p.nombre} (${p.categoriaId ? 'Categoría ID: ' + p.categoriaId : 'Sin categoría'})`).slice(0, 10).join(', ') || 'N/A';
  
  // Resto del prompt...
};
```

### 3. Migrar Operaciones CRUD

**En `AsistenteContextual.svelte`**:

```javascript
// ❌ ANTES
await fetch('/api/productos/crear', ...)

// ✅ DESPUÉS
import { crearProducto } from '$lib/services/productos.js';
import { actualizarStock } from '$lib/services/stock.js';

// Crear producto
await crearProducto({
  nombre: crud.nombre,
  categoriaId: obtenerCategoriaIdPorNombre(crud.categoria),
  codigoBarras: crud.sku || null
});

// Actualizar stock
await actualizarStock(
  productoId,
  localId,
  cantidad
);
```

### 4. Obtener Token del Usuario

**En `/api/chat/+server.js`**:

```javascript
export const POST = async ({ request, cookies }) => {
  // Obtener token del usuario
  const accessToken = cookies.get('accessToken') || 
    request.headers.get('authorization')?.replace('Bearer ', '');
  
  if (!accessToken) {
    return json({ respuesta: 'Debes iniciar sesión para usar el asistente.' }, { status: 401 });
  }

  // Cargar datos reales
  const datosReales = await cargarDatosReales(accessToken);
  
  // Construir prompt con datos reales
  const prompt = construirPrompt(mensaje, datosReales);
  
  // Resto del código...
};
```

---

## 📋 Cambios Requeridos

### Archivos a Modificar

1. **`src/routes/api/chat/+server.js`**
   - ✅ Cargar datos reales del backend
   - ✅ Obtener token del usuario
   - ✅ Actualizar prompt con datos reales

2. **`src/lib/components/AsistenteContextual.svelte`**
   - ✅ Usar servicios del backend
   - ✅ Eliminar endpoints propios
   - ✅ Actualizar operaciones CRUD

3. **Crear helper functions**
   - ✅ Función para mapear nombres a IDs
   - ✅ Función para cargar datos del usuario
   - ✅ Función para procesar CRUD del asistente

---

## 🔧 Implementación Detallada

### Paso 1: Helper para Cargar Datos

```javascript
// src/lib/services/chat-helper.js

import { obtenerProductos, obtenerLocales, obtenerCategorias, obtenerStock } from './index.js';
import { apiClient } from '../api-client.js';

export async function cargarDatosParaAsistente() {
  try {
    const [productos, locales, categorias, stock] = await Promise.all([
      obtenerProductos(),
      obtenerLocales(),
      obtenerCategorias(),
      obtenerStock()
    ]);

    return {
      productos,
      locales,
      categorias,
      stock,
      // Formato para el prompt
      productosFormateados: productos.map(p => ({
        id: p.id,
        nombre: p.nombre,
        categoria: categorias.find(c => c.id === p.categoriaId)?.nombre || 'Sin categoría',
        codigoBarras: p.codigoBarras
      }))
    };
  } catch (error) {
    console.error('Error cargando datos para asistente:', error);
    return { productos: [], locales: [], categorias: [], stock: [] };
  }
}
```

### Paso 2: Actualizar `/api/chat/+server.js`

```javascript
import { cargarDatosParaAsistente } from '$lib/services/chat-helper.js';
import { obtenerProductos, crearProducto, actualizarProducto } from '$lib/services/productos.js';
import { actualizarStock } from '$lib/services/stock.js';

const construirPrompt = (preguntaUsuario, datosReales) => {
  const locales = datosReales.locales?.map((l) => `${l.nombre} (ID: ${l.id})`).join(', ') || 'N/A';
  const productos = datosReales.productosFormateados?.map((p) => `${p.nombre} (${p.categoria})`).slice(0, 20).join(', ') || 'N/A';
  
  return `Eres SIGA, asistente inteligente de gestión de inventario...
  
📊 CONTEXTO REAL:
- Locales: ${locales}
- Productos: ${productos}
- Total productos: ${datosReales.productos?.length || 0}

...`;
};

export const POST = async ({ request }) => {
  // Verificar autenticación
  const token = request.headers.get('authorization')?.replace('Bearer ', '');
  if (!token) {
    return json({ respuesta: 'Debes iniciar sesión para usar el asistente.' }, { status: 401 });
  }

  // Cargar datos reales
  const datosReales = await cargarDatosParaAsistente();
  
  // Construir prompt
  const prompt = construirPrompt(mensaje, datosReales);
  
  // Resto...
};
```

### Paso 3: Procesar CRUD del Asistente

```javascript
// Función para procesar operaciones CRUD del asistente
async function procesarCRUDAsistente(crud, datosReales) {
  if (crud.accion === 'crear_producto') {
    // Buscar categoría por nombre
    const categoria = datosReales.categorias.find(c => 
      c.nombre.toLowerCase() === crud.categoria?.toLowerCase()
    );
    
    await crearProducto({
      nombre: crud.nombre,
      categoriaId: categoria?.id || null,
      codigoBarras: crud.sku || null
    });
  }
  
  if (crud.accion === 'agregar_stock') {
    // Buscar producto y local por nombre
    const producto = datosReales.productos.find(p => 
      p.nombre.toLowerCase().includes(crud.producto.toLowerCase())
    );
    const local = datosReales.locales.find(l => 
      l.nombre.toLowerCase().includes(crud.local.toLowerCase())
    );
    
    if (producto && local) {
      await actualizarStock(
        producto.id,
        local.id,
        crud.cantidad
      );
    }
  }
  
  // Más operaciones...
}
```

---

## ⚠️ Consideraciones Importantes

### 1. Autenticación

El asistente necesita acceso al token del usuario para cargar sus datos.

**Opciones**:
- ✅ Pasar token en headers (recomendado)
- ✅ Usar cookies (si está configurado)
- ✅ Obtener del contexto de SvelteKit

### 2. Mapeo de Nombres a IDs

El asistente trabaja con nombres (ej: "Canela", "ITR"), pero el backend usa IDs.

**Solución**:
- Buscar por nombre (fuzzy matching)
- Mostrar error si no encuentra
- Sugerir alternativas

### 3. Operaciones en Lote

El asistente puede querer hacer múltiples operaciones.

**Solución**:
- Procesar secuencialmente
- Mostrar progreso
- Manejar errores individualmente

---

## 🎯 Resultado Esperado

Después de la migración:

- ✅ Asistente ve datos reales del usuario
- ✅ Puede crear/editar productos reales
- ✅ Puede actualizar stock real
- ✅ Respuestas precisas y relevantes
- ✅ Operaciones CRUD funcionan con backend

---

**Esta migración es crítica para que el asistente IA funcione correctamente en el contexto SaaS real.** 🤖
