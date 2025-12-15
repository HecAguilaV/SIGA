# 📦 Configuración pnpm - SIGA App Web

## ✅ Proyecto Configurado para pnpm

El proyecto ha sido configurado para usar **pnpm** en lugar de npm por temas de seguridad.

---

## 🔒 Ventajas de pnpm

1. **Seguridad mejorada**:
   - Store centralizado de paquetes
   - Evita duplicación de dependencias
   - Mejor gestión de vulnerabilidades

2. **Rendimiento**:
   - Instalación más rápida
   - Menor uso de disco
   - Enlaces simbólicos eficientes

3. **Consistencia**:
   - Lock file más estricto
   - Mejor resolución de dependencias

---

## 📋 Comandos pnpm

### Instalación
```bash
pnpm install
# o simplemente
pnpm i
```

### Desarrollo
```bash
pnpm run dev
```

### Build
```bash
pnpm run build
```

### Otros comandos
```bash
pnpm run test        # Ejecutar tests
pnpm run check       # Verificar tipos
pnpm run preview     # Preview de build
```

---

## 📁 Archivos

- ✅ `pnpm-lock.yaml` - Lock file de pnpm (versionado)
- ❌ `package-lock.json` - Eliminado (no necesario con pnpm)

---

## ⚠️ Nota para el Equipo

**Usar siempre pnpm**, no npm:
- ✅ `pnpm install`
- ✅ `pnpm run dev`
- ❌ `npm install` (no usar)
- ❌ `npm run dev` (no usar)

---

## 🔧 Si Alguien Usa npm por Error

Si alguien ejecuta `npm install`:
1. Se creará `package-lock.json`
2. Puede causar conflictos
3. **Solución**: Eliminar `package-lock.json` y usar `pnpm install`

---

**Proyecto configurado para pnpm ✅**
