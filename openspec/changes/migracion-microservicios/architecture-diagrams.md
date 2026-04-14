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

    subgraph DB["Base de Datos PostgreSQL :5432"]
        SCH_AU[("esq: siga_auth")]
        SCH_IN[("esq: siga_inventario")]
        SCH_VE[("esq: siga_ventas")]
        SCH_BI[("esq: siga_billing")]
    end
    
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
    AU --> SCH_AU
    IN --> SCH_IN
    VE --> SCH_VE
    BI --> SCH_BI
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
graph TD
    subgraph App["Microservicios SIGA - Red Docker"]
        GW["siga-gateway :8080"]
        EU["siga-eureka :8761"]
        AU["siga-auth :8081"]
        IN["siga-inventario :8082"]
        VE["siga-ventas :8083"]
        BI["siga-billing :8084"]
        AG["siga-agente :8085"]
        FB["siga-fallback :8086"]
        
        subgraph DB["PostgreSQL :5432"]
            SCH_AU[("siga_auth")]
            SCH_IN[("siga_inventario")]
            SCH_VE[("siga_ventas")]
            SCH_BI[("siga_billing")]
        end
        
        PA["pgAdmin :8090"]
    end

    subgraph Obs["Stack de Observabilidad"]
        PR["Prometheus :9090"]
        GR["Grafana :3000"]
        ES["Elasticsearch :9200"]
        LS["Logstash :5044"]
        KI["Kibana :5601"]
        ZI["Zipkin :9411"]
    end

    GW --> AU
    GW --> IN
    GW --> VE
    GW --> BI
    GW --> AG
    AU --> SCH_AU
    IN --> SCH_IN
    VE --> SCH_VE
    BI --> SCH_BI
    PA --> SCH_AU
    PA --> SCH_IN
    PA --> SCH_VE
    PA --> SCH_BI
    PR --> GR
    LS --> ES
    ES --> KI
```

> **Nota:** Todos los servicios envian metricas a Prometheus, logs a Logstash y traces a Zipkin.
> Las conexiones de observabilidad se omiten para mantener la claridad del diagrama.

---

## 6. Pipeline CI/CD (GitHub Actions)

```mermaid
graph LR
    subgraph Desarrollo
        DEV["Developer - Push a rama"]
    end

    subgraph CI["GitHub Actions - CI"]
        BU["Build + Test"]
        LI["Lint + Quality"]
        DO["Docker Build"]
    end

    subgraph CD["GitHub Actions - CD"]
        DH["Push a DockerHub"]
        DP["Deploy"]
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
graph TD
    subgraph OLTP["Operacional - Hoy"]
        VE["siga-ventas"]
        IN["siga-inventario"]
        DB[("PostgreSQL")]
    end

    subgraph Ingesta["Ingesta de Eventos"]
        CDC["Change Data Capture"]
        PS["Cloud Pub/Sub"]
    end

    subgraph Procesamiento
        DF["Dataflow - Apache Beam"]
    end

    subgraph OLAP["Analitico - Futuro"]
        BQ["BigQuery"]
        VA["Vertex AI"]
        DS["Looker Studio"]
    end

    VE --> DB
    IN --> DB
    DB --> CDC
    CDC --> PS
    PS --> DF
    DF --> BQ
    BQ --> VA
    BQ --> DS
    VA --> DS
```
