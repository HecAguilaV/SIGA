# A2UI Protocol — Contrato Agent ↔ Webapp

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../../en/architecture/A2UI_PROTOCOL.md)*

> **Versión:** 1.0
> **Fecha:** 2026-04-23
> **Propósito:** Definir el "idioma" entre el Agente y la Webapp

---

## 1. REQUEST — Envío desde Webapp

```json
POST /agent/chat
Content-Type: application/json

{
  "tenant_id": "emp_abc123",
  "user_id": "usr_xyz789",
  "user_rol": "OWNER",
  "plan": "PRO",
  "message": "Añade 50 bolsas de arroz al local Centro",
  "context": {
    "current_locales": ["Centro", "Norte", "Sur"],
    "last_interaction": "2026-04-23T14:30:00Z"
  }
}
```

### Campos obligatorios

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `tenant_id` | string | ID de la empresa (CRÍTICO para multi-tenant) |
| `user_id` | string | ID del usuario que habla |
| `user_rol` | string | OWNER, ADMIN, STAFF |
| `plan` | string | STARTER, PRO (para filtrar acciones) |
| `message` | string | Texto del usuario |

### Campos opcionales

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `context` | object | Locales activos, última interacción |
| `language` | string | es, en (default: es) |

---

## 2. INTENTS — Qué puede pedir el usuario

### 2.1 Inventario

| Intent | Ejemplo | Parámetros |
|--------|---------|------------|
| `ADD_STOCK` | "Añade 50 arroz al local Centro" | `producto`, `cantidad`, `local` |
| `REMOVE_STOCK` | "Retira 10 Jabón del local Norte" | `producto`, `cantidad`, `local` |
| `TRANSFER_STOCK` | "Mueve 20 arroz de Centro a Norte" | `producto`, `cantidad`, `origen`, `destino` |
| `GET_STOCK` | "¿Cuántas unidades de arroz tenemos?" | `producto` (opcional), `local` (opcional) |
| `SET_STOCK` | "Pon el stock de arroz en 200" | `producto`, `cantidad`, `local` |

### 2.2 Ventas/Métricas

| Intent | Ejemplo | Parámetros |
|--------|---------|------------|
| `GET_SALES` | "¿Cuánto vendimos hoy?" | `periodo` (hoy, semana, mes) |
| `GET_TOP_PRODUCTS` | "¿Cuál es el producto más vendido?" | `periodo`, `tipo` (ventas, margen) |
| `GET_LOW_STOCK` | "¿Qué productos tienen poco stock?" | `local` (opcional), `umbral` |
| `GET_PROFIT` | "¿Cuánto ganamos esta semana?" | `periodo` |

### 2.3 Locales

| Intent | Ejemplo | Parámetros |
|--------|---------|------------|
| `ADD_LOCAL` | "Crea un nuevo local en Maipú" | `nombre`, `direccion` |
| `GET_LOCALES` | "¿Cuántos locales tengo?" | ninguno |
| `CLOSE_LOCAL` | "Cierra el local Sur temporalmente" | `local`, `motivo` |

### 2.4 Usuarios (solo ADMIN/OWNER)

| Intent | Ejemplo | Parámetros |
|--------|---------|------------|
| `ADD_USER` | "Agrega a María como administradora" | `nombre`, `email`, `rol` |
| `REMOVE_USER` | "Elimina al usuario Pedro" | `email` |
| `LIST_USERS` | "¿Quién tiene acceso al sistema?" | ninguno |

### 2.5 Sistema

| Intent | Ejemplo | Parámetros |
|--------|---------|------------|
| `HELP` | "¿Qué puedes hacer?" | ninguno |
| `PLAN` | "¿Qué plan tengo?" | ninguno |
| `REPORT_BUG` | "Hay un error en el stock" | `descripcion` |

---

## 3. RESPONSE — Respuesta del Agent

### 3.1 Tipos de respuesta

