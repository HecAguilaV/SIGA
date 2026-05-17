# 05. Vista Física (Despliegue y Escalabilidad)

Mapeo de los componentes de software a la infraestructura de servidores y red.

[🇺🇸 View English Version](../en/05-PHYSICAL-VIEW.mdx)

## 1. Topología de Red Docker (Nodos Internos)

```mermaid
graph BT
    subgraph Net [<b>Red Interna Docker</b>]
        GT[API Gateway]
        Eureka[Servidor de Registro]
        
        subgraph Cluster [<b>Clúster de Microservicios</b>]
            S1[siga-auth]
            S2[siga-inventory]
            S3[siga-sales]
        end
        
        DB[(PostgreSQL)]
    end

    User((Usuario)) -->|Puerto 8080| GT
    GT --> Eureka
    GT --> Cluster
    Cluster --> DB

    %% Estilos Glass-Tech
    style Net fill:#0ea5e90a,stroke:#38bdf8,stroke-width:2px,stroke-dasharray: 5 5
    style Cluster fill:#0ea5e90a,stroke:#38bdf8,stroke-width:2px
    style GT fill:#0369a1,stroke:#38bdf8,stroke-width:3px,color:#fff
    style DB fill:#0f172a,stroke:#38bdf8,color:#fff
```

## 2. Visión de Escalado: Arquitectura Lambda

Estructura para el procesamiento masivo de datos (Big Data).

```mermaid
graph LR
    Input[Eventos POS] --> Kafka{Kafka}
    Kafka --> Speed[Capa Rápida: Redis]
    Kafka --> Batch[Capa de Lote: S3/HDFS]
    
    Speed --> UI[Dashboards Tiempo Real]
    Batch --> UI

    style Kafka fill:#0369a1,stroke:#38bdf8,color:#fff
```

---

## 🛡️ Defensa del Arquitecto (Tips para tu Capstone)

> **¿Qué es la Arquitectura Lambda?**
> "Es el estándar para manejar volúmenes masivos de datos. La **Capa Rápida** nos da visibilidad inmediata para el negocio, mientras la **Capa de Lote** asegura que los reportes históricos sean 100% precisos mediante procesos de limpieza de datos en segundo plano".

---
> **Un Soñador con poca RAM 🧑~💻**
