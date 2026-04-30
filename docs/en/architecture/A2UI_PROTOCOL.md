# A2UI Protocol — Agent ↔ Webapp Contract

> **Version:** 1.0
> **Date:** 2026-04-23
> **Purpose:** Define the "language" between the Agent and the Webapp

---

## 1. REQUEST — Sent from Webapp

```json
POST /agent/chat
Content-Type: application/json

{
  "tenant_id": "emp_abc123",
  "user_id": "usr_xyz789",
  "user_rol": "OWNER",
  "plan": "PRO",
  "message": "Add 50 bags of rice to the Downtown branch",
  "context": {
    "current_locales": ["Downtown", "North", "South"],
    "last_interaction": "2026-04-23T14:30:00Z"
  }
}
```

### Mandatory Fields

| Field | Type | Description |
|-------|------|-------------|
| `tenant_id` | string | Company ID (CRITICAL for multi-tenant) |
| `user_id` | string | ID of the user speaking |
| `user_rol` | string | OWNER, ADMIN, STAFF |
| `plan` | string | STARTER, PRO (to filter actions) |
| `message` | string | User text |

### Optional Fields

| Field | Type | Description |
|-------|------|-------------|
| `context` | object | Active locations, last interaction |
| `language` | string | es, en (default: es) |

---

## 2. INTENTS — What the user can request

### 2.1 Inventory

| Intent | Example | Parameters |
|--------|---------|------------|
| `ADD_STOCK` | "Add 50 rice to Downtown" | `product`, `quantity`, `location` |
| `REMOVE_STOCK` | "Remove 10 Soap from North" | `product`, `quantity`, `location` |
| `TRANSFER_STOCK` | "Move 20 rice from Downtown to North" | `product`, `quantity`, `origin`, `destination` |
| `GET_STOCK` | "How many units of rice do we have?" | `product` (optional), `location` (optional) |
| `SET_STOCK` | "Set rice stock to 200" | `product`, `quantity`, `location` |

### 2.2 Sales/Metrics

| Intent | Example | Parameters |
|--------|---------|------------|
| `GET_SALES` | "How much did we sell today?" | `period` (today, week, month) |
| `GET_TOP_PRODUCTS` | "What is the best-selling product?" | `period`, `type` (sales, margin) |
| `GET_LOW_STOCK` | "Which products have low stock?" | `location` (optional), `threshold` |
| `GET_PROFIT` | "How much did we earn this week?" | `period` |

### 2.3 Locations

| Intent | Example | Parameters |
|--------|---------|------------|
| `ADD_LOCAL` | "Create a new branch in Maipú" | `name`, `address` |
| `GET_LOCALES` | "How many branches do I have?" | none |
| `CLOSE_LOCAL` | "Close the South branch temporarily" | `location`, `reason` |

### 2.4 Users (ADMIN/OWNER only)

| Intent | Example | Parameters |
|--------|---------|------------|
| `ADD_USER` | "Add Maria as administrator" | `name`, `email`, `role` |
| `REMOVE_USER` | "Remove user Pedro" | `email` |
| `LIST_USERS` | "Who has access to the system?" | none |

---

## 3. RESPONSE — Agent Response

### 3.1 Response Types

| Type | When to use | Example |
|------|------------|--------|
| `success` | Action completed | "✅ Added: 50 rice → Downtown branch" |
| `action_required` | Needs confirmation | "Confirm entry of 50 rice to Downtown?" |
| `data` | Analytical response | "This week you sold $1,250,000" |
| `clarification` | Missing parameters | "Do you mean Downtown or North?" |
| `error` | Controlled failure | "Product 'soap' not found." |
| `permission_denied` | Plan/Role restriction | "Only PRO plans allow unlimited branches." |

### 3.2 Data structure (success)

```json
{
  "type": "success",
  "intent": "ADD_STOCK",
  "message": "✅ Confirmed: 50 rice → Downtown",
  "data": { "new_stock": 150 },
  "visual_hint": "pulse_green",
  "updates_dashboard": ["stock", "movements"]
}
```

### 3.3 Confirmation required

```json
{
  "type": "action_required",
  "intent": "ADD_STOCK",
  "action_id": "act_abc123",
  "message": "Confirm adding 50 bags of rice to Downtown?",
  "data": {
    "product": "Rice",
    "qty": 50,
    "location": "Downtown"
  },
  "visual_hint": "card_pop"
}
```

---

## 4. VISUAL HINTS — Webapp Guide

| Hint | Description | When to use |
|------|------------|-------------|
| `card_pop` | Card enters with bounce animation | Successful confirmations, first sale |
| `highlight_field` | Field flashes green/amber | Visible data updates in dashboard |
| `pulse_green` | Subtle pulse on element | Minor stock updates |
| `confetti` | Subtle screen confetti | Major milestones |

---

## 5. TENANT ISOLATION — Mandatory Filter

**Rule:** EVERY agent query MUST include `tenant_id`.

```kotlin
// Filter in Agent Service
@Service
class AgentService {
  fun processMessage(request: AgentRequest): AgentResponse {
    // ⛔ NEVER allow queries without tenant
    requireNotNull(request.tenant_id) { throw TenantNotSpecifiedException() }
    
    // ⛔ EVERY query to inventory/sales MUST filter by tenant_id
    val stock = inventoryService.getStock(
      tenantId = request.tenant_id,  // ← MANDATORY
      productId = request.params.product_id
    )
    return response
  }
}
```
