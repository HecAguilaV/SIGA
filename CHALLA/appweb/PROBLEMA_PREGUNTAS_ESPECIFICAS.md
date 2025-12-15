# 🔍 Problema: Algunas Preguntas Funcionan, Otras Dan Error 500

## 📋 Situación Reportada

- ✅ **"Hola"** → Funciona correctamente, responde con reporte del inventario
- ❌ **"Qué puedes hacer?"** → Error 500
- ❌ **"Hola, qué puedes hacer?"** → Error 500

**Conclusión**: El backend funciona, pero hay algo específico que causa error en ciertas preguntas.

---

## 🔍 Posibles Causas

### Causa 1: Contexto Agregado Causa Problema

**Código**: `AsistenteContextual.svelte` (línea 236)

```javascript
body: JSON.stringify({
  mensaje: contextoActual + contenido  // ⚠️ Se agrega contexto
})
```

**Contexto agregado**:
- `"El usuario está viendo el inventario. "` (si está en `/`)
- `"El usuario está viendo análisis de ventas. "` (si está en `/analisis`)

**Problema posible**: 
- El contexto + la pregunta puede crear un prompt muy largo
- O el contexto puede tener caracteres especiales que causan problemas
- O el backend no maneja bien el contexto

**Solución**: Probar sin contexto para ver si es la causa.

---

### Causa 2: Longitud del Prompt

**Problema posible**: 
- "Hola" es corto → funciona
- "Qué puedes hacer?" con contexto puede ser más largo → falla

**Solución**: Verificar si hay límite de longitud en el backend.

---

### Causa 3: Caracteres Especiales

**Problema posible**: 
- "Hola" no tiene caracteres especiales → funciona
- "Qué puedes hacer?" tiene "?" y acentos → puede causar problemas

**Solución**: Verificar encoding y caracteres especiales.

---

### Causa 4: El Backend Maneja Diferente Según el Contenido

**Problema posible**: 
- El backend puede tener lógica diferente según el contenido
- "Hola" puede activar un flujo simple
- "Qué puedes hacer?" puede activar un flujo más complejo que falla

**Solución**: Revisar logs del backend para ver qué flujo se activa.

---

## 🛠️ Solución Temporal: Probar Sin Contexto

Vamos a modificar temporalmente el código para NO agregar contexto y ver si eso soluciona el problema:

```javascript
// ANTES (con contexto)
body: JSON.stringify({
  mensaje: contextoActual + contenido
})

// DESPUÉS (sin contexto, temporalmente)
body: JSON.stringify({
  mensaje: contenido  // Sin contexto
})
```

Esto nos dirá si el problema es el contexto o la pregunta en sí.

---

## 🔍 Debugging

### Paso 1: Ver Qué Se Envía Exactamente

Revisar logs del servidor cuando envías "Hola" vs "Qué puedes hacer?":

**Para "Hola"**:
```
💬 Mensaje: El usuario está viendo el inventario. Hola
📦 Request body: {
  "message": "El usuario está viendo el inventario. Hola"
}
```

**Para "Qué puedes hacer?"**:
```
💬 Mensaje: El usuario está viendo el inventario. que puedes hacer?
📦 Request body: {
  "message": "El usuario está viendo el inventario. que puedes hacer?"
}
```

### Paso 2: Comparar

- ¿La longitud es muy diferente?
- ¿Hay caracteres especiales?
- ¿El contexto causa problemas?

---

## 📋 Checklist de Verificación

- [ ] Probar sin contexto (modificar código temporalmente)
- [ ] Comparar logs de "Hola" vs "Qué puedes hacer?"
- [ ] Verificar longitud del mensaje
- [ ] Verificar caracteres especiales
- [ ] Revisar logs del backend en Railway

---

## 🎯 Próximos Pasos

1. **Modificar código temporalmente** para NO agregar contexto
2. **Probar ambas preguntas** sin contexto
3. **Ver si el problema persiste**
4. **Si funciona sin contexto**: El problema es el contexto
5. **Si sigue fallando**: El problema es la pregunta específica o el backend

---

**Vamos a probar sin contexto para aislar el problema.** 🔍
