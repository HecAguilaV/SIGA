erDiagram
    %% ESQUEMA SIGA_SAAS (OPERATIVO)

    USUARIOS ||--o{ USUARIOS_LOCALES : tiene
    LOCALES ||--o{ USUARIOS_LOCALES : pertenece
    USUARIOS {
        int id PK
        string email
        string rol
    }
    LOCALES {
        int id PK
        string nombre
    }

    CATEGORIAS ||--o{ PRODUCTOS : clasifica
    PRODUCTOS ||--o{ STOCK : tiene
    LOCALES ||--o{ STOCK : almacena
    PRODUCTOS {
        int id PK
        string nombre
        decimal precio_unitario
        int usuario_comercial_id FK
    }
    STOCK {
        int id PK
        int cantidad
        int cantidad_minima
    }

    PRODUCTOS ||--o{ MOVIMIENTOS : registra
    LOCALES ||--o{ MOVIMIENTOS : ocurre_en
    VENTAS ||--o{ MOVIMIENTOS : genera
    MOVIMIENTOS {
        int id PK
        string tipo
        int cantidad
    }

    USUARIOS ||--o{ VENTAS : realiza
    LOCALES ||--o{ VENTAS : ocurre_en
    VENTAS ||--o{ DETALLES_VENTA : contiene
    PRODUCTOS ||--o{ DETALLES_VENTA : vende
    VENTAS {
        int id PK
        decimal total
        string estado
        int usuario_comercial_id FK
    }

    TURNOS_CAJA ||--o{ TRANSACCIONES_POS : agrupa
    VENTAS ||--o{ TRANSACCIONES_POS : paga
    METODOS_PAGO ||--o{ TRANSACCIONES_POS : utiliza
    TURNOS_CAJA {
        int id PK
        decimal monto_inicial
        decimal monto_final
        string estado
    }