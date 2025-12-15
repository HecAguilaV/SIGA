# Instrucciones para Equipo WebApp - Sistema CRUD del Asistente

**Fecha**: Diciembre 2024  
**Para**: Equipo WebApp  
**Estado**: ✅ **Backend Desplegado - Listo para Integración**

---

## 🎯 RESUMEN

El backend ahora soporta **acciones CRUD completas** en el asistente IA. El asistente puede:
- ✅ Crear productos, locales, categorías
- ✅ Actualizar productos y stock
- ✅ Eliminar productos (con confirmación)
- ✅ Consultar y listar (ya funcionaba)

---

## 📡 CAMBIOS EN EL API

### Endpoint: `POST /api/saas/chat`

**URL**: `https://siga-backend-production.up.railway.app/api/saas/chat`

**Request** (sin cambios):
```json
{
  "message": "Crea un producto llamado Café con precio 1500"
}
```

**Response** (NUEVO formato):
```json
{
  "success": true,
  "response": "✅ Producto 'Café' creado exitosamente",
  "action": {
    "executed": true,
    "type": "CREATE_PRODUCT",
    "data": null,
    "requiresConfirmation": false
  }
}
```

### Campo `action` en la Respuesta

El campo `action` contiene información sobre la acción ejecutada:

```typescript
interface ActionInfo {
  executed: boolean;           // true si la acción se ejecutó exitosamente
  type?: string;               // Tipo de acción: "CREATE_PRODUCT", "UPDATE_STOCK", etc.
  data?: Map<string, any>;     // Datos adicionales (por ahora null)
  requiresConfirmation?: boolean; // true si requiere confirmación del usuario
}
```

**Tipos de acción posibles**:
- `CREATE_PRODUCT` - Producto creado
- `UPDATE_PRODUCT` - Producto actualizado
- `DELETE_PRODUCT` - Producto eliminado
- `UPDATE_STOCK` - Stock actualizado
- `CREATE_LOCAL` - Local creado
- `CREATE_CATEGORIA` - Categoría creada
- `null` o ausente - Es una consulta normal (no acción CRUD)

---

## 🔧 INTEGRACIÓN REQUERIDA

### 1. Manejar Respuestas con Acciones

Cuando recibas una respuesta del chat, verifica si hay una acción ejecutada:

```javascript
const response = await fetch(`${API_URL}/api/saas/chat`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({ message: userMessage })
});

const data = await response.json();

// Verificar si se ejecutó una acción
if (data.action?.executed) {
  // Acción ejecutada exitosamente
  console.log('Acción ejecutada:', data.action.type);
  
  // Actualizar UI según el tipo de acción
  switch (data.action.type) {
    case 'CREATE_PRODUCT':
    case 'UPDATE_PRODUCT':
    case 'DELETE_PRODUCT':
      // Refrescar lista de productos
      await refreshProductos();
      break;
    
    case 'UPDATE_STOCK':
      // Refrescar stock
      await refreshStock();
      break;
    
    case 'CREATE_LOCAL':
      // Refrescar lista de locales
      await refreshLocales();
      break;
    
    case 'CREATE_CATEGORIA':
      // Refrescar lista de categorías
      await refreshCategorias();
      break;
  }
  
  // Mostrar notificación de éxito
  showNotification('success', data.response);
} else if (data.action?.requiresConfirmation) {
  // Requiere confirmación del usuario
  const confirmed = await showConfirmationDialog(data.response);
  if (confirmed) {
    // Reenviar el mensaje con confirmación
    // (Por ahora, el backend retorna el mensaje de confirmación,
    // pero no maneja la confirmación explícita aún)
  }
}
```

### 2. Actualizar UI Después de Acciones

**Importante**: Después de una acción CRUD exitosa, debes actualizar la UI para reflejar los cambios:

```javascript
// Ejemplo: Refrescar productos después de crear/actualizar/eliminar
async function refreshProductos() {
  const productos = await fetch(`${API_URL}/api/saas/productos`, {
    headers: { 'Authorization': `Bearer ${token}` }
  }).then(r => r.json());
  
  // Actualizar estado de productos en tu componente
  setProductos(productos.productos);
}
```

### 3. Manejar Errores

Si `action.executed === false`, significa que hubo un error:

```javascript
if (data.action && !data.action.executed) {
  // Error en la ejecución
  showNotification('error', data.response || 'No se pudo ejecutar la acción');
}
```

---

## 📋 EJEMPLOS DE USO

### Ejemplo 1: Crear Producto

**Usuario escribe**: "Crea un producto llamado Café con precio 1500"

**Backend responde**:
```json
{
  "success": true,
  "response": "✅ Producto 'Café' creado exitosamente",
  "action": {
    "executed": true,
    "type": "CREATE_PRODUCT",
    "requiresConfirmation": false
  }
}
```

