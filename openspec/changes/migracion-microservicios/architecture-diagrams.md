# Diagramas de Arquitectura — SIGA Microservicios

Diagramas técnicos del sistema SIGA en su arquitectura objetivo de microservicios.

---

## 1. Vista General del Sistema

```mermaid
graph TD
    subgraph Clientes
        WA["Webapp (SvelteKit)"]
        WC["Web Comercial (React)"]
    end

    GW["siga-gateway :8080"]
    EU["siga-eureka :8761"]

    subgraph Servicios de Negocio
        AU["siga-auth :8081"]
        IN["siga-inventario :8082"]
        VE["siga-ventas :8083"]
        BI["siga-billing :8084"]
    end

    subgraph Servicios de Inteligencia
        AG["siga-agente :8085"]
        FB["siga-fallback :8086"]
    end

    DB[("PostgreSQL :5432")]

    WA --> GW
    WC --> GW
    GW --> AU
    GW --> IN
    GW --> VE
    GW --> BI
    GW --> AG
    AG --> FB
    AU --> DB
    IN --> DB
    VE --> DB
    BI --> DB
```

> Nota: Todos los servicios se registran en `siga-eureka`. Las flechas de registro
> se omiten para mantener la claridad del diagrama.

---

## 2. Service Discovery (Eureka)

```mermaid
graph LR
    EU["siga-eureka :8761"]

    AU["siga-auth"] -.->|registra| EU
    IN["siga-inventario"] -.->|registra| EU
    VE["siga-ventas"] -.->|registra| EU
    BI["siga-billing"] -.->|registra| EU
    AG["siga-agente"] -.->|registra| EU
    FB["siga-fallback"] -.->|registra| EU
    GW["siga-gateway"] -.->|registra| EU

    GW -->|descubre| EU
```

---

## 3. Esquema de Base de Datos

```mermaid
graph TD
    DB[("PostgreSQL :5432")]

    subgraph siga_saas
        US["Usuarios (operadores)"]
        PE["Permisos"]
        PR["Productos"]
        CA["Categorías"]
        LO["Locales"]
        ST["Stock"]
        VT["Ventas"]
        DV["Detalles Venta"]
    end

    subgraph siga_comercial
        UC["Usuarios Comerciales"]
        PL["Planes"]
        SU["Suscripciones"]
        FA["Facturas"]
    end

    DB --- siga_saas
    DB --- siga_comercial

    AU["siga-auth"] --> siga_saas
    IN["siga-inventario"] --> siga_saas
    VE["siga-ventas"] --> siga_saas
    BI["siga-billing"] --> siga_comercial
```

---

## 4. Flujo de Autenticación (OAuth2 + JWT)

```mermaid
sequenceDiagram
    actor U as Usuario
    participant W as Webapp
    participant GW as siga-gateway
    participant AU as siga-auth
    participant G as Google OAuth2

    U->>W: Clic en "Iniciar con Google"
    W->>G: Redirige a Google
    G->>W: Authorization code
    W->>GW: POST /api/auth/oauth2/google
    GW->>AU: Forward
    AU->>G: Intercambia code por token
    G-->>AU: email, nombre, foto
    AU-->>GW: JWT SIGA (tenant_id, rol)
    GW-->>W: 200 OK + JWT
    Note over W: Almacena JWT
    W->>GW: GET /api/inventario (Bearer JWT)
    GW-->>W: 200 OK + datos
```

---

## 5. Flujo de Venta

```mermaid
sequenceDiagram
    actor C as Cajero
    participant GW as siga-gateway
    participant VE as siga-ventas
    participant IN as siga-inventario
    participant DB as PostgreSQL

    C->>GW: POST /api/ventas
    GW->>VE: Forward

    loop Por cada producto
        VE->>IN: GET /stock/{productoId}
        IN-->>VE: cantidad disponible
    end

    alt Stock suficiente
        VE->>DB: INSERT venta + detalles
        VE->>IN: PUT /stock/descontar
        VE-->>GW: 201 Created
        GW-->>C: Venta registrada
    else Stock insuficiente
        VE-->>GW: 409 Conflict
        GW-->>C: Error
    end
```

---

## 6. Flujo del Asistente IA con Fallback

```mermaid
sequenceDiagram
    actor U as Usuario
    participant AG as siga-agente
    participant GM as Gemini API
    participant FB as siga-fallback
    participant IN as siga-inventario

    U->>AG: "¿Cuánto queda del producto X?"
    AG->>AG: Analizar intención

    alt Gemini disponible
        AG->>GM: Prompt + contexto
        GM-->>AG: Respuesta natural
        AG-->>U: Respuesta completa
    else Gemini no disponible
        AG->>FB: POST /fallback/consulta
        FB->>IN: GET /stock/{productoId}
        IN-->>FB: cantidad: 42
        FB-->>AG: Respuesta estructurada
        AG-->>U: Respuesta (modo reducido)
    end
```

---

## 7. Infraestructura Docker

```mermaid
graph TD
    subgraph Aplicación
        EU["siga-eureka :8761"]
        GW["siga-gateway :8080"]
        AU["siga-auth :8081"]
        IN["siga-inventario :8082"]
        VE["siga-ventas :8083"]
        BI["siga-billing :8084"]
        AG["siga-agente :8085"]
        FB["siga-fallback :8086"]
    end

    subgraph Datos
        DB[("PostgreSQL :5432")]
        PA["pgAdmin :8090"]
    end

    subgraph Observabilidad
        PR["Prometheus :9090"]
        GR["Grafana :3000"]
        ZI["Zipkin :9411"]
    end

    subgraph Logs
        LS["Logstash :5044"]
        ES["Elasticsearch :9200"]
        KI["Kibana :5601"]
    end

    PA --> DB
    GR --> PR
    LS --> ES
    KI --> ES
```

> Nota: Todas las conexiones de los servicios de aplicación hacia la base de datos,
> Prometheus, Logstash y Zipkin se omiten para mantener la legibilidad.
> La red Docker (`siga-network`) conecta todos los contenedores entre sí.

---

## 8. Pipeline CI/CD

```mermaid
graph LR
    A["Push a rama"] --> B["Build + Test"]
    B --> C["Lint / Quality"]
    C --> D["Docker Build"]
    D --> E["Push DockerHub"]
    E --> F["Deploy"]
```

---

## 9. Preparación Big Data (GCP)

```mermaid
graph TD
    subgraph OLTP["Operacional (Hoy)"]
        VE["siga-ventas"]
        DB[("PostgreSQL")]
    end

    subgraph Ingesta
        CDC["Change Data Capture"]
        PS["Cloud Pub/Sub"]
    end

    subgraph Procesamiento
        DF["Dataflow (Apache Beam)"]
    end

    subgraph OLAP["Analítico (Futuro)"]
        BQ["BigQuery"]
        VA["Vertex AI (AutoML)"]
        DS["Looker Studio"]
    end

    VE --> DB
    DB --> CDC
    CDC --> PS
    PS --> DF
    DF --> BQ
    BQ --> VA
    BQ --> DS
```
