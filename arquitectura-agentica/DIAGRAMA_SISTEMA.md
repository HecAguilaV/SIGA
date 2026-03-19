# Diagrama de Arquitectura de Microservicios SIGA

El siguiente diagrama refleja la arquitectura real y refinada de SIGA, basandose en las tablas de la base de datos divididas por contexto (`siga_comercial` y `siga_saas`) y el diseño de contingencia para la Inteligencia Artificial.

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'clusterBkg': '#1E293B', 'clusterBorder': '#334155', 'edgeLabelBackground': '#0F172A', 'mainBkg': '#0F172A'}}}%%
graph TD
    %% Estilos Profesionales Soberbios (Dark Slate Theme)
    classDef client fill:#1E293B,stroke:#475569,stroke-width:2px,color:#F8FAFC;
    classDef gateway fill:#0F172A,stroke:#38BDF8,stroke-width:2px,color:#F8FAFC;
    classDef service fill:#0F172A,stroke:#10B981,stroke-width:2px,color:#F8FAFC;
    classDef database fill:#0F172A,stroke:#64748B,stroke-width:2px,color:#F8FAFC;
    classDef ai fill:#0F172A,stroke:#8B5CF6,stroke-width:2px,color:#F8FAFC;
    classDef fallback fill:#0F172A,stroke:#F59E0B,stroke-width:2px,color:#F8FAFC;


    User([Usuario / Emprendedor]):::client

    %% Gateway & Orquestación
    User ===|Petición HTTPS| API[API Gateway]:::gateway
    API === BFF[BFF / Creador de Contexto]:::gateway

    %% Módulos Independientes
    BFF --->|Manejo SaaS B2B| MC[Microservicio Comercial]:::service
    BFF --->|JWT + UsuarioPermiso| MI[Microservicio Core SAAS]:::service
    BFF --->|Prompt Contextualizado| IA[Microservicio Asistente IA]:::ai

    %% Almacenamiento
    subgraph Almacenamiento Segregado
        MC -.- |JPA| BD_C[(DB: siga_comercial)]:::database
        MI -.- |JPA| BD_S[(DB: siga_saas)]:::database
    end

    %% IA y Contingencia
    subgraph Inteligencia Artificial Resiliente
        IA === |1. Consulta Core| LLM[LLM Externo API]:::ai
        LLM -.->|Fallo de Conexión / Timeout| FB{Motor Fallback Local NL2SQL}:::fallback
        FB --->|2. Extracción Regex a PL/SQL| MI
        FB --->|3. Respuesta Básica Adaptada| BFF
    end
```

### Razonamiento Arquitectonico:
1. **Division Comercial vs Core**: Mantener las facturas del pago del SaaS (Comercial) alejadas de las lechugas y computadores del inventario (SAAS) garantiza que si un modulo cae, el otro sigue vivo.
2. **El Agente como Proxy Restringido**: La peticion al Agente (IA) viaja desde el BFF con los permisos cargados. Si el usuario es Chofer, la IA sabe por defecto que no puede inyectar SQL de borrado.
3. **Fallback PL/SQL**: Es imperativo en entornos de alta disponibilidad (inventarios en tiempo real) que la IA nunca paralice al operario. Si Gemini no responde, el modulo entra en modo "Consulta Directa" (Scripting) temporalmente.
