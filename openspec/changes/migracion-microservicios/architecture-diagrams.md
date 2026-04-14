# Diagramas de Arquitectura — SIGA Microservicios

Diagramas técnicos del sistema SIGA en su arquitectura objetivo de microservicios.
Todos los diagramas utilizan la sintaxis Mermaid compatible con GitHub.

---

## 1. Vista General del Sistema

```mermaid
graph TD
    subgraph Clientes
        W1["Webapp SvelteKit"]
        W2["Web Comercial React"]
    end

    GW["siga-gateway :8080"]

    subgraph Negocio["Servicios de Negocio"]
        AU["siga-auth :8081"]
        IN["siga-inventario :8082"]
        VE["siga-ventas :8083"]
        BI["siga-billing :8084"]
    end

    subgraph IA["Servicios de Inteligencia"]
        AG["siga-agente :8085"]
        FB["siga-fallback :8086"]
    end

    DB[("PostgreSQL :5432")]
    EU["siga-eureka :8761"]

    W1 --> GW
    W2 --> GW
    GW --> AU
    GW --> IN
    GW --> VE
    GW --> BI
    GW --> AG
    AG -.->|Circuit Breaker| FB
    VE -.->|Verifica stock| IN
    AU --> DB
    IN --> DB
    VE --> DB
    BI --> DB
```

> **Nota:** Todos los servicios se registran en `siga-eureka` para Service Discovery.
> Las flechas de registro se omiten para mantener la claridad del diagrama.

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
flowchart TD
    subgraph App["Microservicios SIGA (Red Docker)"]
        direction TB
        GW["Gateway :8080"]
        MS["Servicios de Negocio"]
        DB["PostgreSQL :5432"]
        EU["Eureka :8761"]
        PA["pgAdmin :8090"]
        
        GW --> MS --> DB
        MS -.-> EU
        GW -.-> EU
        PA -.-> DB
    end

    subgraph Obs["Stack de Observabilidad"]
        direction TB
        PR["Prometheus :9090"] --> GR["Grafana :3000"]
        LS["Logstash :5044"] --> ES["Elasticsearch :9200"] --> KI["Kibana :5601"]
        ZI["Zipkin :9411"]
    end
    
    MS ==>|Métricas| PR
    MS ==>|Logs JSON| LS
    MS ==>|Traces| ZI
```

---

## 6. Pipeline CI/CD (GitHub Actions)

```mermaid
%%{init: {"flowchart": {"defaultRenderer": "elk"}}}%%
flowchart LR
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
%%{init: {"flowchart": {"defaultRenderer": "elk"}}}%%
flowchart TD
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
