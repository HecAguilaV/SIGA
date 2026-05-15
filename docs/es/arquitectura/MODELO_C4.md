# Modelo de Arquitectura C4 - SIGA

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../../en/architecture/C4_MODEL.md)*

Este documento describe la arquitectura del **Sistema Inteligente de Gestión de Activos (SIGA)** utilizando el estándar del **Modelo C4**. El enfoque se centra en los niveles estratégicos (Contexto y Contenedores) para proporcionar una visión clara tanto para el negocio como para la ingeniería.

---

## ¿Qué es el Modelo C4?
El Modelo C4 es un enfoque estructurado para visualizar la arquitectura de software en diferentes niveles de abstracción:
1.  **Nivel 1 (Contexto)**: Muestra cómo el sistema interactúa con los usuarios y otros sistemas externos.
2.  **Nivel 2 (Contenedores)**: Muestra la arquitectura técnica de alto nivel y cómo se distribuyen las responsabilidades (Microservicios, Bases de Datos, Frontends).
3.  **Nivel 3 (Componentes)**: Estructura interna de un contenedor específico (Ej: Controladores, Servicios, Repositorios dentro del servicio de Inventario).
4.  **Nivel 4 (Código)**: Diagramas de clases UML o estructura de código puro.

*Nota: Para arquitecturas ágiles y orientadas a microservicios como SIGA, documentamos explícitamente los Niveles 1 y 2. Los niveles 3 y 4 se consideran implícitos en el código base y la Arquitectura Hexagonal.*

---

## Nivel 1: Diagrama de Contexto (System Context)

Este diagrama ilustra el panorama general de SIGA. Muestra a los usuarios que interactúan con el sistema y las dependencias externas (sistemas fiscales y financieros) que permiten la operatividad comercial completa.

```mermaid
flowchart TB
    %% Definición de Estilos
    classDef actor fill:#08427b,stroke:#052e56,stroke-width:2px,color:#fff
    classDef system fill:#1168bd,stroke:#0b4884,stroke-width:2px,color:#fff
    classDef external fill:#999999,stroke:#666666,stroke-width:2px,color:#fff

    %% Actores
    Admin(("Administrador /<br>Dueño de Pyme")):::actor
    Op(("Operador POS /<br>Vendedor")):::actor
    
    %% Sistema Principal
    SIGA["SIGA<br>[Sistema Principal]<br>Sistema Inteligente de Gestión de Activos"]:::system
    
    %% Sistemas Externos
    SII["Entidad Fiscal<br>[Sistema Externo]<br>Ej: SII Chile, DIAN, etc."]:::external
    Payment["Pasarela de Pago<br>[Sistema Externo]<br>Ej: Transbank, Stripe"]:::external
    
    %% Relaciones
    Admin -- "Administra inventario, revisa analíticas<br>y gestiona el negocio" --> SIGA
    Op -- "Registra ventas y controla<br>stock en sucursal" --> SIGA
    
    SIGA -. "Reporta DTEs (Documentos<br>Tributarios Electrónicos)" .-> SII
    SIGA -. "Procesa y valida transacciones<br>con tarjetas" .-> Payment
```

---

## Nivel 2: Diagrama de Contenedores (Containers)

Este diagrama hace un "zoom" dentro del bloque principal de SIGA. Muestra la arquitectura técnica basada en **Microservicios** que despliega el sistema, evidenciando el patrón de API Gateway, Service Registry y el modelo de *Database-per-service* (implementado lógicamente vía esquemas).

