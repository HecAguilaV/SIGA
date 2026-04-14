# Diagramas de Arquitectura — SIGA Microservicios

Diagramas técnicos del sistema SIGA en su arquitectura objetivo de microservicios.
Todos los diagramas utilizan la sintaxis Mermaid compatible con GitHub.

---

## 1. Vista General del Sistema

```mermaid
graph TB
    subgraph Clientes["Clientes"]
        WA["🖥️ Webapp<br/>(SvelteKit + Bulma)<br/>Cajeros / Operadores"]
        WC["🖥️ Web Comercial<br/>(React + Bootstrap)<br/>Dueños de Negocio"]
        LP["🌐 Landing Page<br/>(Estática)<br/>Vercel / GitHub Pages"]
    end

    subgraph Infraestructura["Capa de Infraestructura"]
        EU["siga-eureka<br/>:8761<br/>Service Discovery"]
        GW["siga-gateway<br/>:8080<br/>API Gateway + JWT"]
    end

    subgraph Negocio["Capa de Negocio"]
        AU["siga-auth<br/>:8081<br/>Identidad + OAuth2"]
        IN["siga-inventario<br/>:8082<br/>Catálogo + Stock"]
        VE["siga-ventas<br/>:8083<br/>Transacciones"]
        BI["siga-billing<br/>:8084<br/>Planes + Suscripciones"]
    end

    subgraph Inteligencia["Capa de Inteligencia"]
        AG["siga-agente<br/>:8085<br/>Asistente IA"]
        FB["siga-fallback<br/>:8086<br/>Resiliencia"]
    end

    subgraph Datos["Capa de Datos"]
        DB[("PostgreSQL :5432")]
        SS["esquema: siga_saas"]
        SC["esquema: siga_comercial"]
    end

    subgraph Externos["Servicios Externos"]
        GG["Google Gemini API"]
        GO["Google OAuth2"]
        AP["Apple Sign-In"]
    end

    WA --> GW
    WC --> GW
    GW --> EU
    GW --> AU
    GW --> IN
    GW --> VE
    GW --> BI
    GW --> AG

    AU -.->|registra| EU
    IN -.->|registra| EU
    VE -.->|registra| EU
    BI -.->|registra| EU
    AG -.->|registra| EU
    FB -.->|registra| EU

    AU --> GO
    AU --> AP
    AG --> GG
    AG -->|Circuit Breaker| FB

    VE -->|verificar stock| IN

    AU --> SS
    IN --> SS
    VE --> SS
    BI --> SC
    DB --- SS
    DB --- SC
```

---

## 2. Flujo de Autenticación (OAuth2 + JWT)

```mermaid
sequenceDiagram
    participant U as Usuario
    participant W as Webapp / Comercial
    participant GW as siga-gateway
    participant AU as siga-auth
    participant G as Google OAuth2
    participant DB as PostgreSQL

    U->>W: Clic en "Iniciar con Google"
    W->>G: Redirige a Google
    G->>W: Retorna authorization code
    W->>GW: POST /api/auth/oauth2/google {code}
    GW->>AU: Forward request
    AU->>G: Intercambia code por token de Google
    G-->>AU: {email, nombre, foto}
    AU->>DB: Buscar o crear usuario por email
    DB-->>AU: Usuario encontrado/creado
    AU-->>GW: JWT de SIGA {sub, tenant_id, rol, email}
    GW-->>W: 200 OK + JWT
    W->>W: Almacena JWT en localStorage/cookie
    U->>W: Navega a /inventario
    W->>GW: GET /api/inventario/productos (Header: Bearer JWT)
    GW->>GW: Valida JWT
    GW->>AU: (Opcional) Verifica permisos
    GW-->>W: 200 OK + datos
```

---

## 3. Flujo de Venta Completo

```mermaid
sequenceDiagram
    participant C as Cajero (Webapp)
    participant GW as siga-gateway
    participant VE as siga-ventas
    participant IN as siga-inventario
    participant DB as PostgreSQL (siga_saas)

    C->>GW: POST /api/ventas {productos, cantidades}
    GW->>VE: Forward (con tenant_id del JWT)

    loop Por cada producto
        VE->>IN: GET /api/inventario/stock/{productoId}/{localId}
        IN->>DB: SELECT cantidad FROM stock WHERE...
        DB-->>IN: cantidad disponible
        IN-->>VE: {disponible: 50}
        VE->>VE: Validar cantidad <= disponible
    end

    alt Stock suficiente
        VE->>DB: INSERT INTO ventas + detalles_venta
        VE->>IN: PUT /api/inventario/stock/descontar {productoId, cantidad}
        IN->>DB: UPDATE stock SET cantidad = cantidad - N
        DB-->>IN: OK
        IN-->>VE: Stock actualizado
        VE-->>GW: 201 Created {venta_id, total}
        GW-->>C: Venta registrada
    else Stock insuficiente
        VE-->>GW: 409 Conflict {error: "Stock insuficiente"}
        GW-->>C: Error mostrado al cajero
    end
```

