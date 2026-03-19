# Diagrama de Arquitectura de Microservicios SIGA

El siguiente diagrama refleja la arquitectura real y refinada de SIGA, basandose en las tablas de la base de datos divididas por contexto (`siga_comercial` y `siga_saas`) y el diseño de contingencia para la Inteligencia Artificial.

```mermaid
graph LR
    %% Elementos
    User([Usuario / Emprendedor])
    API[API Gateway]
    BFF[BFF / Creador de Contexto]

    %% Microservicios Principales
    MC[Microservicio Comercial]
    MI[Microservicio Core SAAS]
    IA[Microservicio Asistente IA]

    %% Almacenamiento
    subgraph AS [Almacenamiento Segregado]
        BD_C[(DB: siga_comercial)]
        BD_S[(DB: siga_saas)]
    end

    %% IA y Contingencia
    subgraph IAR [Inteligencia Artificial Resiliente]
        LLM[LLM Externo API]
        FB{Motor Fallback NL2SQL}
    end

    %% Conexiones espaciadas (Flechas largas para evitar colisiones)
    User ====>|Petición HTTPS| API
    API  ====> BFF

    BFF  --->|SaaS B2B| MC
    BFF  --->|JWT + Rol| MI
    BFF  --->|Prompt| IA

    MC   -.- |JPA| BD_C
    MI   -.- |JPA| BD_S

    IA   ===>|1. Consulta| LLM
    LLM  -.->|Fallo de API| FB
    FB   --->|2. Extracción Cruda a PL/SQL| MI
    FB   --->|3. Respuesta Adaptada| BFF

    %% ESTILOS ULTRA MINIMALISTAS (SOBRIEDAD CORPORATIVA)
    %% Nodos principales: Blanco y Negro
    style User fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style API fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style BFF fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style MC fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style MI fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style IA fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style BD_C fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style BD_S fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000
    style LLM fill:#ffffff,stroke:#333333,stroke-width:1px,color:#000000

    %% Fallback: Ámbar extremadamente suave y texto oscuro para legibilidad total
    style FB fill:#fffbeb,stroke:#d97706,stroke-width:1px,color:#000000

    %% Subgrafos: Sin fondo, solo un borde sutil punteado
    style AS fill:none,stroke:#9ca3af,stroke-width:1px,stroke-dasharray: 5 5,color:#000000
    style IAR fill:none,stroke:#9ca3af,stroke-width:1px,stroke-dasharray: 5 5,color:#000000
```

### Razonamiento Arquitectonico:
1. **Division Comercial vs Core**: Mantener las facturas del pago del SaaS (Comercial) alejadas de las lechugas y computadores del inventario (SAAS) garantiza que si un modulo cae, el otro sigue vivo.
2. **El Agente como Proxy Restringido**: La peticion al Agente (IA) viaja desde el BFF con los permisos cargados. Si el usuario es Chofer, la IA sabe por defecto que no puede inyectar SQL de borrado.
3. **Fallback PL/SQL**: Es imperativo en entornos de alta disponibilidad (inventarios en tiempo real) que la IA nunca paralice al operario. Si Gemini no responde, el modulo entra en modo "Consulta Directa" (Scripting) temporalmente.
