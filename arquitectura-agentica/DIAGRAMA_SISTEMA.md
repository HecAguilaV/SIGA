# Diagrama de Arquitectura de Microservicios SIGA

El siguiente diagrama refleja la arquitectura real y refinada de SIGA, basandose en las tablas de la base de datos divididas por contexto (`siga_comercial` y `siga_saas`) y el diseño de contingencia para la Inteligencia Artificial.

```mermaid
graph TD
    %% Estilos Profesionales Soberbios
    classDef client fill:#2D3748,stroke:#4A5568,stroke-width:2px,color:#E2E8F0;
    classDef gateway fill:#2B6CB0,stroke:#2C5282,stroke-width:3px,color:#E2E8F0;
    classDef service fill:#2F855A,stroke:#276749,stroke-width:2px,color:#E2E8F0;
    classDef database fill:#4A5568,stroke:#2D3748,stroke-width:2px,color:#E2E8F0;
    classDef ai fill:#805AD5,stroke:#6B46C1,stroke-width:2px,color:#E2E8F0;
    classDef fallback fill:#C53030,stroke:#9B2C2C,stroke-width:2px,color:#E2E8F0;

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
