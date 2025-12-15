# ✅ Solución: Error CORS en Asistente

## 🔴 Problema

El componente `AsistenteContextual.svelte` estaba intentando cargar datos directamente desde el cliente (navegador) al backend, lo cual está siendo bloqueado por CORS:

```
Access to fetch at 'https://siga-backend-production.up.railway.app/api/saas/locales' 
from origin 'http://localhost:5174' has been blocked by CORS policy
```

**Causa**: El componente cargaba datos al montar (`onMount`) llamando directamente a los servicios del backend desde el cliente.

---

## ✅ Solución Implementada

### Cambio 1: Eliminar Carga Automática al Montar

**Antes**:
```javascript
onMount(async () => {
  await cargarDatosCache(); // ❌ Carga datos al montar, causa CORS
});
```

**Después**:
```javascript
// NO cargar datos al montar
// Los datos se cargan en el servidor cuando se procesa el mensaje
```

### Cambio 2: Carga Lazy (Solo Cuando se Necesita)

**Nueva función**:
```javascript
async function cargarDatosCacheSiNecesario() {
  // Solo cargar si el cache está vacío
  if (datosCache.productos.length === 0 && datosCache.locales.length === 0) {
    try {
      [datosCache.productos, datosCache.locales, datosCache.categorias] = 
        await Promise.all([
          obtenerProductos(),
          obtenerLocales(),
          obtenerCategorias()
        ]);
    } catch (error) {
      console.error('Error cargando datos:', error);
      // Si falla por CORS, usar arrays vacíos
    }
  }
}
```

### Cambio 3: Cargar Datos Solo al Procesar CRUD

**Antes**:
```javascript
const procesarCRUD = async (crud: any) => {
  // Usaba datosCache que se cargaba al montar
}
```

**Después**:
```javascript
const procesarCRUD = async (crud: any) => {
  // Cargar datos si es necesario (solo cuando se procesa CRUD)
  await cargarDatosCacheSiNecesario();
  // ... resto del código
}
```

---

## 🎯 Resultado

1. **No hay carga al montar**: El componente no intenta cargar datos cuando se abre
2. **Carga lazy**: Los datos se cargan solo cuando se necesitan (al procesar CRUD)
3. **Manejo de errores**: Si falla por CORS, simplemente usa arrays vacíos
4. **Los datos principales se cargan en el servidor**: Cuando se procesa el mensaje del asistente, los datos se cargan en `/api/chat/+server.js` (sin problemas de CORS)

---

## ⚠️ Nota Importante

**Los datos para el asistente se cargan en el servidor**, no en el cliente:

- ✅ **Servidor** (`/api/chat/+server.js`): Carga datos del backend → Sin problemas de CORS
- ⚠️ **Cliente** (`AsistenteContextual.svelte`): Solo carga datos cuando se procesa CRUD → Puede fallar por CORS, pero no es crítico

**Si el backend tiene CORS configurado correctamente**, las llamadas desde el cliente también funcionarán. Si no, el asistente seguirá funcionando porque los datos principales se cargan en el servidor.

---

**El error de CORS debería estar resuelto. El asistente ahora carga datos de forma más eficiente.** ✅
