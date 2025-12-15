# 🔍 Debug del Asistente - Verificar Logs

## 📋 Pasos para Diagnosticar

### 1. Enviar un Mensaje y Ver Logs

1. **Abre el asistente** en el navegador
2. **Envía un mensaje** (ej: "Hola")
3. **Inmediatamente revisa la terminal** donde corre `pnpm run dev`
4. **Deberías ver logs como**:

```
📡 Enviando mensaje al backend...
🔗 URL: https://siga-backend-production.up.railway.app/api/saas/chat
💬 Mensaje: Hola
📦 Request body: {
  "message": "Hola"
}
📊 Response status: 200
📊 Response ok: true
✅ Respuesta del backend recibida
📦 Datos del backend: {...}
💬 Respuesta extraída: ...
```

### 2. Si NO Ves los Logs

**Posibles causas**:
- Los `console.log` no se están ejecutando
- Hay un error antes de llegar a esos logs
- El endpoint no se está llamando

**Solución**: Verificar en la consola del navegador (DevTools → Console) si hay errores.

### 3. Si Ves los Logs pero la Respuesta es "No pude procesar tu mensaje ahora"

**Revisa la línea `📦 Datos del backend:`**:
- ¿Qué estructura tiene el JSON?
- ¿Tiene un campo `respuesta`, `message`, `response`, `text`, o `content`?
- ¿O es un string directo?

**El código intenta extraer la respuesta de estos campos**:
```javascript
const respuesta = datosBackend.respuesta 
  || datosBackend.message 
  || datosBackend.response
  || datosBackend.text
  || datosBackend.content
  || (typeof datosBackend === 'string' ? datosBackend : null)
  || 'No pude procesar tu mensaje ahora.';
```

Si ninguno de estos campos existe, mostrará el mensaje predeterminado.

---

## 🎯 Qué Hacer

1. **Envía un mensaje** en el asistente
2. **Copia TODO lo que aparece en la terminal** después de enviar el mensaje
3. **Comparte los logs** para ver qué está retornando el backend

---

## ⚠️ Nota Importante

Si el backend está retornando respuestas predeterminadas, eso es responsabilidad del **backend**, no del frontend. El frontend solo muestra lo que el backend retorna.

El frontend NO tiene respuestas predeterminadas (excepto el fallback si no puede parsear la respuesta).
