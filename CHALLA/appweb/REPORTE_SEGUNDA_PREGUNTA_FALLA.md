# 🔴 Reporte: Segunda Pregunta Siempre Falla

## 📋 Problema Reportado

**Patrón consistente**:
- ✅ **Primera pregunta** (cualquiera, ej: "Hola") → **Funciona correctamente**
- ❌ **Segunda pregunta** (cualquiera) → **Error 500 siempre**

**No importa**:
- Qué pregunta sea la segunda
- Cuánto tiempo pase entre preguntas
- Si es la misma pregunta o diferente

**Siempre falla en la segunda pregunta.**

---

## 🔍 Análisis del Frontend

### Lo que el Frontend Envía

**Cada mensaje se envía de forma independiente**:
- No enviamos historial de conversación
- Solo enviamos el mensaje actual
- Cada request es independiente

**Código**:
```javascript
body: JSON.stringify({
  mensaje: contenido  // Solo el mensaje actual, sin historial
})
```

**Conclusión**: El frontend NO está acumulando nada entre mensajes.

---

## 🔍 Posibles Causas en el Backend

### Causa 1: Historial de Conversación Acumulado ⚠️ MÁS PROBABLE

**Problema**: El backend puede estar:
- Manteniendo historial de conversación en memoria
- Enviando todo el historial a Gemini en la segunda pregunta
- El prompt se vuelve muy largo y falla

**Síntoma**: Primera pregunta funciona (sin historial), segunda falla (con historial).

**Verificación en logs del backend**:
- ¿El backend mantiene historial de conversación?
- ¿El prompt de la segunda pregunta es mucho más largo?
- ¿Hay un límite de tokens que se excede?

---

### Causa 2: Estado/Sesión Corrupto

**Problema**: 
- Algo en el estado del backend se corrompe después de la primera pregunta
- La segunda pregunta falla porque el estado está mal

**Síntoma**: Primera funciona, segunda falla siempre.

---

### Causa 3: Rate Limiting

**Problema**: 
- Gemini API o el backend tiene rate limiting
- La primera pregunta funciona
- La segunda pregunta excede el límite

**Síntoma**: Siempre falla en la segunda, sin importar el tiempo.

---

### Causa 4: Recursos/Memoria

**Problema**: 
- El backend consume mucha memoria en la primera pregunta
- La segunda pregunta no tiene recursos suficientes

**Síntoma**: Primera funciona, segunda falla con 500.

---

## 📊 Logs del Frontend

Cuando se envía la segunda pregunta, el frontend muestra:

```
📡 Enviando mensaje al backend...
🔗 URL: https://siga-backend-production.up.railway.app/api/saas/chat
💬 Mensaje: [mensaje de la segunda pregunta]
📦 Request body: {
  "message": "[mensaje]"
}
📊 Response status: 500
📊 Response ok: false
❌ Error del backend: Error al procesar la solicitud con el asistente IA...
```

---

## 🔍 Qué Revisar en el Backend

### 1. Historial de Conversación

**Preguntas clave**:
- ¿El backend mantiene historial de conversación?
- ¿En qué se basa? (¿session ID, user ID, token?)
- ¿Se limpia entre requests o se acumula?

**Si mantiene historial**:
- ¿Cuántos mensajes acumula?
- ¿Hay un límite?
- ¿El historial se envía a Gemini?

---

### 2. Logs de Railway

**Revisar logs cuando**:
- Se envía la primera pregunta (debería funcionar)
- Se envía la segunda pregunta (debería fallar)

**Comparar**:
- ¿El prompt es más largo en la segunda?
- ¿Hay diferencia en los recursos usados?
- ¿Hay diferencia en el tiempo de procesamiento?
- ¿Qué error específico ocurre en la segunda?

---

### 3. Estado/Sesión

**Verificar**:
- ¿Hay estado que se mantiene entre requests?
- ¿Se limpia correctamente?
- ¿Hay algo que se corrompe después de la primera pregunta?

---

## 🛠️ Soluciones Sugeridas

### Solución 1: Limitar o Deshabilitar Historial Temporalmente

**Si el backend mantiene historial**:
- Deshabilitar historial temporalmente para probar
- O limitar a 1-2 mensajes
- O limpiar historial entre requests

---

### Solución 2: Verificar Límites de Tokens

**Si el prompt es muy largo**:
- Verificar límite de tokens de Gemini
- Truncar historial si es necesario
- O no enviar historial completo

---

### Solución 3: Limpiar Estado Entre Requests

**Si hay estado acumulado**:
- Limpiar estado después de cada request
- O usar estado stateless
- O resetear estado si hay error

---

## 📋 Checklist para el Equipo Backend

- [ ] Verificar si el backend mantiene historial de conversación
- [ ] Comparar logs de primera vs segunda pregunta
- [ ] Verificar longitud del prompt en primera vs segunda
- [ ] Verificar si hay límite de tokens que se excede
- [ ] Verificar si hay estado que se acumula
- [ ] Verificar recursos/memoria en primera vs segunda
- [ ] Probar deshabilitando historial temporalmente

---

## 🎯 Conclusión

**Este es un problema del backend relacionado con el manejo de múltiples mensajes.**

**El frontend envía cada mensaje de forma independiente, sin acumular nada.**

**El problema está en cómo el backend maneja el estado/historial entre requests.**

---

**Necesitamos que el equipo backend revise cómo manejan el historial de conversación y el estado entre requests.** 🔍

