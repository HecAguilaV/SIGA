# 🔑 Solución: API Key de Gemini Inválida

## 🔴 Error

```
"API key not valid. Please pass a valid API key."
"reason": "API_KEY_INVALID"
```

**Este es un error de nuestro lado (frontend), NO del backend.**

---

## ✅ Solución

### Paso 1: Verificar/Crear `.env.local`

1. En la raíz del proyecto, verifica si existe `.env.local`
2. Si no existe, créalo
3. Si existe, ábrelo para editarlo

### Paso 2: Agregar GEMINI_API_KEY

Agrega esta línea al archivo `.env.local`:

```env
GEMINI_API_KEY=tu_api_key_aqui
```

**Importante**: Reemplaza `tu_api_key_aqui` con tu API key real de Google Gemini.

### Paso 3: Obtener API Key de Gemini

Si no tienes una API key:

1. Ve a: https://aistudio.google.com/app/apikey
2. O: https://makersuite.google.com/app/apikey
3. Inicia sesión con tu cuenta de Google
4. Haz clic en "Create API Key" o "Get API Key"
5. Copia la key generada
6. Pégala en `.env.local`

### Paso 4: Reiniciar el Servidor

**MUY IMPORTANTE**: Después de agregar/modificar `.env.local`, debes reiniciar el servidor:

```bash
# Detén el servidor (Ctrl+C en la terminal)
# Luego reinicia:
pnpm run dev
```

**Nota**: Las variables de entorno solo se cargan al iniciar el servidor. Si no reinicias, los cambios no tendrán efecto.

---

## 🔍 Verificación

### Verificar que la Key se Cargó

1. Reinicia el servidor
2. Intenta usar el asistente
3. Si el error persiste, verifica:
   - ¿El archivo se llama exactamente `.env.local`? (con el punto al inicio)
   - ¿Está en la raíz del proyecto? (mismo nivel que `package.json`)
   - ¿La key está en una sola línea sin espacios extra?
   - ¿Reiniciaste el servidor después de agregar la key?

### Verificar que la Key es Válida

Si agregaste la key pero sigue dando error:

1. Verifica que la key no tenga espacios al inicio o final
2. Verifica que la key sea completa (no cortada)
3. Prueba crear una nueva key en Google AI Studio
4. Reemplaza la key antigua con la nueva

---

## 📝 Formato Correcto del `.env.local`

```env
# API Key de Google Gemini para el asistente IA
GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

# Opcional: URL del backend (si no está en .env.local.example)
VITE_API_BASE_URL=https://siga-backend-production.up.railway.app
```

**Importante**:
- No uses comillas alrededor de la key
- No dejes espacios antes o después del `=`
- Una key por línea

---

## ⚠️ Errores Comunes

### Error: "API key not valid"

**Causas posibles**:
- ❌ La key no está en `.env.local`
- ❌ El servidor no se reinició después de agregar la key
- ❌ La key tiene espacios o caracteres extra
- ❌ La key es inválida o fue revocada

**Solución**: Sigue los pasos arriba, asegurándote de reiniciar el servidor.

---

### Error: "GEMINI_API_KEY no está configurada"

**Causa**: El archivo `.env.local` no existe o la variable no está definida.

**Solución**: Crea el archivo y agrega la variable como se indica arriba.

---

## 🎯 Resumen

1. **Error**: API key inválida → **Nuestro error (frontend)**
2. **Causa**: Falta `GEMINI_API_KEY` en `.env.local` o es inválida
3. **Solución**: Agregar key válida y reiniciar servidor
4. **NO es error del backend**: El backend no tiene nada que ver con Gemini

---

**Una vez que agregues la key válida y reinicies el servidor, el asistente debería funcionar.** ✅
