# 02. Logical View (Design & Data)

The Logical View describes the technical organization of the system, focusing on decoupling and data integrity.

[🇪🇸 Ver versión en Español](../es/02-LOGICAL-VIEW.mdx)

## 1. Hexagonal Architecture (Ports and Adapters)

This pattern ensures that the core of SIGA remains independent of databases or frameworks.

```mermaid
graph TD
    subgraph Inbound [<b>Inbound Adapters</b>]
        Rest[REST Controller]
        Kafka[Kafka Consumer]
    end

    subgraph Core [<b>Domain (Business Rules)</b>]
        Service[Application Service]
        Entity[Business Entities]
    end

    subgraph Outbound [<b>Outbound Adapters</b>]
        Jpa[JPA Persistence Adapter]
        Feign[Feign Microservices Client]
    end

    Rest & Kafka --> Service
    Service --> Entity
    Entity --> Jpa & Feign

    style Core fill:#0369a120,stroke:#38bdf8,stroke-width:3px
    style Inbound fill:#0ea5e90a,stroke:#38bdf8
    style Outbound fill:#0ea5e90a,stroke:#38bdf8
```

## 2. Domain Class Diagram (UML)

Representation of business objects and their structural relationships.

```mermaid
classDiagram
    direction LR
    class Product {
        -UUID id
        -String sku
        -BigDecimal price
        +calculateTax()
    }
    class Sale {
        -UUID id
        -LocalDateTime timestamp
        +process()
    }
    class SaleItem {
        -Integer quantity
        -BigDecimal subtotal
    }
    class Stock {
        -Integer quantity
        -Integer threshold
    }
    
    Sale "1" *-- "1..*" SaleItem : composition
    SaleItem "0..*" --o "1" Product : references
    Product "1" *-- "0..*" Stock : tracks
    
    style Product fill:#1e293b,stroke:#38bdf8,color:#fff
    style Sale fill:#1e293b,stroke:#38bdf8,color:#fff
```

## 3. Entity-Relationship Diagram (Persistence)

Physical data mapping in the PostgreSQL database.

```mermaid
erDiagram
    PRODUCTS ||--o{ STOCKS : "has"
    PRODUCTS ||--o{ SALE_ITEMS : "included in"
    SALES ||--|{ SALE_ITEMS : "contains"
    USERS ||--o{ SALES : "registers"
    ROLES ||--o{ USERS : "assigned to"

    PRODUCTS {
        uuid id PK
        string sku UK
        string name
        decimal base_price
    }
    SALES {
        uuid id PK
        timestamp created_at
        decimal total_amount
        uuid user_id FK
    }
    SALE_ITEMS {
        uuid id PK
        uuid sale_id FK
        uuid product_id FK
        integer quantity
    }
    STOCKS {
        uuid id PK
        uuid product_id FK
        integer quantity
    }
```

---

## 🛡️ Architect's Defense (Capstone Tips)

> **Why separate Class from Entity (ER)?**
> "The class diagram represents **Behavior** (Domain). The ER diagram represents **State** (Persistence). In SIGA, this ensures that a change in a SQL table doesn't necessarily break price calculation logic in Java."

---
> **Un Soñador con poca RAM 🧑~💻**