| Type | Cuándo usar | Ejemplo |
|------|------------|--------|
| `success` | Acción completada | "✅ Agregado: 50 arroz → Local Centro" |
| `action_required` | Necesita confirmación | "Confirmar ingreso de 50 arroz a Local Centro" |
| `data` | Devuelve información | "Tienes 150 unidades de arroz en Centro" |
| `error` | Hubo un problema | "No encontré el producto 'arroz'." |
| `clarification` | Necesita más info | "¿A qué local te refieres: Centro o Norte?" |
| `permission_denied` | Plan no permite | "Esta función es solo para Plan PRO" |

### 3.2 Schema de respuesta completa

```json
{
  "type": "action_required",
  "intent": "ADD_STOCK",
  "action_id": "act_abc123",
  "message": "Confirmar ingreso de 50 bolsas de arroz a Local Centro",
  "params": {
    "producto": "arroz",
    "cantidad": 50,
    "local": "Centro",
    "producto_id": "prod_xyz789",
    "local_id": "loc_abc123"
  },
  "confirmation_needed": true,
  "visual_hint": "card_pop",
  "can_expire": true,
  "expires_in": 60,
  "suggestions": [
    "Revisa el stock actual",
    "Ver otras-transferencias"
  ]
}
```

### 3.3 Respuesta exitosa

```json
{
  "type": "success",
  "intent": "ADD_STOCK",
  "message": "✅ Agregado: 50 bolsas de arroz → Local Centro",
  "data": {
    "stock_anterior": 100,
    "stock_nuevo": 150,
    "local": "Centro",
    "producto": "arroz"
  },
  "visual_hint": "pulse_green",
  "updates_dashboard": ["stock", "movimientos"]
}
```

### 3.4 Respuesta con datos

```json
{
  "type": "data",
  "intent": "GET_STOCK",
  "message": "Tienes estas unidades:",
  "data": {
    "items": [
      { "producto": "arroz", "local": "Centro", "stock": 150 },
      { "producto": "arroz", "local": "Norte", "stock": 80 },
      { "producto": "arroz", "local": "Sur", "stock": 45 }
    ],
    "total": 275
  },
  "visual_hint": "highlight_field",
  "chart_type": "bar"
}
```

### 3.5 Respuesta de error

```json
{
  "type": "error",
  "intent": "ADD_STOCK",
  "message": "No encontré el producto 'arroz' en tu inventario.",
  "error_code": "PRODUCT_NOT_FOUND",
  "suggestions": [
    { "text": "¿Buscas 'Arroz Grano Largo'?",
      "message": "Añade 50 Arroz Grano Largo al local Centro"
    },
    { "text": "Ver todos los productos",
      "message": "Muéstrame todos mis productos"
    }
  ]
}
```

### 3.6 Respuesta de permission denied

```json
{
  "type": "permission_denied",
  "intent": "ADD_LOCAL",
  "message": "Solo el Plan PRO permite locales ilimitados.",
  "current_plan": "STARTER",
  "feature": "multiple_locales",
  "upgrade_cta": "Pásate a PRO por $29.900/mes",
  "visual_hint": null
}
```

---

## 4. CONFIRMATION — Confirmación de usuario

```json
POST /agent/confirm
Content-Type: application/json

{
  "action_id": "act_abc123",
  "confirmed": true,
  "modified_params": {}
}
```

### Respuesta a confirmación

```json
{
  "type": "success",
  "intent": "ADD_STOCK",
  "message": "✅ Confirmado: 50 bolsas de arroz → Local Centro",
  "data": { "stock_nuevo": 150 },
  "visual_hint": "card_pop",
  "updates_dashboard": ["stock", "movimientos"]
}
```

### Si confirmación expira (60 segundos)

```json
{
  "type": "error",
  "message": "La acción expiró. ¿Quieres intentarlo de nuevo?",
  "error_code": "ACTION_EXPIRED",
  "visual_hint": null
}
```

---

## 5. VISUAL HINTS — Guía para Webapp

