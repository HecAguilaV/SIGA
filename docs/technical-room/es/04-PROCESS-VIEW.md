# 04. Vista de Proceso (Flujos y Estados)

Describe la dinámica del sistema: cómo interactúan los servicios y cómo cambian de estado las entidades.

[🇺🇸 View English Version](../en/04-PROCESS-VIEW.mdx)

## 1. Diagrama de Secuencia: Registro de Venta

```mermaid
sequenceDiagram
    autonumber
    participant U as Usuario
    participant G as API Gateway
    participant S as Siga-Sales
    participant I as Siga-Inventory

    U->>G: POST /ventas/pago
    G->>S: Redirección de Petición
    S->>I: Validar y Reservar Stock
    alt Stock Disponible
        I-->>S: Reserva Exitosa
        S->>S: Generar Boleta
        S-->>G: 201 Creado
        G-->>U: Confirmación de Éxito
    else Sin Stock
        I-->>S: Stock Insuficiente
        S-->>G: 400 Petición Incorrecta
        G-->>U: Error: Producto Agotado
    end
```

## 2. Diagrama de Estados: Ciclo de Vida de una Venta

Este diagrama detalla la lógica de transición de estados del sistema.

```mermaid
stateDiagram-v2
    [*] --> BORRADOR: Usuario agrega productos
    BORRADOR --> PENDIENTE_PAGO: Checkout iniciado
    PENDIENTE_PAGO --> PAGADA: Confirmación de Pago
    PENDIENTE_PAGO --> CANCELADA: Tiempo Agotado / Error
    PAGADA --> ENTREGADA: Productos entregados
    PAGADA --> REEMBOLSADA: Devolución solicitada
    ENTREGADA --> [*]
    CANCELADA --> [*]
    REEMBOLSADA --> [*]

    state PENDIENTE_PAGO {
        [*] --> BloqueoLógico
        BloqueoLógico --> [*]
    }
    note right of PENDIENTE_PAGO : El stock se reserva temporalmente.
```

---

## 🛡️ Defensa del Arquitecto (Tips para tu Capstone)

> **¿Por qué usar un Diagrama de Estados?**
> "La integridad transaccional no solo depende de la base de datos, sino de la coherencia del estado del objeto. Al definir estados claros como 'PENDIENTE_PAGO', permitimos que el sistema realice un **Bloqueo Lógico (Soft-lock)** del stock, evitando ventas duplicadas del mismo artículo".

---
> **Un Soñador con poca RAM 🧑~💻**
