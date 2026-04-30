# 01. Vista de Escenarios (The "+1" View)

Esta vista define los casos de uso críticos que validan la arquitectura técnica de SIGA. Es el contrato de valor entre el negocio y la tecnología.

[🇺🇸 View English Version](../en/01-SCENARIOS.mdx)

## Diagrama de Casos de Uso (Core SIGA)

```mermaid
graph TD
    subgraph Actores [<b>Actores del Sistema</b>]
        Admin[Administrador / Dueño]
        Op[Operador de Caja]
        Inv[Encargado de Inventario]
        AI[Agente de IA]
    end

    subgraph Business [<b>Gestión de Negocio</b>]
        UC1(Administrar Inventario)
        UC2(Analizar Reportes de Ventas)
        UC3(Configurar Permisos de Empleados)
    end

    subgraph POS [<b>Operación POS</b>]
        UC4(Registrar Venta / Boleta)
        UC5(Apertura y Cierre de Turno)
    end

    subgraph AI_Layer [<b>Capacidades Agénticas</b>]
        AI1(Comandos de Voz y Chat)
        AI2(Búsqueda Semántica RAG)
    end

    Admin --> UC1 & UC2 & UC3
    Op --> UC4 & UC5
    Inv --> UC1
    
    AI -- Intermediario --> AI1 & AI2
    AI1 -.->|ejecuta| UC4
    AI2 -.->|asiste| UC1

    %% Estilos Glass-Tech
    style Actores fill:#0ea5e90a,stroke:#38bdf8,stroke-width:2px,stroke-dasharray: 5 5
    style Business fill:#0ea5e90a,stroke:#38bdf8,stroke-width:2px
    style POS fill:#0ea5e90a,stroke:#38bdf8,stroke-width:2px
    style AI_Layer fill:#0369a120,stroke:#38bdf8,stroke-width:3px
    
    style Admin fill:#0f172a,stroke:#38bdf8,color:#fff
    style AI fill:#0369a1,stroke:#38bdf8,stroke-width:3px,color:#fff
```

---

## 🛡️ Defensa del Arquitecto (Tips para tu Capstone)

> **¿Por qué el Agente de IA es un actor separado?**
> "Aunque técnicamente es un servicio, en el modelo 4+1 se trata como un actor porque introduce una nueva forma de interacción (A2UI - Interfaz de Agente a Usuario). Sin embargo, su seguridad es delegada: la IA actúa como un **Proxy** del usuario humano, heredando sus permisos granulares".

---
> **Un Soñador con poca RAM 🧑‍💻**