**Frontend debe**:
1. Mostrar el mensaje "✅ Producto 'Café' creado exitosamente"
2. Refrescar la lista de productos
3. Mostrar notificación de éxito

### Ejemplo 2: Actualizar Stock

**Usuario escribe**: "Agrega 50 unidades de Café al local ITR"

**Backend responde**:
```json
{
  "success": true,
  "response": "✅ Stock actualizado: Café en ITR = 50 unidades",
  "action": {
    "executed": true,
    "type": "UPDATE_STOCK",
    "requiresConfirmation": false
  }
}
```

**Frontend debe**:
1. Mostrar el mensaje de confirmación
2. Refrescar el stock
3. Mostrar notificación de éxito

### Ejemplo 3: Consulta Normal

**Usuario escribe**: "¿Qué productos tengo?"

**Backend responde**:
```json
{
  "success": true,
  "response": "Tienes 5 productos: Café, Leche, Pan...",
  "action": null
}
```

**Frontend debe**:
1. Mostrar la respuesta normalmente (sin refrescar nada)
2. No hay acción que ejecutar

### Ejemplo 4: Eliminar Producto (Requiere Confirmación)

**Usuario escribe**: "Elimina el producto Café"

**Backend responde** (primera vez):
```json
{
  "success": true,
  "response": "¿Estás seguro de que deseas eliminar este producto?",
  "action": {
    "executed": false,
    "requiresConfirmation": true
  }
}
```

**Frontend debe**:
1. Mostrar diálogo de confirmación
2. Si el usuario confirma, reenviar el mensaje (por ahora el backend no maneja confirmación explícita, pero puedes implementar lógica en frontend)

---

## ⚠️ IMPORTANTE

### 1. El Asistente NO es una Página de Prueba

- ✅ Integrar el asistente directamente en la UI principal
- ❌ NO crear una página de prueba separada
- ✅ El asistente debe estar disponible en toda la aplicación

### 2. Actualizar UI Después de Acciones

**Crítico**: Después de cada acción CRUD exitosa, debes refrescar los datos correspondientes:
- Crear/actualizar/eliminar producto → Refrescar lista de productos
- Actualizar stock → Refrescar stock
- Crear local → Refrescar lista de locales
- Crear categoría → Refrescar lista de categorías

### 3. Manejo de Permisos

El backend valida permisos automáticamente:
- Solo `ADMINISTRADOR` puede crear/actualizar/eliminar productos, locales, categorías
- `OPERADOR` puede actualizar stock
- Si el usuario no tiene permisos, recibirás un mensaje de error en `response`

### 4. Errores Comunes

**Error**: "Solo administradores pueden crear productos"
- **Causa**: El usuario no tiene rol `ADMINISTRADOR`
- **Solución**: Verificar que el usuario tenga el rol correcto

**Error**: "Producto no encontrado"
- **Causa**: El producto no existe o el nombre no coincide
- **Solución**: Verificar que el producto exista antes de actualizar/eliminar

---

## 🧪 PRUEBAS

### Pruebas Recomendadas

1. **Crear producto**:
   - Mensaje: "Crea un producto llamado Test con precio 1000"
   - Verificar que aparece en la lista de productos

2. **Actualizar stock**:
   - Mensaje: "Agrega 10 unidades de Test al local [nombre-local]"
   - Verificar que el stock se actualiza

3. **Crear local**:
   - Mensaje: "Crea un local llamado TestLocal"
   - Verificar que aparece en la lista de locales

4. **Consultar**:
   - Mensaje: "¿Qué productos tengo?"
   - Verificar que muestra información sin ejecutar acción

5. **Permisos**:
   - Con usuario OPERADOR, intentar crear producto
   - Verificar que muestra error de permisos

---

## 📞 SOPORTE

Si encuentras problemas:

1. Verifica que el endpoint sea: `https://siga-backend-production.up.railway.app/api/saas/chat`
2. Verifica que el token de autenticación sea válido
3. Revisa la consola del navegador para errores
4. Verifica los logs de Railway si el problema persiste

---

## ✅ CHECKLIST DE INTEGRACIÓN

- [ ] Actualizar tipo/interfaz de `ChatResponse` para incluir `action`
- [ ] Manejar `action.executed === true` para refrescar UI
- [ ] Manejar `action.requiresConfirmation === true` para mostrar confirmación
- [ ] Refrescar productos después de CREATE_PRODUCT, UPDATE_PRODUCT, DELETE_PRODUCT
- [ ] Refrescar stock después de UPDATE_STOCK
- [ ] Refrescar locales después de CREATE_LOCAL
- [ ] Refrescar categorías después de CREATE_CATEGORIA
- [ ] Mostrar notificaciones de éxito/error
- [ ] Integrar asistente en UI principal (no página de prueba)
- [ ] Probar todas las acciones CRUD
- [ ] Probar manejo de errores y permisos

---

**Instrucciones completas para el equipo WebApp - Sistema CRUD del Asistente**
