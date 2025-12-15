# 🔴 Problema: Segunda Pregunta Siempre Falla

## 📋 Situación Reportada

- ✅ **Primera pregunta** (ej: "Hola") → Funciona correctamente
- ❌ **Segunda pregunta** (cualquiera) → Error 500 siempre

**Patrón**: Siempre la segunda pregunta falla, sin importar cuál sea.

---

## 🔍 Posibles Causas

### Causa 1: Estado Acumulado en el Backend

**Problema posible**: 
- El backend puede estar acumulando contexto/historial
- La segunda pregunta hace que el prompt sea muy largo
- O hay un problema de memoria/recursos

**Síntoma**: Primera pregunta funciona, segunda falla siempre.

---

### Causa 2: Rate Limiting

**Problema posible**: 
- El backend o Gemini API tiene rate limiting
- La primera pregunta funciona
- La segunda pregunta excede el límite

**Síntoma**: Siempre falla en la segunda, sin importar el tiempo entre preguntas.

---

### Causa 3: Token/Sesión

**Problema posible**: 
- El token se invalida después de la primera pregunta
- O hay un problema de sesión

**Síntoma**: Primera funciona, segunda falla con 401 o 500.

---

### Causa 4: Memoria/Recursos del Backend

**Problema posible**: 
- El backend consume mucha memoria en la primera pregunta
- La segunda pregunta no tiene recursos suficientes

**Síntoma**: Primera funciona, segunda falla con 500.

---

### Causa 5: Historial de Conversación

**Problema posible**: 
- El backend está intentando mantener historial
- La segunda pregunta incluye el historial y es muy largo
- O hay un bug al procesar el historial

**Síntoma**: Primera funciona (sin historial), segunda falla (con historial).

---

## 🛠️ Soluciones a Probar

### Solución 1: Verificar si el Backend Envía Historial

**Pregunta al equipo backend**:
- ¿El backend está enviando el historial de conversación a Gemini?
- ¿Hay un límite de tokens/palabras?
- ¿El historial se acumula entre mensajes?

---

### Solución 2: Probar con Diferentes Intervalos

**Prueba**:
1. Primera pregunta: "Hola"
2. Esperar 10 segundos
3. Segunda pregunta: "Qué puedes hacer?"

**Si funciona con intervalo**: Puede ser rate limiting o recursos.

**Si sigue fallando**: Es un problema de estado/historial.

---

### Solución 3: Verificar Logs del Backend

**Revisar en Railway**:
- ¿Qué pasa en la primera pregunta? (debería funcionar)
- ¿Qué pasa en la segunda pregunta? (debería fallar)
- ¿Hay diferencia en el tamaño del prompt?
- ¿Hay diferencia en los recursos usados?

---

### Solución 4: Probar Sin Historial (Si el Backend lo Envía)

**Si el backend envía historial**, pedir que:
- No envíe historial en las primeras pruebas
- O limite el historial a X mensajes
- O limpie el historial entre preguntas

---

## 📋 Checklist de Diagnóstico

- [ ] Probar con intervalo de 10+ segundos entre preguntas
- [ ] Revisar logs del backend en Railway para primera vs segunda pregunta
- [ ] Verificar si el backend envía historial de conversación
- [ ] Verificar rate limiting en Gemini API
- [ ] Verificar recursos/memoria del backend
- [ ] Preguntar al equipo backend sobre historial de conversación

---

## 🎯 Próximos Pasos

1. **Probar con intervalo** entre preguntas
2. **Revisar logs de Railway** para ver diferencia entre primera y segunda
3. **Preguntar al backend** si envían historial de conversación
4. **Verificar** si hay límites de tokens/palabras

---

**Este es un problema del backend relacionado con el manejo de múltiples mensajes.** 🔍