```mermaid
flowchart TB
    %% Estilos
    classDef actor fill:#08427b,color:#fff
    classDef container fill:#438dd5,stroke:#2e6295,color:#fff
    classDef db fill:#2e6295,stroke:#1b3a58,color:#fff
    classDef infrastructure fill:#d3d3d3,stroke:#999999,color:#000

    User(["Usuarios - Admin/POS"]):::actor
    
    subgraph SIGA_System [Ecosistema SIGA]
        direction TB
        
        %% Aplicaciones Cliente
        WebApp["WebApp V2<br>[Contenedor: SvelteKit]<br>Consola de administración UI"]:::container
        MobileApp["Mobile App<br>[Contenedor: Android/Kotlin]<br>App para operadores"]:::container
        
        %% Entrada
        Gateway["API Gateway<br>[Contenedor: Spring Cloud]<br>Enrutador único (Puerto 8080)"]:::container
        
        %% Orquestación y Mensajería
        Eureka(("Service Registry<br>[Infra: Netflix Eureka]<br>Descubrimiento de servicios")):::infrastructure
        Kafka(("Event Broker<br>[Infra: Apache Kafka]<br>Coreografía SAGA & Eventos")):::infrastructure
        
        %% Microservicios (Hexagonal Architecture)
        subgraph Microservicios [Core Backend - Hexagonal]
            Auth["siga-auth<br>[Spring Boot]"]
            Inv["siga-inventory<br>[Spring Boot<br>🔺 Hexagonal"]
            Sales["siga-sales<br>[Spring Boot<br>🔺 Hexagonal"]
            Bill["siga-billing<br>[Spring Boot<br>🔺 Hexagonal"]
            Agent["siga-agent<br>[Python/LangChain]"]
        end
        
        %% Base de datos
        DB[(PostgreSQL Multi-tenant)]:::db
    end
    
    %% Interacciones de Usuario
    User -->|Visita HTTPS| WebApp
    User -->|Usa App| MobileApp
    
    %% Hacia el Backend
    WebApp -->|REST/JSON| Gateway
    MobileApp -->|REST/JSON| Gateway
    
    %% Enrutamiento
    Gateway -->|Enruta peticiones| Auth
    Gateway -->|Enruta peticiones| Inv
    Gateway -->|Enruta peticiones| Sales
    Gateway -->|Enruta peticiones| Bill
    Gateway -->|Enruta peticiones| Agent
    
    %% Descubrimiento
    Auth -.->|Se registra| Eureka
    Inv -.->|Se registra| Eureka
    Sales -.->|Se registra| Eureka
    Bill -.->|Se registra| Eureka
    Agent -.->|Se registra| Eureka
    Gateway -.->|Consulta ubicaciones| Eureka
    
    %% Comunicación Asíncrona (SAGA)
    Sales -- "Publica SALE_INITIATED<br>(Topic: sale-events)" --> Kafka
    Kafka -- "Entrega evento" --> Inv
    Inv -- "Publica STOCK_RESERVED<br>(Topic: stock-events)" --> Kafka
    Kafka -- "Entrega respuesta" --> Sales
    Sales -- "Publica SALE_COMPLETED<br>(Topic: sale-completed)" --> Kafka
    Kafka -- "Entrega evento" --> Bill

    %% Persistencia
    Auth -->|Lee/Escribe esquema: auth| DB
    Inv -->|Lee/Escribe esquema: inventory| DB
    Sales -->|Lee/Escribe esquema: sales| DB
    Bill -->|Lee/Escribe esquema: billing| DB
    Agent -->|Búsqueda Vectorial esquema: agent| DB
```

### Decisiones Técnicas Clave (L2)
- **API Gateway**: Mantiene los microservicios internos ocultos del internet público. Centraliza CORS y enrutamiento.
- **Service Registry (Eureka)**: Permite escalar microservicios horizontalmente sin necesidad de balanceadores de carga físicos.
- **Mensajería (Kafka)**: Implementa el patrón **SAGA (Coreografía)** para transacciones distribuidas. Sales orquesta la reserva de stock con Inventory y, al confirmarse, publica un evento para que Billing genere la factura de venta (`SaleInvoice`).
- **Aislamiento de Datos**: Cada microservicio se conecta a un único servidor PostgreSQL, pero tiene su propio `esquema` restringido, garantizando que un servicio no pueda corromper los datos de otro de forma directa.

---
`> Un Soñador con poca RAM 🧑‍💻
