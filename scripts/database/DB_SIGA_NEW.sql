-- ============================================================
-- SIGA - Base de Datos PostgreSQL
-- Modelo Relacional: 5 esquemas + PGVector
-- Total: 24 tablas
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "vector";

-- ============================================================
-- SCHEMA: siga_auth (Autenticación y Permisos)
-- Servicio: auth (:8081)
-- ============================================================

CREATE SCHEMA siga_auth;
COMMENT ON SCHEMA siga_auth IS 'Autenticación, usuarios operativos, permisos y asignación a locales';

-- -------------------------------------------------------
-- Table: siga_auth.usuarios
-- Usuarios operativos del sistema (empleados de las empresas)
-- -------------------------------------------------------
CREATE TABLE siga_auth.usuarios (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'OPERADOR', 'CAJERO')),
    usuario_comercial_id INTEGER,  -- Referencia lógica al tenant (siga_comercial.usuarios)
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_usuarios_email ON siga_auth.usuarios(email);
CREATE INDEX idx_usuarios_comercial ON siga_auth.usuarios(usuario_comercial_id);
COMMENT ON TABLE siga_auth.usuarios IS 'Usuarios operativos del sistema SaaS';

-- -------------------------------------------------------
-- Table: siga_auth.permisos
-- Catálogo de operaciones permitidas
-- -------------------------------------------------------
CREATE TABLE siga_auth.permisos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX idx_permisos_codigo ON siga_auth.permisos(codigo);
CREATE INDEX idx_permisos_categoria ON siga_auth.permisos(categoria);
COMMENT ON TABLE siga_auth.permisos IS 'Catálogo de permisos del sistema';

-- -------------------------------------------------------
-- Table: siga_auth.roles_permisos
-- Plantilla de permisos por rol (PK compuesta)
-- -------------------------------------------------------
CREATE TABLE siga_auth.roles_permisos (
    rol VARCHAR(20) NOT NULL,
    permiso_id INTEGER NOT NULL,
    PRIMARY KEY (rol, permiso_id),
    CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (permiso_id) 
        REFERENCES siga_auth.permisos(id) ON DELETE CASCADE);
CREATE INDEX idx_roles_permisos_permiso ON siga_auth.roles_permisos(permiso_id);
COMMENT ON TABLE siga_auth.roles_permisos IS 'Plantilla base de permisos por rol';

-- -------------------------------------------------------
-- Table: siga_auth.usuarios_permisos
-- Permisos adicionales por usuario (PK compuesta)
-- -------------------------------------------------------
CREATE TABLE siga_auth.usuarios_permisos (
    usuario_id INTEGER NOT NULL,
    permiso_id INTEGER NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    asignado_por INTEGER,
    PRIMARY KEY (usuario_id, permiso_id),
    CONSTRAINT fk_usu_perm_usuario FOREIGN KEY (usuario_id) 
        REFERENCES siga_auth.usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usu_perm_permiso FOREIGN KEY (permiso_id) 
        REFERENCES siga_auth.permisos(id) ON DELETE CASCADE);
CREATE INDEX idx_usuarios_permisos_permiso ON siga_auth.usuarios_permisos(permiso_id);
COMMENT ON TABLE siga_auth.usuarios_permisos IS 'Permisos adicionales por usuario';

-- -------------------------------------------------------
-- Table: siga_auth.usuarios_locales
-- Asignación M:N usuarios a locales
-- -------------------------------------------------------
CREATE TABLE siga_auth.usuarios_locales (
    usuario_id INTEGER NOT NULL,
    local_id INTEGER NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, local_id),
    CONSTRAINT fk_usu_loc_usuario FOREIGN KEY (usuario_id) 
        REFERENCES siga_auth.usuarios(id) ON DELETE CASCADE);
    -- FK cross-schema (local_id → siga_inventario.locales) se crea al final del script
COMMENT ON TABLE siga_auth.usuarios_locales IS 'Asignación M:N de usuarios a locales';


-- ============================================================
-- SCHEMA: siga_inventario (Gestión de Inventario)
-- Servicio: inventario (:8082)
-- ============================================================

CREATE SCHEMA siga_inventario;
COMMENT ON SCHEMA siga_inventario IS 'Productos, categorías, stock, movimientos (Kardex) y alertas';

