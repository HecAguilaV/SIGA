# Problema: ADMINISTRADOR sin permisos asignados

## Descripción del problema

Un usuario con rol `ADMINISTRADOR` no puede realizar acciones básicas porque el backend está rechazando las peticiones con `403 Forbidden` debido a que no tiene permisos asignados explícitamente.

## Errores observados

1. **Cargar permisos del usuario:**
   ```
   GET /api/saas/usuarios/18/permisos → 403
   Error: "No tienes permiso para ver permisos"
   ```

2. **Crear un local:**
   ```
   POST /api/saas/locales → 403
   Error: "No tienes permiso para crear locales"
   ```

3. **Acceder a recursos:**
   ```
   GET /api/saas/categorias → 403
   GET /api/saas/productos → 403
   GET /api/saas/stock → 403
   ```

## Usuario afectado

- **Email:** `admin.test@siga.com`
- **Rol:** `ADMINISTRADOR`
- **ID:** `18`

## Comportamiento esperado

Según la documentación del sistema de permisos (`RESUMEN_EJECUTIVO_SISTEMA_PERMISOS.md`), el rol `ADMINISTRADOR` debería tener **todos los permisos automáticamente**, sin necesidad de asignarlos explícitamente.

## Comportamiento actual

El backend está validando permisos de forma granular y rechazando acciones incluso para usuarios `ADMINISTRADOR` que no tienen permisos asignados explícitamente en la base de datos.

## Solución esperada del backend

1. **Opción 1 (Recomendada):** El backend debería reconocer que un usuario con rol `ADMINISTRADOR` tiene todos los permisos automáticamente, sin necesidad de validar permisos explícitos.

2. **Opción 2:** Si el backend requiere permisos explícitos, entonces debería haber un proceso de inicialización que asigne todos los permisos disponibles a los usuarios `ADMINISTRADOR` al momento de crearlos o al activar el sistema de permisos.

## Workaround temporal en frontend

El frontend ya está manejando este caso:
- Si un `ADMINISTRADOR` no puede cargar permisos, el frontend asume que tiene todos los permisos.
- Se muestra un mensaje claro cuando un `ADMINISTRADOR` intenta realizar una acción y recibe 403.

Sin embargo, esto no resuelve el problema real: el backend sigue rechazando las peticiones.

## Acción requerida

**El backend debe corregir la validación de permisos para que los usuarios `ADMINISTRADOR` puedan realizar todas las acciones sin necesidad de permisos explícitos.**

## Fecha del reporte

2025-01-XX
