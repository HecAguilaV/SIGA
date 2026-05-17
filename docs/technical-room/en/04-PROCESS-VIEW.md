# 04. Process View (Flow & States)

Describes system dynamics: how services interact and how entities change state.

[🇪🇸 Ver versión en Español](../es/04-PROCESS-VIEW.mdx)

## 1. Sequence Diagram: Sale Registration

```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant G as API Gateway
    participant S as Siga-Sales
    participant I as Siga-Inventory

    U->>G: POST /sales/payment
    G->>S: Request Redirection
    S->>I: Validate and Reserve Stock
    alt Stock Available
        I-->>S: Successful Reservation
        S->>S: Generate Invoice
        S-->>G: 201 Created
        G-->>U: Success Confirmation
    else Out of Stock
        I-->>S: Insufficient Stock
        S-->>G: 400 Bad Request
        G-->>U: Error: Product Sold Out
    end
```

## 2. State Diagram: Sale Lifecycle

This diagram details the system's state transition logic.

```mermaid
stateDiagram-v2
    [*] --> DRAFT: User adds products
    DRAFT --> PENDING_PAYMENT: Checkout initiated
    PENDING_PAYMENT --> PAID: Payment Confirmation
    PENDING_PAYMENT --> CANCELLED: Timeout / Error
    PAID --> DELIVERED: Products delivered
    PAID --> REFUNDED: Refund requested
    DELIVERED --> [*]
    CANCELLED --> [*]
    REFUNDED --> [*]

    state PENDING_PAYMENT {
        [*] --> LogicalLock
        LogicalLock --> [*]
    }
    note right of PENDING_PAYMENT : Stock is temporarily reserved.
```

---

## 🛡️ Architect's Defense (Capstone Tips)

> **Why use a State Diagram?**
> "Transactional integrity doesn't just depend on the database, but on object state coherence. By defining clear states like 'PENDING_PAYMENT', we allow the system to perform a **Logical Lock (Soft-lock)** on stock, preventing duplicate sales of the same item."

---
> **Un Soñador con poca RAM 🧑~💻**
