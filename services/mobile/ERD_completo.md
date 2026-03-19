erDiagram
    %% ==========================================
    %% ESQUEMA SIGA_COMERCIAL (ADMINISTRATIVO)
    %% ==========================================
    COMERCIAL_USUARIOS ||--o{ SUSCRIPCIONES : tiene
    COMERCIAL_USUARIOS ||--o{ CARRITOS : tiene
    PLANES ||--o{ SUSCRIPCIONES : define
    SUSCRIPCIONES ||--o{ PAGOS : genera
    SUSCRIPCIONES ||--o{ FACTURAS : factura
    PAGOS ||--o{ FACTURAS : paga

    COMERCIAL_USUARIOS["siga_comercial.USUARIOS"] {
        int id PK
        string email
        string rut
    }
    PLANES["siga_comercial.PLANES"] {
        int id PK
        string nombre
        decimal precio
        jsonb caracteristicas
    }
    SUSCRIPCIONES["siga_comercial.SUSCRIPCIONES"] {
        int id PK
        date fecha_inicio
        string estado
    }
    PAGOS["siga_comercial.PAGOS"] {
        int id PK
        decimal monto
        string estado
    }
    FACTURAS["siga_comercial.FACTURAS"] {
        int id PK
        string numero
        decimal monto
    }
    CARRITOS["siga_comercial.CARRITOS"] {
        int id PK
        int plan_id
    }

    %% ==========================================
    %% RELACION MULTI-TENANT (EL PUENTE)
    %% ==========================================
    COMERCIAL_USUARIOS ||--o| SAAS_USUARIOS : "es dueño de"
    %% Nota: La relación física es via email o lógica de negocio, 
    %% pero conceptualmente el usuario comercial es dueño de los datos operativos.

    %% ==========================================
    %% ESQUEMA SIGA_SAAS (OPERATIVO)
    %% ==========================================
    SAAS_USUARIOS ||--o{ USUARIOS_LOCALES : tiene
    LOCALES ||--o{ USUARIOS_LOCALES : pertenece
    
    COMERCIAL_USUARIOS ||--o{ PRODUCTOS : "es dueño de (tenant)"
    COMERCIAL_USUARIOS ||--o{ LOCALES : "es dueño de (tenant)"
    COMERCIAL_USUARIOS ||--o{ VENTAS : "es dueño de (tenant)"
    COMERCIAL_USUARIOS ||--o{ CATEGORIAS : "es dueño de (tenant)"

    SAAS_USUARIOS["siga_saas.USUARIOS"] {
        int id PK
        string email
        string rol
    }
    LOCALES["siga_saas.LOCALES"] {
        int id PK
        string nombre
        int usuario_comercial_id FK
    }

    CATEGORIAS ||--o{ PRODUCTOS : clasifica
    PRODUCTOS ||--o{ STOCK : tiene
    LOCALES ||--o{ STOCK : almacena
    
    PRODUCTOS["siga_saas.PRODUCTOS"] {
        int id PK
        string nombre
        int usuario_comercial_id FK
    }
    STOCK["siga_saas.STOCK"] {
        int id PK
        int cantidad
    }

    PRODUCTOS ||--o{ MOVIMIENTOS : registra
    LOCALES ||--o{ MOVIMIENTOS : ocurre_en
    VENTAS ||--o{ MOVIMIENTOS : genera
    SAAS_USUARIOS ||--o{ MOVIMIENTOS : autoriza

    SAAS_USUARIOS ||--o{ VENTAS : realiza
    LOCALES ||--o{ VENTAS : ocurre_en
    VENTAS ||--o{ DETALLES_VENTA : contiene
    PRODUCTOS ||--o{ DETALLES_VENTA : vende
    
    VENTAS["siga_saas.VENTAS"] {
        int id PK
        decimal total
        int usuario_comercial_id FK
    }

    TURNOS_CAJA ||--o{ TRANSACCIONES_POS : agrupa
    VENTAS ||--o{ TRANSACCIONES_POS : paga
    METODOS_PAGO ||--o{ TRANSACCIONES_POS : utiliza
    
    TURNOS_CAJA["siga_saas.TURNOS_CAJA"] {
        int id PK
        decimal monto_final
    }
