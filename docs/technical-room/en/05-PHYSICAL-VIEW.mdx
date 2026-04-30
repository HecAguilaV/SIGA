# 05. Physical View (Deployment & Scaling)

Mapping of software components to server and network infrastructure.

[🇪🇸 Ver versión en Español](../es/05-PHYSICAL-VIEW.mdx)

## 1. Docker Network Topology (Internal Nodes)

```mermaid
graph BT
    subgraph Net [<b>Docker Internal Network</b>]
        GT[API Gateway]
        Eureka[Service Registry]
        
        subgraph Cluster [<b>Microservices Cluster</b>]
            S1[siga-auth]
            S2[siga-inventory]
            S3[siga-sales]
        end
        
        DB[(PostgreSQL)]
    end

    User((User)) -->|Port 8080| GT
    GT --> Eureka
    GT --> Cluster
    Cluster --> DB

    %% Glass-Tech Styles
    style Net fill:#0ea5e90a,stroke:#38bdf8,stroke-width:2px,stroke-dasharray: 5 5
    style Cluster fill:#0ea5e90a,stroke:#38bdf8,stroke-width:2px
    style GT fill:#0369a1,stroke:#38bdf8,stroke-width:3px,color:#fff
    style DB fill:#0f172a,stroke:#38bdf8,color:#fff
```

## 2. Scaling Vision: Lambda Architecture

Structure for massive data processing (Big Data).

```mermaid
graph LR
    Input[POS Events] --> Kafka{Kafka}
    Kafka --> Speed[Speed Layer: Redis]
    Kafka --> Batch[Batch Layer: S3/HDFS]
    
    Speed --> UI[Real-time Dashboards]
    Batch --> UI

    style Kafka fill:#0369a1,stroke:#38bdf8,color:#fff
```

---

## 🛡️ Architect's Defense (Capstone Tips)

> **What is Lambda Architecture?**
> "It is the standard for handling massive data volumes. The **Speed Layer** gives us immediate business visibility, while the **Batch Layer** ensures historical reports are 100% accurate through background data cleansing processes."

---
> **Un Soñador con poca RAM 🧑~💻**
