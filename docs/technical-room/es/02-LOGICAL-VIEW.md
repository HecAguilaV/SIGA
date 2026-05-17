# 02. Vista Lógica (Diseño y Datos)

La Vista Lógica describe la organización técnica del sistema, centrándose en el desacoplamiento y la integridad de los datos.

[🇺🇸 View English Version](../en/02-LOGICAL-VIEW.mdx)

## 1. Arquitectura Hexagonal (Puertos y Adaptadores)

Este patrón garantiza que el núcleo de SIGA sea independiente de la base de datos o el framework.

```mermaid
graph TD
    subgraph Entrada [<b>Adaptadores de Entrada</b>]
        Rest[Controlador REST]
        Kafka[Consumidor Kafka]
    end

    subgraph Core [<b>Dominio (Reglas de Negocio)</b>]
        Service[Servicio de Aplicación]
        Entity[Entidades de Negocio]
    end

    subgraph Salida [<b>Adaptadores de Salida</b>]
        Jpa[Adaptador de Persistencia JPA]
        Feign[Cliente de Microservicios Feign]
    end

    Rest & Kafka --> Service
    Service --> Entity
    Entity --> Jpa & Feign

    style Core fill:#0369a120,stroke:#38bdf8,stroke-width:3px
    style Entrada fill:#0ea5e90a,stroke:#38bdf8
    style Salida fill:#0ea5e90a,stroke:#38bdf8
```

## 2. Diagrama de Clases del Dominio (UML)

Representación de los objetos de negocio y sus relaciones estructurales.

```mermaid
classDiagram
    direction LR
    class Product {
        -UUID id
        -String sku
        -BigDecimal price
        +calcularImpuesto()
    }
    class Sale {
        -UUID id
        -LocalDateTime timestamp
        +procesar()
    }
    class SaleItem {
        -Integer quantity
        -BigDecimal subtotal
    }
    class Stock {
        -Integer quantity
        -Integer threshold
    }
    
    Sale "1" *-- "1..*" SaleItem : composición
    SaleItem "0..*" --o "1" Product : referencia
    Product "1" *-- "0..*" Stock : rastrea
    
    style Product fill:#1e293b,stroke:#38bdf8,color:#fff
    style Sale fill:#1e293b,stroke:#38bdf8,color:#fff
```

## 3. Diagrama Entidad-Relación (Persistencia)

Mapeo físico de los datos en la base de datos PostgreSQL.

```mermaid
erDiagram
    PRODUCTS ||--o{ STOCKS : "tiene"
    PRODUCTS ||--o{ SALE_ITEMS : "incluido en"
    SALES ||--|{ SALE_ITEMS : "contiene"
    USERS ||--o{ SALES : "registra"
    ROLES ||--o{ USERS : "asignado a"

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

## 🛡️ Defensa del Arquitecto (Tips para tu Capstone)

> **¿Por qué separar Clase de Entidad (ER)?**
> "El diagrama de clases representa el **Comportamiento** (Dominio). El diagrama ER representa el **Estado** (Persistencia). En SIGA, esto nos permite que un cambio en la tabla SQL no rompa necesariamente la lógica de cálculo de precios en Java".

---
> **Un Soñador con poca RAM 🧑~💻**
