# 🔄 Asistente IA - Llamar al Backend

## ✅ Cambio Implementado

**Problema**: El frontend estaba llamando directamente a Gemini, exponiendo la necesidad de tener la API key en el frontend.

**Solución**: El frontend ahora llama al backend, que es quien tiene la API key de Gemini.

---

## 🔄 Flujo Correcto

### Antes (Incorrecto):
```
Frontend → SvelteKit endpoint → Gemini API (con API key en frontend) ❌
```

### Ahora (Correcto):
```
Frontend → SvelteKit endpoint → Backend → Gemini API (API key en backend) ✅
```

---

## 📋 Cambios Realizados

### 1. Endpoint `/api/chat` Modificado

**Archivo**: `src/routes/api/chat/+server.js`

**Cambios**:
- ❌ Eliminado: Llamada directa a Gemini
- ❌ Eliminado: Verificación de `GEMINI_API_KEY`
- ❌ Eliminado: Construcción de prompt en el frontend
- ✅ Agregado: Llamada al backend en `/api/saas/chat`
- ✅ Agregado: Pasar token de autenticación al backend

**Código**:
```javascript
// Llamar al backend que tiene la API key de Gemini
const respuestaBackend = await fetch(API_ENDPOINTS.CHAT, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${accessToken}`
  },
  body: JSON.stringify({
    mensaje: mensaje,
    contexto: body?.contexto || null
  })
});
```

### 2. Config Actualizado

**Archivo**: `src/lib/config.js`

**Agregado**:
```javascript
// Asistente IA (debe estar en el backend)
CHAT: `${API_BASE_URL}/api/saas/chat`,
```

---

## ⚠️ Pendiente en el Backend

El backend necesita implementar el endpoint:

**Endpoint**: `POST /api/saas/chat`

**Request**:
```json
{
  "mensaje": "¿Qué productos tengo?",
  "contexto": "El usuario está viendo el inventario" // opcional
}
```

**Headers**:
```
Authorization: Bearer {token_jwt}
Content-Type: application/json
```

**Response**:
```json
{
  "respuesta": "Tienes 5 productos: Pan, Leche, Galletas...",
  "crud": [] // opcional: operaciones CRUD a ejecutar
}
```

**Funcionalidad del Backend**:
1. Recibir mensaje del frontend
2. Cargar datos del usuario (productos, locales, stock, etc.)
3. Construir prompt con los datos
4. Llamar a Gemini con su API key
5. Procesar respuesta de Gemini
6. Retornar respuesta al frontend

---

## 🔍 Manejo de Errores

### Si el Backend No Tiene el Endpoint (404):

El frontend mostrará:
```
⚠️ El endpoint de chat aún no está disponible en el backend. 
Por favor, contacta al equipo backend para implementarlo.
```

### Si Hay Error del Backend:

El frontend mostrará el mensaje de error específico del backend.

---

## ✅ Ventajas de Este Enfoque

1. **Seguridad**: API key de Gemini solo en el backend
2. **Mantenibilidad**: Lógica de IA centralizada en el backend
3. **Escalabilidad**: Fácil agregar más funcionalidades de IA
4. **Consistencia**: Mismo patrón que otros endpoints

---

## 📝 Notas para el Equipo Backend

El frontend está listo para usar el endpoint `/api/saas/chat`. 

**Requisitos**:
- Endpoint: `POST /api/saas/chat`
- Autenticación: Requiere token JWT válido
- Request body: `{ mensaje: string, contexto?: string }`
- Response: `{ respuesta: string, crud?: array }`

**El backend debe**:
1. Validar token JWT
2. Cargar datos del usuario autenticado
3. Construir prompt con datos
4. Llamar a Gemini con API key
5. Retornar respuesta formateada

---

**El frontend ahora llama correctamente al backend. Falta que el backend implemente el endpoint.** ✅
