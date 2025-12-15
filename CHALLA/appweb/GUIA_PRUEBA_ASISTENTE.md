# 🤖 Guía Rápida - Probar el Asistente IA

## ✅ Checklist Antes de Probar

### 1. Verificar que estás autenticado

- [ ] Ve a `/login` y haz login
- [ ] Verifica en DevTools → Application → LocalStorage que existe `accessToken`
- [ ] Deberías estar en la página principal (`/`)

### 2. Verificar variable de entorno GEMINI_API_KEY

**Importante**: El asistente necesita la API key de Google Gemini.

**Pasos**:
1. Crea o edita el archivo `.env.local` en la raíz del proyecto
2. Agrega:
   ```env
   GEMINI_API_KEY=tu_api_key_aqui
   ```
3. Reinicia el servidor (`pnpm run dev`)

**Obtener API Key**:
- Ve a: https://makersuite.google.com/app/apikey
- O: https://aistudio.google.com/app/apikey
- Crea una nueva API key
- Cópiala al `.env.local`

---

## 🚀 Pasos para Probar

### Paso 1: Abrir el Asistente

1. Ve a la página principal: `http://localhost:5173`
2. Busca el botón flotante del asistente (generalmente en la esquina inferior derecha)
3. Haz clic para abrir el panel del asistente

### Paso 2: Hacer una Pregunta Simple

**Pruebas sugeridas**:

1. **Consulta simple**:
   ```
   ¿Qué productos tengo?
   ```

2. **Consulta sobre locales**:
   ```
   ¿Cuántos locales tengo?
   ```

3. **Si tienes productos, prueba crear stock**:
   ```
   Agrega 10 unidades de [nombre_producto] al local [nombre_local]
   ```

4. **Si no tienes productos, prueba crear uno**:
   ```
   Crea un producto llamado "Pan" en la categoría "Panadería"
   ```

---

## ✅ Qué Deberías Ver

### Si Funciona Correctamente:

1. **El asistente responde** con un mensaje amigable
2. **Si ejecuta una operación CRUD**, verás:
   - Mensaje de confirmación
   - Los datos se actualizan en la página principal

### Ejemplo de Respuesta Exitosa:

```
Usuario: ¿Qué productos tengo?
SIGA: Tienes 3 productos: Pan, Leche, Galletas. ¿Quieres ver el stock de alguno?
```

---

## ❌ Problemas Comunes

### Error: "Debes iniciar sesión para usar el asistente"

**Causa**: No hay token en localStorage

**Solución**:
1. Ve a `/login`
2. Haz login nuevamente
3. Verifica que `accessToken` esté en localStorage

---

### Error: "La configuración del asistente no está completa"

**Causa**: Falta `GEMINI_API_KEY` en `.env.local`

**Solución**:
1. Crea/edita `.env.local`
2. Agrega `GEMINI_API_KEY=tu_key`
3. Reinicia el servidor

---

### Error: "No fue posible conectar con SIGA" (500)

**Causa**: Error en el servidor al cargar datos

**Solución**:
1. Abre la consola del servidor (terminal)
2. Revisa los logs de error
3. Verifica que:
   - Estés autenticado
   - El backend esté accesible
   - Tengas permisos (suscripción activa)

---

### El asistente no ve mis datos

**Causa**: No hay datos en el backend o error al cargarlos

**Solución**:
1. Verifica que tengas:
   - Al menos un local creado
   - Productos (opcional, pero recomendado)
   - Categorías (si quieres crear productos)

2. Si no tienes datos:
   - Crea un local desde la página principal
   - Crea algunos productos
   - Luego prueba el asistente

---

## 🔍 Debugging

### Ver Logs del Servidor

En la terminal donde corre `pnpm run dev`, deberías ver:

```
🔵 API Request: https://siga-backend-production.up.railway.app/api/saas/productos GET
🟢 API Response: 200
```

Si ves errores 500 o 401, revisa:
- Token válido
- Suscripción activa
- Backend accesible

### Ver Logs del Cliente

En DevTools → Console, deberías ver:
- Mensajes del asistente
- Errores si los hay
- Confirmaciones de operaciones CRUD

---

## 📝 Notas

1. **Primera vez**: Si no tienes datos, el asistente funcionará pero dirá que no hay productos/locales
2. **Operaciones CRUD**: El asistente puede crear/editar productos y actualizar stock
3. **Búsqueda**: El asistente busca productos y locales por nombre (fuzzy matching)

---

## ✅ Checklist de Prueba

- [ ] Estoy autenticado (token en localStorage)
- [ ] `GEMINI_API_KEY` configurada en `.env.local`
- [ ] Servidor reiniciado después de agregar la key
- [ ] Asistente se abre correctamente
- [ ] Puedo enviar un mensaje
- [ ] El asistente responde
- [ ] (Opcional) Puedo crear/editar productos vía asistente
- [ ] (Opcional) Puedo actualizar stock vía asistente

---

**¡Listo para probar!** 🚀

Si encuentras algún error, revisa los logs del servidor y la consola del navegador.
