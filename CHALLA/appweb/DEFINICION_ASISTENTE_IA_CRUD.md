# 🤖 Definición: Asistente IA con Capacidades CRUD

## 🎯 IMPORTANCIA CRÍTICA

**El Asistente IA con capacidades CRUD es EL ALMA DEL PROYECTO SIGA.**

No es una funcionalidad opcional o secundaria. Es la característica principal que diferencia a SIGA de otros sistemas de inventario.

---

## 📋 Capacidades CRUD del Asistente

### 1. ✅ Crear Producto (`crear_producto`)

**Descripción**: El asistente puede crear nuevos productos mediante lenguaje natural.

**Ejemplo de uso**:
```
Usuario: "Crear producto Mouse Logitech en categoría Electrónica"
```

**Lo que hace el asistente**:
1. Detecta la intención de crear producto
2. Extrae: nombre="Mouse Logitech", categoria="Electrónica"
3. Busca la categoría por nombre
4. Llama a `crearProducto()` con los datos
5. Retorna confirmación al usuario
6. Dispara evento `producto-creado` para actualizar la UI

**Código backend**:
```javascript
if (crud.accion === 'crear_producto') {
  const categoria = buscarCategoriaPorNombre(crud.categoria, datosCache.categorias);
  const producto = await crearProducto({
    nombre: crud.nombre,
    categoriaId: categoria?.id || null,
    codigoBarras: crud.codigoBarras || crud.sku || null,
    descripcion: null,
    precioUnitario: null
  });
  window.dispatchEvent(new CustomEvent('producto-creado'));
}
```

---

### 2. ✏️ Editar Producto (`editar_producto`)

**Descripción**: El asistente puede modificar productos existentes.

**Ejemplo de uso**:
```
Usuario: "Cambiar el nombre de Mouse Logitech a Mouse Logitech Pro"
Usuario: "Actualizar el código de barras de Mouse a 123456789"
```

**Lo que hace el asistente**:
1. Detecta la intención de editar
2. Extrae: id del producto, campos a modificar
3. Busca el producto por nombre o ID
4. Llama a `actualizarProducto()` con los cambios
5. Retorna confirmación
6. Dispara evento `producto-actualizado`

**Código backend**:
```javascript
if (crud.accion === 'editar_producto') {
  const categoria = crud.categoria 
    ? buscarCategoriaPorNombre(crud.categoria, datosCache.categorias)
    : null;
  const producto = await actualizarProducto(crud.id, {
    nombre: crud.nombre || undefined,
    categoriaId: categoria?.id || undefined,
    codigoBarras: crud.codigoBarras || crud.sku || undefined
  });
  window.dispatchEvent(new CustomEvent('producto-actualizado'));
}
```

---

### 3. 🗑️ Eliminar Producto (`eliminar_producto`)

**Descripción**: El asistente puede eliminar/desactivar productos.

**Ejemplo de uso**:
```
Usuario: "Eliminar el producto Mouse Logitech Pro"
Usuario: "Borrar el producto con ID 123"
```

**Lo que hace el asistente**:
1. Detecta la intención de eliminar
2. Extrae: id del producto
3. Llama a `eliminarProducto(id)`
4. Retorna confirmación
5. Dispara evento `producto-eliminado`

**Código backend**:
```javascript
if (crud.accion === 'eliminar_producto') {
  await eliminarProducto(crud.id);
  window.dispatchEvent(new CustomEvent('producto-eliminado'));
}
```

---

### 4. ➕ Agregar Stock (`agregar_stock`)

**Descripción**: El asistente puede aumentar el stock de un producto en un local.

**Ejemplo de uso**:
```
Usuario: "Agregar 10 unidades de Mouse Logitech en el local Central"
Usuario: "Sumar 5 Mouse en Central"
```

**Lo que hace el asistente**:
1. Detecta la intención de agregar stock
2. Extrae: producto="Mouse Logitech", cantidad=10, local="Central"
3. Busca producto y local por nombre
4. Obtiene stock actual del producto en ese local
5. Calcula nueva cantidad: `stockActual + cantidad`
6. Llama a `actualizarStock(productoId, localId, nuevaCantidad)`
7. Retorna confirmación
8. Dispara evento `stock-actualizado`

**Código backend**:
```javascript
if (crud.accion === 'agregar_stock') {
  const producto = buscarProductoPorNombre(crud.producto, datosCache.productos);
  const local = buscarLocalPorNombre(crud.local, datosCache.locales);
  const stockActual = await obtenerStock(local.id);
  const stockDelProducto = stockActual.find(s => s.productoId === producto.id);
  const cantidadActual = stockDelProducto?.cantidad || 0;
  const nuevaCantidad = cantidadActual + crud.cantidad;
  await actualizarStock(producto.id, local.id, nuevaCantidad);
  window.dispatchEvent(new CustomEvent('stock-actualizado'));
}
```

---

### 5. ➖ Reducir Stock (`reducir_stock`)

**Descripción**: El asistente puede disminuir el stock de un producto en un local.

**Ejemplo de uso**:
```
Usuario: "Reducir 5 unidades de Mouse Logitech en Central"
Usuario: "Quitar 3 Mouse del local Central"
```