-- -------------------------------------------------------
-- Table: siga_inventario.locales
-- Sucursales/bodegas de la empresa
-- -------------------------------------------------------
CREATE TABLE siga_inventario.locales (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT,
    ciudad VARCHAR(100),
    usuario_comercial_id INTEGER NOT NULL,  -- Tenant
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX idx_locales_empresa ON siga_inventario.locales(usuario_comercial_id);
COMMENT ON TABLE siga_inventario.locales IS 'Sucursales/bodegas de la empresa';

-- -------------------------------------------------------
-- Table: siga_inventario.categorias
-- Agrupación de productos
-- -------------------------------------------------------
CREATE TABLE siga_inventario.categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    usuario_comercial_id INTEGER NOT NULL,  -- Tenant
    activa BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_categorias_nombre_empresa UNIQUE (nombre, usuario_comercial_id));
CREATE INDEX idx_categorias_empresa ON siga_inventario.categorias(usuario_comercial_id);
COMMENT ON TABLE siga_inventario.categorias IS 'Agrupación de productos por empresa';

-- -------------------------------------------------------
-- Table: siga_inventario.productos
-- Catálogo maestro de productos
-- -------------------------------------------------------
CREATE TABLE siga_inventario.productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    categoria_id INTEGER,
    codigo_barras VARCHAR(50) UNIQUE,
    precio_unitario DECIMAL(10,2),
    usuario_comercial_id INTEGER NOT NULL,  -- Tenant
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) 
        REFERENCES siga_inventario.categorias(id) ON DELETE SET NULL);
CREATE INDEX idx_productos_empresa ON siga_inventario.productos(usuario_comercial_id);
CREATE INDEX idx_productos_categoria ON siga_inventario.productos(categoria_id);
CREATE INDEX idx_productos_barras ON siga_inventario.productos(codigo_barras);
COMMENT ON TABLE siga_inventario.productos IS 'Catálogo maestro de productos';

-- -------------------------------------------------------
-- Table: siga_inventario.stock
-- Cantidad actual por producto/local
-- -------------------------------------------------------
CREATE TABLE siga_inventario.stock (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER NOT NULL,
    local_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 0 CHECK (cantidad >= 0),
    cantidad_minima INTEGER NOT NULL DEFAULT 0,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_stock_producto_local UNIQUE (producto_id, local_id),
    CONSTRAINT fk_stock_producto FOREIGN KEY (producto_id) 
        REFERENCES siga_inventario.productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_local FOREIGN KEY (local_id) 
        REFERENCES siga_inventario.locales(id) ON DELETE CASCADE);
CREATE INDEX idx_stock_producto ON siga_inventario.stock(producto_id);
CREATE INDEX idx_stock_local ON siga_inventario.stock(local_id);
COMMENT ON TABLE siga_inventario.stock IS 'Cantidad actual por producto y local';

-- -------------------------------------------------------
-- Table: siga_inventario.movimientos
-- Kardex: historial completo de entradas/salidas
-- -------------------------------------------------------
CREATE TABLE siga_inventario.movimientos (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER NOT NULL,
    local_id INTEGER NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('ENTRADA', 'SALIDA', 'VENTA', 'AJUSTE', 'TRASLADO')),
    cantidad_anterior INTEGER NOT NULL,
    cantidad_nueva INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,  -- diferencia (puede ser negativa)
    usuario_id INTEGER,  -- Referencia lógica a siga_auth.usuarios
    venta_id INTEGER,  -- Referencia lógica a siga_ventas.ventas
    observaciones TEXT,
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) 
        REFERENCES siga_inventario.productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimiento_local FOREIGN KEY (local_id) 
        REFERENCES siga_inventario.locales(id) ON DELETE CASCADE);
CREATE INDEX idx_movimientos_producto ON siga_inventario.movimientos(producto_id);
CREATE INDEX idx_movimientos_fecha ON siga_inventario.movimientos(fecha_movimiento);
CREATE INDEX idx_movimientos_tipo ON siga_inventario.movimientos(tipo);
COMMENT ON TABLE siga_inventario.movimientos IS 'Kardex: historial de movimientos de stock';

-- -------------------------------------------------------
-- Table: siga_inventario.alertas
-- Notificaciones automáticas de inventario
-- -------------------------------------------------------
CREATE TABLE siga_inventario.alertas (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('STOCK_BAJO', 'STOCK_AGOTADO', 'VENTA_ALTA', 'MOVIMIENTO_SOSPECHOSO')),
    producto_id INTEGER,
    local_id INTEGER,
    mensaje TEXT NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT false,
    fecha_alerta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alerta_producto FOREIGN KEY (producto_id) 
        REFERENCES siga_inventario.productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerta_local FOREIGN KEY (local_id) 
        REFERENCES siga_inventario.locales(id) ON DELETE CASCADE);
