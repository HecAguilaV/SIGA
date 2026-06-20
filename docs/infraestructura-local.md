# Infraestructura Local — Desarrollo SIGA

> Diagrama de la arquitectura local para desarrollo con Docker Compose.
> Versión: Junio 2026

```mermaid
graph TB
    subgraph DEV["🖥️ Máquina Local — Desarrollo"]
        direction TB
        
        subgraph DOCKER["🐳 Docker Compose"]
            direction TB
            
            PG[("🗄️ PostgreSQL 16<br/>puerto: 5432<br/>siga_admin")]
            REDIS[("⚡ Redis 7<br/>puerto: 6379")]
            
            subgraph APPS["Aplicaciones"]
                REG[("🔷 Registry (Eureka)<br/>puerto: 8761")]
                GW["🚪 Gateway<br/>Spring Cloud Gateway<br/>puerto: 8080"]
                
                AUTH["🔐 Auth<br/>puerto: 8081"]
                INV["📦 Inventory<br/>puerto: 8082"]
                SALES["💰 Sales<br/>puerto: 8083"]
                BILL["🧾 Billing<br/>puerto: 8084"]
                NOTIF["📧 Notification<br/>puerto: 8085"]
                AGENT["🤖 Agent (IA)<br/>puerto: 8000"]
                
                KAFKA["📨 Kafka (KRaft)<br/>puerto: 9092"]
                DASH["📊 Dashboard<br/>SvelteKit 5<br/>puerto: 3000"]
            end
            
            subgraph TOOLS["Herramientas"]
                PROM["⏱️ Prometheus<br/>puerto: 9090"]
                GRAF["📈 Grafana<br/>puerto: 3001"]
            end
        end
        
        subgraph CODE["📦 Código Fuente"]
            MONOREPO["Monorepo SIGA<br/>services/* — 8 MS<br/>apps/dashboard — Frontend<br/>k8s/ — Manifests<br/>terraform/ — IaC"]
        end
        
        subgraph IDE["💻 IDE + IA"]
            OPENCODE["OpenCode<br/>Orquestador Agentico<br/>SDD + TDD + BDD + HiTL"]
        end
    end

    subgraph EXTERNAL["🌐 Servicios Externos"]
        GEMINI["🧠 Google Gemini API<br/>IA Generativa"]
        SMTP["📧 SMTP (MailHog)<br/>Emails locales"]
    end

    %% Conexiones
    GW --> REG
    GW --> AUTH
    GW --> INV
    GW --> SALES
    GW --> BILL
    GW --> NOTIF
    GW --> AGENT
    
    AUTH --> PG
    INV --> PG
    SALES --> PG
    BILL --> PG
    NOTIF --> PG
    
    INV --> REDIS
    
    SALES -->|"Eventos"| KAFKA
    INV -->|"Consume"| KAFKA
    BILL -->|"Consume"| KAFKA
    NOTIF -->|"Consume"| KAFKA
    
    AGENT --> GEMINI
    NOTIF --> SMTP
    
    DASH -->|"HTTP :8080/api/*"| GW
    
    PROM --> GW
    PROM --> AUTH
    PROM --> INV
    PROM --> SALES
    PROM --> BILL
    PROM --> NOTIF
    GRAF --> PROM

    OPENCODE --> MONOREPO

    style DEV fill:#1a1a2e,color:#e0e0e0,stroke:#326ce5
    style DOCKER fill:#16213e,color:#c0d0ff,stroke:#0f3460
    style PG fill:#1a3a1a,color:#90ee90,stroke:#2ecc71
    style REDIS fill:#3a1a1a,color:#ff9090,stroke:#e74c3c
    style REG fill:#2a1a3a,color:#d090ff,stroke:#9b59b6
    style GW fill:#1a2a3a,color:#90c0ff,stroke:#3498db
    style KAFKA fill:#3a2a1a,color:#ffd090,stroke:#f39c12
    style AGENT fill:#1a2a2a,color:#90ffd0,stroke:#1abc9c
    style APPS fill:#16213e,color:#c0d0ff
    style TOOLS fill:#1a1a2a,color:#c0c0d0,stroke:#505080
    style CODE fill:#1a1a2e,color:#c0d0ff,stroke:#667eea
    style IDE fill:#2a1a2e,color:#ffc0ff,stroke:#e040e0
    style EXTERNAL fill:#2a2a1a,color:#e0e0a0,stroke:#aaaa40
```

---

## Puertos y Servicios — Resumen Local

| Servicio | Puerto Local | Tecnología | Depende de |
|:---------|:-------------|:-----------|:-----------|
| **siga-registry** (Eureka) | 8761 | Spring Cloud Netflix | — |
| **siga-gateway** | 8080 | Spring Cloud Gateway | registry |
| **siga-auth** | 8081 | Spring Boot + JWT | postgres, registry |
| **siga-inventory** | 8082 | Spring Boot + JPA | postgres, redis, registry |
| **siga-sales** | 8083 | Spring Boot + JPA | postgres, kafka, registry |
| **siga-billing** | 8084 | Spring Boot + JPA | postgres, kafka, registry |
| **siga-notification** | 8085 | Spring Boot + Mail | postgres, kafka, registry |
| **siga-agent** | 8000 | Spring Boot + Gemini | registry |
| **siga-dashboard** | 3000 | SvelteKit 5 | gateway |
| **siga-kafka** | 9092 | Apache Kafka (KRaft) | — |
| **PostgreSQL** | 5432 | PostgreSQL 16 | — |
| **Redis** | 6379 | Redis 7 | — |
| **Prometheus** | 9090 | Prometheus | todos los MS |
| **Grafana** | 3001 | Grafana | prometheus |

---

## Stack de Desarrollo

| Herramienta | Versión | Propósito |
|:------------|:--------|:----------|
| Java / Kotlin | 21 / 1.9 | Lenguaje backend |
| Spring Boot | 4.0.6 | Framework backend |
| SvelteKit | 5 | Framework frontend |
| Gradle | 8.x | Build tool |
| Docker Compose | 3.8 | Orquestación local |
| OpenCode | — | IDE agentico (SDD + TDD + BDD) |
| Kotest | 5.x | Testing (BehaviorSpec) |
| MockK | 1.13 | Mocking Kotlin |
| JaCoCo | 0.8.11 | Cobertura de código |
| Testcontainers | 1.19 | Tests de integración |
