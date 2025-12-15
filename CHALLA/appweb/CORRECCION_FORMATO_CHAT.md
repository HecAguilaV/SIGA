# ✅ Corrección: Formato del Request al Backend

## 🔴 Error Encontrado

```
JSON parse error: Instantiation of [simple type, class com.siga.backend.controller.ChatRequest] 
value failed for JSON property message due to missing (therefore NULL) value for creator parameter messa...
```

**Causa**: El backend espera el campo `message` (en inglés), pero estábamos enviando `mensaje` (en español).

---

## ✅ Solución Implementada

### Cambio en `/api/chat/+server.js`

**Antes**:
```javascript
body: JSON.stringify({
  mensaje: mensaje,  // ❌ Backend no reconoce "mensaje"
  contexto: body?.contexto || null
})
```

**Después**:
```javascript
body: JSON.stringify({
  message: mensaje  // ✅ Backend espera "message" en inglés
})
```

---

## 📋 Formato Correcto del Request

### Request al Backend

**Endpoint**: `POST /api/saas/chat`

**Headers**:
```
Content-Type: application/json
Authorization: Bearer {token_jwt}
```

**Body**:
```json
{
  "message": "¿Qué productos tengo?"
}
```

**Nota**: El backend espera `message` (singular, en inglés), no `mensaje`.

---

## 🔍 Verificación

El error debería estar resuelto ahora. El frontend envía:

```json
{
  "message": "texto del usuario"
}
```

Que es exactamente lo que el backend espera en su clase `ChatRequest`.

---

**El formato del request ahora coincide con lo que el backend espera.** ✅