CREATE INDEX idx_alertas_tipo ON siga_inventario.alertas(tipo);
CREATE INDEX idx_alertas_leida ON siga_inventario.alertas(leida);
COMMENT ON TABLE siga_inventario.alertas IS 'Notificaciones automáticas de inventario';


-- ============================================================
-- SCHEMA: siga_ventas (Punto de Venta - POS)
-- Servicio: ventas (:8083)
-- ============================================================

CREATE SCHEMA siga_ventas;
COMMENT ON SCHEMA siga_ventas IS 'POS, ventas, turnos de caja, transacciones y carrito';

-- -------------------------------------------------------
-- Table: siga_ventas.metodos_pago
-- Catálogo de métodos de pago
-- -------------------------------------------------------
CREATE TABLE siga_ventas.metodos_pago (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT true);
COMMENT ON TABLE siga_ventas.metodos_pago IS 'Catálogo de métodos de pago';

-- Data inicial
INSERT INTO siga_ventas.metodos_pago (nombre) VALUES 
    ('EFECTIVO'), ('TARJETA_DEBITO'), ('TARJETA_CREDITO'), ('TRANSFERENCIA');

-- -------------------------------------------------------
-- Table: siga_ventas.turnos_caja
-- Apertura y cierre de caja por cajero/local
-- -------------------------------------------------------
CREATE TABLE siga_ventas.turnos_caja (
    id SERIAL PRIMARY KEY,
    local_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,  -- Referencia lógica a siga_auth.usuarios
    fecha_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP,
    monto_inicial DECIMAL(10,2) NOT NULL,
    monto_final DECIMAL(10,2),
    monto_contado DECIMAL(10,2),  -- Conteo físico del cajero para reconciliación
    estado VARCHAR(10) NOT NULL DEFAULT 'ABIERTO' CHECK (estado IN ('ABIERTO', 'CERRADO')));
    -- FK cross-schema (local_id → siga_inventario.locales) se crea al final del script
CREATE INDEX idx_turnos_usuario ON siga_ventas.turnos_caja(usuario_id);
CREATE INDEX idx_turnos_estado ON siga_ventas.turnos_caja(estado);
COMMENT ON TABLE siga_ventas.turnos_caja IS 'Turnos de caja con reconciliación';

-- -------------------------------------------------------
-- Table: siga_ventas.ventas
-- Registro de ventas
-- -------------------------------------------------------
CREATE TABLE siga_ventas.ventas (
    id SERIAL PRIMARY KEY,
    local_id INTEGER NOT NULL,
    usuario_id INTEGER,  -- Referencia lógica a siga_auth.usuarios
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'COMPLETADA' 
        CHECK (estado IN ('COMPLETADA', 'CANCELADA', 'PENDIENTE')),
    observaciones TEXT,
    fecha_venta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
    -- FK cross-schema (local_id → siga_inventario.locales) se crea al final del script
CREATE INDEX idx_ventas_fecha ON siga_ventas.ventas(fecha_venta);
CREATE INDEX idx_ventas_estado ON siga_ventas.ventas(estado);
COMMENT ON TABLE siga_ventas.ventas IS 'Registro de ventas';

-- -------------------------------------------------------
-- Table: siga_ventas.detalles_venta
-- Líneas de productos por venta
-- -------------------------------------------------------
CREATE TABLE siga_ventas.detalles_venta (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,  -- Referencia lógica a siga_inventario.productos
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) 
        REFERENCES siga_ventas.ventas(id) ON DELETE CASCADE);
CREATE INDEX idx_detalles_venta ON siga_ventas.detalles_venta(venta_id);
COMMENT ON TABLE siga_ventas.detalles_venta IS 'Líneas de productos por venta';