---

## 4. Flujo del Asistente IA con Fallback

```mermaid
sequenceDiagram
    participant U as Usuario
    participant GW as siga-gateway
    participant AG as siga-agente
    participant GM as Google Gemini API
    participant FB as siga-fallback
    participant IN as siga-inventario

    U->>GW: POST /api/agente/chat {"¿Cuánto queda de X?"}
    GW->>AG: Forward

    AG->>AG: Analizar intención (NLP)

    alt Gemini disponible
        AG->>GM: Prompt + contexto del negocio
        GM-->>AG: Respuesta en lenguaje natural
        AG-->>GW: 200 OK {respuesta}
    else Gemini no responde (timeout / error)
        AG->>AG: Circuit Breaker ABIERTO
        AG->>FB: POST /fallback/consulta {intención, parámetros}
        FB->>IN: GET /api/inventario/stock/{productoId}
        IN-->>FB: {cantidad: 42}
        FB-->>AG: Respuesta estructurada
        AG-->>GW: 200 OK {respuesta + aviso: "modo reducido"}
    end

    GW-->>U: Respuesta al usuario
```

---

## 5. Infraestructura Docker Compose

```mermaid
graph LR
    subgraph Docker["Docker Compose"]
        subgraph Red["Red: siga-network"]
            EU["siga-eureka<br/>:8761"]
            GW["siga-gateway<br/>:8080"]

            AU["siga-auth<br/>:8081"]
            IN["siga-inventario<br/>:8082"]
            VE["siga-ventas<br/>:8083"]
            BI["siga-billing<br/>:8084"]
            AG["siga-agente<br/>:8085"]
            FB["siga-fallback<br/>:8086"]

            DB[("PostgreSQL<br/>:5432")]
            PA["pgAdmin<br/>:8090"]
        end

        subgraph Observabilidad["Observabilidad"]
            PR["Prometheus<br/>:9090"]
            GR["Grafana<br/>:3000"]
            ES["Elasticsearch<br/>:9200"]
            LS["Logstash<br/>:5044"]
            KI["Kibana<br/>:5601"]
            ZI["Zipkin<br/>:9411"]
        end
    end

    GW --> EU
    AU --> EU
    IN --> EU
    VE --> EU
    BI --> EU
    AG --> EU
    FB --> EU

    AU --> DB
    IN --> DB
    VE --> DB
    BI --> DB
    PA --> DB

    PR --> AU
    PR --> IN
    PR --> VE
    PR --> BI
    PR --> AG
    GR --> PR

    AU --> LS
    IN --> LS
    VE --> LS
    BI --> LS
    AG --> LS
    LS --> ES
    KI --> ES

    AU --> ZI
    IN --> ZI
    VE --> ZI
```

---

## 6. Pipeline CI/CD (GitHub Actions)

```mermaid
graph LR
    subgraph Desarrollo["Desarrollo"]
        DEV["Developer<br/>Push a rama"]
    end

    subgraph CI["GitHub Actions - CI"]
        BU["Build + Test<br/>./gradlew build"]
        LI["Lint + Quality<br/>ktlint / detekt"]
        DO["Docker Build<br/>por servicio"]
    end

    subgraph CD["GitHub Actions - CD"]
        DH["Push a<br/>DockerHub"]
        DP["Deploy<br/>(staging / prod)"]
    end

    DEV --> BU
    BU --> LI
    LI --> DO
    DO --> DH
    DH --> DP
```

---

## 7. Preparación Big Data (Pipeline Analítico)

```mermaid
graph TB
    subgraph OLTP["OLTP — Operacional (Hoy)"]
        VE["siga-ventas"]
        IN["siga-inventario"]
        DB[("PostgreSQL")]
    end

    subgraph Ingesta["Ingesta de Eventos"]
        CDC["Change Data Capture<br/>(Debezium / Eventos)"]
        PS["Cloud Pub/Sub"]
    end

    subgraph Procesamiento["Procesamiento"]
        DF["Dataflow<br/>(Apache Beam)"]
    end

    subgraph OLAP["OLAP — Analítico (Futuro)"]
        BQ["BigQuery<br/>Data Warehouse"]
        VA["Vertex AI<br/>AutoML / Pipelines"]
        DS["Dashboard<br/>Looker Studio"]
    end

    VE --> DB
    IN --> DB
    DB --> CDC
    CDC --> PS
    PS --> DF
    DF --> BQ
    BQ --> VA
    BQ --> DS

    VA -->|"Predicción de demanda<br/>Detección de anomalías<br/>Segmentación de clientes"| DS
```
