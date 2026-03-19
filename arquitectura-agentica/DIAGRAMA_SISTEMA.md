# Diagrama de Arquitectura de SIGA

Este diagrama representa la vision estructurada del sistema, consolidando la decision de utilizar un modelo de autenticacion delegada para maximizar la seguridad y eficiencia.

## Arquitectura de Servicios

```mermaid
graph TD
    %% Definicion de Estilos
    classDef default fill:#f9f9f9,stroke:#333,stroke-width:1px;
    classDef frontend fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    classDef gateway fill:#fff3e0,stroke:#e65100,stroke-width:2px;
    classDef service fill:#f3e5f5,stroke:#4a148c,stroke-width:2px;
    classDef database fill:#e8f5e9,stroke:#1b5e20,stroke-width:2px;

    subgraph Cliente [Frontend]
        Web[SIGA Web Next.js]:::frontend
        Mobile[SIGA Mobile React Native]:::frontend
    end

    subgraph Infraestructura [Infraestructura de Acceso]
        Gateway[API Gateway / Auth Delegada]:::gateway
    end

    subgraph Servicios [Capa de Negocio]
        Inv[Servicio de Inventario]:::service
        Ventas[Servicio de Ventas / POS]:::service
        IA[Servicio Asistente de IA]:::service
        Com[Servicio Comercial]:::service
    end

    subgraph Persistencia [Persistencia de Datos]
        DB_Inv[(BD Inventario)]:::database
        DB_Ventas[(BD Ventas)]:::database
        DB_Com[(BD Comercial)]:::database
    end

    %% Conexiones
    Web --> Gateway
    Mobile --> Gateway
    
    Gateway --> Inv
    Gateway --> Ventas
    Gateway --> IA
    Gateway --> Com

    Inv --> DB_Inv
    Ventas --> DB_Ventas
    Com --> DB_Com
```

## Analisis Tecnico: Autenticacion Delegada (Opcion B)

Tras el debate tecnico, se ha decidido implementar la **Opcion B**: Autenticacion Gestionada (ej. Supabase / Auth0).

### Justificacion
- **Seguridad Garantizada**: Delegamos la gestion de credenciales y encriptacion a proveedores con certificaciones internacionales.
- **Eficiencia (Haiku)**: Reducimos la carga de mantenimiento del equipo, permitiendo centrar los recursos en la logica de negocio real de SIGA.
- **Escalabilidad**: El sistema de sesion y manejo de tokens es gestionado externamente, facilitando el escalado de los servicios core de forma independiente.

---
> Un Soñador con Poca RAM & Misael