| Hint | Descripción | Cuándo usar |
|------|------------|-------------|
| `card_pop` | Tarjeta entra con animación bounce | Confirmaciones exitosas, primera venta, meta alcanzada |
| `highlight_field` | Campo parpadea en verde/amarillo | Actualización de datos visibles en dashboard |
| `pulse_green` | Pulso sutil en elemento actualizado | Actualizaciones menores de stock |
| `confetti` | Confetti sutil en pantalla | Hitos importantes (primera venta, meta mensual) |
| `loading` | Spinner/skeleton mientras procesa | Cargando datos |

### Reglas de uso

1. **card_pop** → Solo en acciones que modifican estado
2. **highlight_field** → Solo si el campo está visible
3. **pulse_green** → Máximo 1 por interacción
4. **confetti** → Reserved para hitos (no abusar)

---

## 6. EJEMPLOS DE CONVERSACIÓN

### Ejemplo 1: Añadir stock

```
USER: Añade 50 bolsas de arroz al local Centro
AGENT: {
  "type": "action_required",
  "intent": "ADD_STOCK",
  "confirmation_needed": true,
  "message": "Confirmar ingreso de 50 bolsas de arroz a Local Centro",
  "visual_hint": "card_pop"
}
USER: Sí, confirmar
AGENT: {
  "type": "success",
  "message": "✅ Agregado: 50 bolsas de arroz → Local Centro",
  "visual_hint": "pulse_green",
  "updates_dashboard": ["stock"]
}
```

### Ejemplo 2: Obtener métricas

```
USER: ¿Cuánto vendimos esta semana?
AGENT: {
  "type": "data",
  "intent": "GET_SALES",
  "message": "Esta semana vendiste:",
  "data": { "total": 1250000, "ventas": 45 },
  "visual_hint": "highlight_field"
}
```

### Ejemplo 3: Error con sugerencias

```
USER: Pon 200 jabones en la tienda
AGENT: {
  "type": "clarification",
  "message": "¿Te refieres a Centro, Norte o Sur?",
  "clarification_for": "local",
  "options": ["Centro", "Norte", "Sur"]
}
```

---

## 7. PERMISOS POR PLAN

| Intent | STARTER | PRO |
|--------|---------|-----|
| `ADD_STOCK` | ✅ | ✅ |
| `GET_STOCK` | ✅ | ✅ |
| `GET_SALES` | ✅ | ✅ |
| `GET_TOP_PRODUCTS` | ❌ | ✅ |
| `GET_PROFIT` | ❌ | ✅ |
| `ADD_LOCAL` | ❌ | ✅ |
| `ADD_USER` | ❌ | ✅ |
| `TRANSFER_STOCK` | ❌ | ✅ |

---

## 8. TENANT ISOLATION — Filtro obligatorio

**Regla:** TODA query del agent DEBE incluir `tenant_id`

```kotlin
// Filtro en el Agent Service
@Service
class AgentService {
  
  fun processMessage(request: AgentRequest): AgentResponse {
    // ⛔ NUNCA permitir queries sin tenant
    requireNotNull(request.tenant_id) { 
      throw TenantNotSpecifiedException() 
    }
    
    // Verificar que el tenant existe y pertenece al usuario
    val tenant = tenantRepository.findById(request.tenant_id)
      .orElseThrow { TenantNotFoundException() }
    
    require(tenant.owner_id == request.user_id) {
      throw TenantAccessDeniedException()
    }
    
    // ⛔ TODO query a inventory/sales debe filtrar por tenant_id
    val stock = inventoryService.getStock(
      tenantId = request.tenant_id,  // ← OBLIGATORIO
      productId = request.params.producto_id
    )
    
    return response
  }
}
```

---

## 9. ENDPOINTS FINALES

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/agent/chat` | Enviar mensaje |
| `POST` | `/agent/confirm` | Confirmar acción pendiente |
| `GET` | `/agent/history` | Historial de interacciones |
| `GET` | `/agent/pending` | Acciones pendientes por confirmar |

---

*Documento vivo — actualizar según evoluciona el agente*