-- -------------------------------------------------------
-- Table: siga_ventas.transacciones_pos
-- Detalle de pago por transacción
-- -------------------------------------------------------
CREATE TABLE siga_ventas.transacciones_pos (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER NOT NULL,
    turno_id INTEGER NOT NULL,
    metodo_pago_id INTEGER NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    ultimos_4_digitos VARCHAR(4),
    referencia_pago VARCHAR(100),
    fecha_transaccion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaccion_venta FOREIGN KEY (venta_id) 
        REFERENCES siga_ventas.ventas(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaccion_turno FOREIGN KEY (turno_id) 
        REFERENCES siga_ventas.turnos_caja(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaccion_metodo FOREIGN KEY (metodo_pago_id) 
        REFERENCES siga_ventas.metodos_pago(id) ON DELETE CASCADE);
CREATE INDEX idx_transacciones_venta ON siga_ventas.transacciones_pos(venta_id);
COMMENT ON TABLE siga_ventas.transacciones_pos IS 'Detalle de pago por transacción';

-- -------------------------------------------------------
-- Table: siga_ventas.carrito_pos
-- Carrito temporal durante la venta
-- -------------------------------------------------------
CREATE TABLE siga_ventas.carrito_pos (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER,  -- Nullable hasta completar
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL,
    fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
    -- FK cross-schema (producto_id → siga_inventario.productos) se crea al final del script
CREATE INDEX idx_carrito_venta ON siga_ventas.carrito_pos(venta_id);
COMMENT ON TABLE siga_ventas.carrito_pos IS 'Carrito temporal durante la venta';


-- ============================================================
-- SCHEMA: siga_comercial (Portal Comercial y Facturación)
-- Servicio: backend (:8084) / futuro billing
-- ============================================================

CREATE SCHEMA siga_comercial;
COMMENT ON SCHEMA siga_comercial IS 'Portal comercial: clientes, planes, suscripciones, pagos y facturación';

-- -------------------------------------------------------
-- Table: siga_comercial.usuarios
-- Clientes del portal (dueños de empresa)
-- -------------------------------------------------------
CREATE TABLE siga_comercial.usuarios (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    rut VARCHAR(20),
    telefono VARCHAR(20),
    nombre_empresa VARCHAR(255),
    rol VARCHAR(20) NOT NULL DEFAULT 'cliente' CHECK (rol IN ('admin', 'cliente')),
    en_trial BOOLEAN NOT NULL DEFAULT true,
    fecha_inicio_trial TIMESTAMP,
    fecha_fin_trial TIMESTAMP,
    plan_id INTEGER,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX idx_comercial_usuarios_email ON siga_comercial.usuarios(email);
COMMENT ON TABLE siga_comercial.usuarios IS 'Clientes del portal comercial';

-- -------------------------------------------------------
-- Table: siga_comercial.planes
-- Catálogo de planes SaaS
-- -------------------------------------------------------
CREATE TABLE siga_comercial.planes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    limite_bodegas INTEGER NOT NULL DEFAULT 1,
    limite_usuarios INTEGER NOT NULL DEFAULT 3,
    limite_productos INTEGER,  -- NULL = ilimitado
    precio_mensual DECIMAL(10,2) NOT NULL,
    precio_anual DECIMAL(10,2),
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT true);
COMMENT ON TABLE siga_comercial.planes IS 'Catálogo de planes SaaS';

-- Data inicial
INSERT INTO siga_comercial.planes (nombre, descripcion, limite_bodegas, limite_usuarios, limite_productos, precio_mensual, precio_anual, orden) VALUES
    ('Básico', 'Para pequeños negocios', 1, 3, 50, 29000, 290000, 1),
    ('Profesional', 'Para negocios en crecimiento', 3, 10, 200, 59000, 590000, 2),
    ('Empresas', 'Para empresas medianas', 10, 50, NULL, 149000, 1490000, 3);

-- -------------------------------------------------------
-- Table: siga_comercial.suscripciones
-- Contratos activos
-- -------------------------------------------------------
CREATE TABLE siga_comercial.suscripciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    plan_id INTEGER NOT NULL,
    periodo VARCHAR(20) NOT NULL DEFAULT 'MENSUAL' CHECK (periodo IN ('MENSUAL', 'ANUAL')),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA' 
        CHECK (estado IN ('ACTIVA', 'SUSPENDIDA', 'CANCELADA', 'VENCIDA')),
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_suscripcion_usuario FOREIGN KEY (usuario_id) 
        REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_suscripcion_plan FOREIGN KEY (plan_id) 
        REFERENCES siga_comercial.planes(id) ON DELETE CASCADE);
CREATE INDEX idx_suscripciones_usuario ON siga_comercial.suscripciones(usuario_id);
CREATE INDEX idx_suscripciones_estado ON siga_comercial.suscripciones(estado);
COMMENT ON TABLE siga_comercial.suscripciones IS 'Contratos activos de suscripciones';

-- -------------------------------------------------------
-- Table: siga_comercial.pagos
-- Registro de cobros
-- -------------------------------------------------------
CREATE TABLE siga_comercial.pagos (
    id SERIAL PRIMARY KEY,
    suscripcion_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' 
        CHECK (estado IN ('PENDIENTE', 'COMPLETADO', 'FALLIDO', 'REEMBOLSADO')),
    referencia VARCHAR(100),
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pago_suscripcion FOREIGN KEY (suscripcion_id) 
        REFERENCES siga_comercial.suscripciones(id) ON DELETE CASCADE,
    CONSTRAINT fk_pago_usuario FOREIGN KEY (usuario_id) 
        REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE);
CREATE INDEX idx_pagos_usuario ON siga_comercial.pagos(usuario_id);
CREATE INDEX idx_pagos_estado ON siga_comercial.pagos(estado);
COMMENT ON TABLE siga_comercial.pagos IS 'Registro de cobros';

-- -------------------------------------------------------
-- Table: siga_comercial.facturas
-- Documentos fiscales
-- -------------------------------------------------------
CREATE TABLE siga_comercial.facturas (
    id SERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    usuario_id INTEGER NOT NULL,
    usuario_nombre VARCHAR(255) NOT NULL,
    usuario_email VARCHAR(255) NOT NULL,
    plan_id INTEGER NOT NULL,
    plan_nombre VARCHAR(255) NOT NULL,
    precio_uf DECIMAL(10,4) NOT NULL,
    precio_clp DECIMAL(12,2),
    unidad VARCHAR(10) NOT NULL DEFAULT 'UF',
    fecha_compra TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PAGADA' 
        CHECK (estado IN ('PAGADA', 'PENDIENTE', 'VENCIDA', 'CANCELADA')),
    metodo_pago VARCHAR(100),
    ultimos_4_digitos VARCHAR(4),
    suscripcion_id INTEGER,
    pago_id INTEGER,
    iva DECIMAL(10,2),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_factura_usuario FOREIGN KEY (usuario_id) 
        REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_factura_plan FOREIGN KEY (plan_id) 
        REFERENCES siga_comercial.planes(id) ON DELETE CASCADE);
CREATE INDEX idx_facturas_usuario ON siga_comercial.facturas(usuario_id);
CREATE INDEX idx_facturas_numero ON siga_comercial.facturas(numero_factura);
COMMENT ON TABLE siga_comercial.facturas IS 'Documentos fiscales';

-- -------------------------------------------------------
-- Table: siga_comercial.carritos
-- Carrito de compra de planes
-- -------------------------------------------------------
CREATE TABLE siga_comercial.carritos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    plan_id INTEGER,
    periodo VARCHAR(20) NOT NULL DEFAULT 'MENSUAL' CHECK (periodo IN ('MENSUAL', 'ANUAL')),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_carrito_usuario FOREIGN KEY (usuario_id) 
        REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_carrito_plan FOREIGN KEY (plan_id) 
        REFERENCES siga_comercial.planes(id) ON DELETE SET NULL);
CREATE INDEX idx_carritos_usuario ON siga_comercial.carritos(usuario_id);
COMMENT ON TABLE siga_comercial.carritos IS 'Carrito de compra de planes';


-- ============================================================
-- SCHEMA: siga_agente (Agente IA y Vector Store)
-- Servicio: agente (:8000)
-- ============================================================

CREATE SCHEMA siga_agente;
COMMENT ON SCHEMA siga_agente IS 'Vector store (PGVector) y contextos de conversación IA';

-- -------------------------------------------------------
-- Table: siga_agente.documentos
-- Documentos indexados para RAG
-- -------------------------------------------------------
CREATE TABLE siga_agente.documentos (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    embedding vector(1536),  -- Dimensión de OpenAI embeddings (1536)
    fuente VARCHAR(100),
    usuario_comercial_id INTEGER,  -- Dueño del documento
    fecha_indexacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX idx_documentos_empresa ON siga_agente.documentos(usuario_comercial_id);
COMMENT ON TABLE siga_agente.documentos IS 'Documentos indexados para RAG';

-- -------------------------------------------------------
-- Table: siga_agente.conversaciones
-- Contextos de conversación IA por usuario
-- -------------------------------------------------------
CREATE TABLE siga_agente.conversaciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    session_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    contexto JSONB,  -- Historial de conversación
    metadata JSONB,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX idx_conversaciones_usuario ON siga_agente.conversaciones(usuario_id);
CREATE INDEX idx_conversaciones_session ON siga_agente.conversaciones(session_id);
COMMENT ON TABLE siga_agente.conversaciones IS 'Contextos de conversación IA';

-- -------------------------------------------------------
-- Table: siga_agente.respuestas
-- Historial de respuestas del agente
-- -------------------------------------------------------
CREATE TABLE siga_agente.respuestas (
    id SERIAL PRIMARY KEY,
    conversacion_id INTEGER NOT NULL,
    pregunta TEXT NOT NULL,
    respuesta TEXT NOT NULL,
    modelo VARCHAR(50),
    tokens INTEGER,
    fecha_respuesta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_respuesta_conversacion FOREIGN KEY (conversacion_id) 
        REFERENCES siga_agente.conversaciones(id) ON DELETE CASCADE);
CREATE INDEX idx_respuestas_conversacion ON siga_agente.respuestas(conversacion_id);
COMMENT ON TABLE siga_agente.respuestas IS 'Historial de respuestas del agente';


-- ============================================================
-- SEGURIDAD Y CUMPLIMIENTO (Ley 21.719)
-- ============================================================

-- ============================================================
-- FOREIGN KEYS CROSS-SCHEMA
-- Se crean al final para evitar errores de dependencia circular
-- ============================================================

-- siga_auth.usuarios_locales → siga_inventario.locales
ALTER TABLE siga_auth.usuarios_locales
    ADD CONSTRAINT fk_usu_loc_local FOREIGN KEY (local_id)
    REFERENCES siga_inventario.locales(id) ON DELETE CASCADE;

-- siga_ventas.turnos_caja → siga_inventario.locales
ALTER TABLE siga_ventas.turnos_caja
    ADD CONSTRAINT fk_turno_local FOREIGN KEY (local_id)
    REFERENCES siga_inventario.locales(id) ON DELETE CASCADE;

-- siga_ventas.ventas → siga_inventario.locales
ALTER TABLE siga_ventas.ventas
    ADD CONSTRAINT fk_venta_local FOREIGN KEY (local_id)
    REFERENCES siga_inventario.locales(id) ON DELETE CASCADE;

-- siga_ventas.carrito_pos → siga_inventario.productos
ALTER TABLE siga_ventas.carrito_pos
    ADD CONSTRAINT fk_carrito_producto FOREIGN KEY (producto_id)
    REFERENCES siga_inventario.productos(id) ON DELETE CASCADE;


-- ============================================================
-- AUDITORÍA: Triggers y Funciones
-- ============================================================

-- Función para actualizar fecha_actualizacion automáticamente
CREATE OR REPLACE FUNCTION update_fecha_actualizacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers para tablas con fecha_actualizacion
-- siga_auth
CREATE TRIGGER trigger_usuarios_actualizacion
    BEFORE UPDATE ON siga_auth.usuarios
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

-- siga_inventario
CREATE TRIGGER trigger_productos_actualizacion
    BEFORE UPDATE ON siga_inventario.productos
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

CREATE TRIGGER trigger_stock_actualizacion
    BEFORE UPDATE ON siga_inventario.stock
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

-- siga_comercial
CREATE TRIGGER trigger_comercial_usuarios_actualizacion
    BEFORE UPDATE ON siga_comercial.usuarios
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

CREATE TRIGGER trigger_suscripciones_actualizacion
    BEFORE UPDATE ON siga_comercial.suscripciones
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

CREATE TRIGGER trigger_facturas_actualizacion
    BEFORE UPDATE ON siga_comercial.facturas
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

CREATE TRIGGER trigger_carritos_actualizacion
    BEFORE UPDATE ON siga_comercial.carritos
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

-- siga_agente
CREATE TRIGGER trigger_conversaciones_actualizacion
    BEFORE UPDATE ON siga_agente.conversaciones
    FOR EACH ROW EXECUTE FUNCTION update_fecha_actualizacion();

-- Comentarios de cumplimiento para PGVector
COMMENT ON COLUMN siga_agente.documentos.embedding IS 'Embedding vectorial para búsqueda semántica (PGVector)';

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================