**Lo que hace el asistente**:
1. Detecta la intención de reducir stock
2. Extrae: producto, cantidad, local
3. Busca producto y local por nombre
4. Obtiene stock actual
5. Calcula nueva cantidad: `Math.max(0, stockActual - cantidad)`
6. Llama a `actualizarStock()` con la nueva cantidad
7. Retorna confirmación
8. Dispara evento `stock-actualizado`

**Código backend**:
```javascript
if (crud.accion === 'reducir_stock') {
  // Similar a agregar_stock pero resta
  const nuevaCantidad = Math.max(0, cantidadActual - crud.cantidad);
  await actualizarStock(producto.id, local.id, nuevaCantidad);
  window.dispatchEvent(new CustomEvent('stock-actualizado'));
}
```

---

### 6. 🔍 Consultas (Sin CRUD)

**Descripción**: El asistente puede responder preguntas sobre el inventario.

**Ejemplos de uso**:
```
Usuario: "¿Qué productos hay?"
Usuario: "¿Cuánto stock hay de Mouse en Central?"
Usuario: "¿En qué locales no hay Mouse?"
Usuario: "Muéstrame un resumen del inventario"
```

**Lo que hace el asistente**:
1. Detecta que es una consulta (no una operación CRUD)
2. Carga datos del inventario desde el backend
3. Procesa la pregunta con Gemini
4. Retorna respuesta en lenguaje natural
5. Puede generar gráficos si se solicita

---

## 🔄 Flujo Completo de una Operación CRUD

```
1. Usuario escribe: "Agregar 10 Mouse en Central"
   ↓
2. Frontend envía mensaje a /api/chat
   ↓
3. SvelteKit endpoint (/api/chat/+server.js) recibe el mensaje
   ↓
4. Endpoint reenvía al backend: POST /api/saas/chat
   ↓
5. Backend procesa con Gemini (con contexto RAG del inventario)
   ↓
6. Gemini retorna respuesta con [CRUD_START]...{accion, datos}...[CRUD_END]
   ↓
7. Frontend parsea la respuesta y detecta el bloque CRUD
   ↓
8. Frontend llama a procesarCRUD() con los datos extraídos
   ↓
9. procesarCRUD() ejecuta la operación (crearProducto, actualizarStock, etc.)
   ↓
10. Se dispara evento personalizado (producto-creado, stock-actualizado, etc.)
   ↓
11. La página principal escucha el evento y recarga datos
   ↓
12. El usuario ve la actualización en tiempo real
```

---

## 📊 Formato de Respuesta del Backend

El backend debe retornar respuestas que incluyan bloques CRUD cuando sea necesario:

**Ejemplo de respuesta con CRUD**:
```
Hola! Voy a agregar 10 unidades de Mouse Logitech en Central.

[CRUD_START]
{
  "accion": "agregar_stock",
  "producto": "Mouse Logitech",
  "local": "Central",
  "cantidad": 10
}
[CRUD_END]

✅ Stock actualizado correctamente. Ahora hay 25 unidades de Mouse Logitech en Central.
```

**Ejemplo de respuesta sin CRUD (solo consulta)**:
```
Actualmente tienes 15 unidades de Mouse Logitech en Central y 8 unidades en Ibáñez.
```

---

## ✅ Criterios de Éxito

Una operación CRUD es exitosa si:

1. ✅ El asistente entiende la intención del usuario
2. ✅ Extrae correctamente los parámetros (producto, local, cantidad, etc.)
3. ✅ Ejecuta la operación en el backend
4. ✅ Retorna confirmación al usuario
5. ✅ La UI se actualiza en tiempo real
6. ✅ No hay errores en consola

---

## 🧪 Pruebas Requeridas

Cada capacidad CRUD debe probarse:

- [ ] Crear producto funciona
- [ ] Editar producto funciona
- [ ] Eliminar producto funciona
- [ ] Agregar stock funciona
- [ ] Reducir stock funciona
- [ ] Las consultas funcionan
- [ ] Los eventos se disparan correctamente
- [ ] La UI se actualiza en tiempo real
- [ ] Los errores se manejan correctamente

---

## 🚨 Problemas Conocidos

1. **Segunda pregunta falla**: El backend puede estar acumulando historial. Ver `REPORTE_SEGUNDA_PREGUNTA_FALLA.md`.

2. **CORS en carga inicial**: El asistente intenta cargar datos al montarse, causando errores CORS. Solución: carga lazy solo cuando se necesita.

3. **Contexto no se envía**: Temporalmente deshabilitado para diagnosticar errores 500.

---

## 📝 Notas para el Equipo Backend

- El frontend espera que el backend retorne respuestas con bloques `[CRUD_START]...{JSON}...[CRUD_END]` cuando se debe ejecutar una operación.
- El formato JSON dentro del bloque debe incluir `accion` y los datos necesarios.
- Las acciones soportadas son: `crear_producto`, `editar_producto`, `eliminar_producto`, `agregar_stock`, `reducir_stock`.
- El frontend se encarga de ejecutar las operaciones CRUD, el backend solo debe retornar la instrucción en el formato correcto.

---

**Este documento define claramente qué debe funcionar y cómo se espera que funcione el Asistente IA con CRUD.** 🎯
