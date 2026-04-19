--
-- PostgreSQL database dump
--

\restrict EXx27ZUJvHmLzM6k2iiNHJsehcPBqahSYgbEBZOV6gFFOBZ5wC9Rce9DNvGfcvl

-- Dumped from database version 16.13
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: siga_comercial; Type: SCHEMA; Schema: -; Owner: hector
--

CREATE SCHEMA siga_comercial;


ALTER SCHEMA siga_comercial OWNER TO hector;

--
-- Name: SCHEMA siga_comercial; Type: COMMENT; Schema: -; Owner: hector
--

COMMENT ON SCHEMA siga_comercial IS 'Portal comercial y gestión de suscripciones';


--
-- Name: siga_saas; Type: SCHEMA; Schema: -; Owner: hector
--

CREATE SCHEMA siga_saas;


ALTER SCHEMA siga_saas OWNER TO hector;

--
-- Name: SCHEMA siga_saas; Type: COMMENT; Schema: -; Owner: hector
--

COMMENT ON SCHEMA siga_saas IS 'Sistema operativo de gestión de inventario y ventas';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: carritos; Type: TABLE; Schema: siga_comercial; Owner: hector
--

CREATE TABLE siga_comercial.carritos (
    id integer NOT NULL,
    usuario_id integer,
    plan_id integer,
    periodo character varying(20) DEFAULT 'MENSUAL'::character varying NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT carritos_periodo_check CHECK (((periodo)::text = ANY ((ARRAY['MENSUAL'::character varying, 'ANUAL'::character varying])::text[])))
);


ALTER TABLE siga_comercial.carritos OWNER TO hector;

--
-- Name: TABLE carritos; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON TABLE siga_comercial.carritos IS 'Carritos de compra de planes';


--
-- Name: carritos_id_seq; Type: SEQUENCE; Schema: siga_comercial; Owner: hector
--

CREATE SEQUENCE siga_comercial.carritos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_comercial.carritos_id_seq OWNER TO hector;

--
-- Name: carritos_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_comercial; Owner: hector
--

ALTER SEQUENCE siga_comercial.carritos_id_seq OWNED BY siga_comercial.carritos.id;


--
-- Name: facturas; Type: TABLE; Schema: siga_comercial; Owner: hector
--

CREATE TABLE siga_comercial.facturas (
    id integer NOT NULL,
    suscripcion_id integer,
    pago_id integer,
    numero_factura character varying(50) NOT NULL,
    usuario_id integer NOT NULL,
    usuario_nombre character varying(255) NOT NULL,
    usuario_email character varying(255) NOT NULL,
    plan_id integer NOT NULL,
    plan_nombre character varying(255) NOT NULL,
    precio_uf numeric(10,2) NOT NULL,
    precio_clp numeric(12,2),
    unidad character varying(10) DEFAULT 'UF'::character varying NOT NULL,
    fecha_compra timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_vencimiento timestamp without time zone,
    estado character varying(20) DEFAULT 'PAGADA'::character varying NOT NULL,
    metodo_pago character varying(100),
    ultimos_4_digitos character varying(4),
    iva numeric(10,2),
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT facturas_estado_check CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'PAGADA'::character varying, 'VENCIDA'::character varying, 'ANULADA'::character varying])::text[]))),
    CONSTRAINT facturas_iva_check CHECK ((iva >= (0)::numeric)),
    CONSTRAINT facturas_precio_clp_check CHECK ((precio_clp >= (0)::numeric)),
    CONSTRAINT facturas_precio_uf_check CHECK ((precio_uf >= (0)::numeric))
);


ALTER TABLE siga_comercial.facturas OWNER TO hector;

--
-- Name: TABLE facturas; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON TABLE siga_comercial.facturas IS 'Facturas generadas para suscripciones';


--
-- Name: facturas_id_seq; Type: SEQUENCE; Schema: siga_comercial; Owner: hector
--

CREATE SEQUENCE siga_comercial.facturas_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_comercial.facturas_id_seq OWNER TO hector;

--
-- Name: facturas_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_comercial; Owner: hector
--

ALTER SEQUENCE siga_comercial.facturas_id_seq OWNED BY siga_comercial.facturas.id;


--
-- Name: pagos; Type: TABLE; Schema: siga_comercial; Owner: hector
--

CREATE TABLE siga_comercial.pagos (
    id integer NOT NULL,
    suscripcion_id integer,
    monto numeric(10,2) NOT NULL,
    moneda character varying(10) DEFAULT 'CLP'::character varying,
    metodo_pago character varying(50),
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying NOT NULL,
    referencia_externa character varying(255),
    fecha_pago timestamp without time zone,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pagos_estado_check CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'COMPLETADO'::character varying, 'FALLIDO'::character varying, 'REEMBOLSADO'::character varying])::text[]))),
    CONSTRAINT pagos_monto_check CHECK ((monto >= (0)::numeric))
);


ALTER TABLE siga_comercial.pagos OWNER TO hector;

--
-- Name: TABLE pagos; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON TABLE siga_comercial.pagos IS 'Registro de pagos de suscripciones';


--
-- Name: pagos_id_seq; Type: SEQUENCE; Schema: siga_comercial; Owner: hector
--

CREATE SEQUENCE siga_comercial.pagos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_comercial.pagos_id_seq OWNER TO hector;

--
-- Name: pagos_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_comercial; Owner: hector
--

ALTER SEQUENCE siga_comercial.pagos_id_seq OWNED BY siga_comercial.pagos.id;


--
-- Name: planes; Type: TABLE; Schema: siga_comercial; Owner: hector
--

CREATE TABLE siga_comercial.planes (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text,
    precio_mensual numeric(10,2) NOT NULL,
    precio_anual numeric(10,2),
    limite_bodegas integer DEFAULT 1,
    limite_usuarios integer DEFAULT 1,
    limite_productos integer,
    caracteristicas jsonb,
    activo boolean DEFAULT true,
    orden integer DEFAULT 0,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT planes_limite_bodegas_check CHECK ((limite_bodegas > 0)),
    CONSTRAINT planes_limite_usuarios_check CHECK ((limite_usuarios > 0)),
    CONSTRAINT planes_precio_anual_check CHECK ((precio_anual >= (0)::numeric)),
    CONSTRAINT planes_precio_mensual_check CHECK ((precio_mensual >= (0)::numeric))
);


ALTER TABLE siga_comercial.planes OWNER TO hector;

--
-- Name: TABLE planes; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON TABLE siga_comercial.planes IS 'Planes de suscripción disponibles';


--
-- Name: COLUMN planes.caracteristicas; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON COLUMN siga_comercial.planes.caracteristicas IS 'JSON con características del plan: {"trial_gratis": true, "soporte": "email", etc}';


--
-- Name: planes_id_seq; Type: SEQUENCE; Schema: siga_comercial; Owner: hector
--

CREATE SEQUENCE siga_comercial.planes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_comercial.planes_id_seq OWNER TO hector;

--
-- Name: planes_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_comercial; Owner: hector
--

ALTER SEQUENCE siga_comercial.planes_id_seq OWNED BY siga_comercial.planes.id;


--
-- Name: suscripciones; Type: TABLE; Schema: siga_comercial; Owner: hector
--

CREATE TABLE siga_comercial.suscripciones (
    id integer NOT NULL,
    usuario_id integer,
    plan_id integer,
    fecha_inicio date NOT NULL,
    fecha_fin date,
    estado character varying(20) DEFAULT 'ACTIVA'::character varying NOT NULL,
    periodo character varying(20) DEFAULT 'MENSUAL'::character varying NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT suscripciones_estado_check CHECK (((estado)::text = ANY ((ARRAY['ACTIVA'::character varying, 'SUSPENDIDA'::character varying, 'CANCELADA'::character varying, 'VENCIDA'::character varying])::text[]))),
    CONSTRAINT suscripciones_periodo_check CHECK (((periodo)::text = ANY ((ARRAY['MENSUAL'::character varying, 'ANUAL'::character varying])::text[])))
);


ALTER TABLE siga_comercial.suscripciones OWNER TO hector;

--
-- Name: TABLE suscripciones; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON TABLE siga_comercial.suscripciones IS 'Suscripciones activas de clientes';


--
-- Name: suscripciones_id_seq; Type: SEQUENCE; Schema: siga_comercial; Owner: hector
--

CREATE SEQUENCE siga_comercial.suscripciones_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_comercial.suscripciones_id_seq OWNER TO hector;

--
-- Name: suscripciones_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_comercial; Owner: hector
--

ALTER SEQUENCE siga_comercial.suscripciones_id_seq OWNED BY siga_comercial.suscripciones.id;


--
-- Name: usuarios; Type: TABLE; Schema: siga_comercial; Owner: hector
--

CREATE TABLE siga_comercial.usuarios (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100),
    rut character varying(20),
    telefono character varying(20),
    activo boolean DEFAULT true,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    en_trial boolean DEFAULT false,
    fecha_inicio_trial timestamp without time zone,
    fecha_fin_trial timestamp without time zone,
    rol character varying(20) DEFAULT 'cliente'::character varying,
    plan_id integer,
    nombre_empresa character varying(255),
    CONSTRAINT usuarios_rol_check CHECK (((rol)::text = ANY ((ARRAY['admin'::character varying, 'cliente'::character varying])::text[])))
);


ALTER TABLE siga_comercial.usuarios OWNER TO hector;

--
-- Name: TABLE usuarios; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON TABLE siga_comercial.usuarios IS 'Clientes del portal comercial';


--
-- Name: COLUMN usuarios.en_trial; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON COLUMN siga_comercial.usuarios.en_trial IS 'Indica si el usuario está en período de trial';


--
-- Name: COLUMN usuarios.fecha_inicio_trial; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON COLUMN siga_comercial.usuarios.fecha_inicio_trial IS 'Fecha de inicio del trial (14 días)';


--
-- Name: COLUMN usuarios.fecha_fin_trial; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON COLUMN siga_comercial.usuarios.fecha_fin_trial IS 'Fecha de fin del trial';


--
-- Name: COLUMN usuarios.rol; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON COLUMN siga_comercial.usuarios.rol IS 'Rol del usuario: admin o cliente';


--
-- Name: COLUMN usuarios.plan_id; Type: COMMENT; Schema: siga_comercial; Owner: hector
--

COMMENT ON COLUMN siga_comercial.usuarios.plan_id IS 'ID del plan actual (cache, se sincroniza con suscripción activa)';


--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: siga_comercial; Owner: hector
--

CREATE SEQUENCE siga_comercial.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_comercial.usuarios_id_seq OWNER TO hector;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_comercial; Owner: hector
--

ALTER SEQUENCE siga_comercial.usuarios_id_seq OWNED BY siga_comercial.usuarios.id;


--
-- Name: alertas; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.alertas (
    id integer NOT NULL,
    tipo character varying(50) NOT NULL,
    producto_id integer,
    local_id integer,
    mensaje text NOT NULL,
    leida boolean DEFAULT false,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT alertas_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['STOCK_BAJO'::character varying, 'STOCK_AGOTADO'::character varying, 'VENTA_ALTA'::character varying, 'MOVIMIENTO_SOSPECHOSO'::character varying])::text[])))
);


ALTER TABLE siga_saas.alertas OWNER TO hector;

--
-- Name: TABLE alertas; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.alertas IS 'Alertas y notificaciones del sistema';


--
-- Name: alertas_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.alertas_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.alertas_id_seq OWNER TO hector;

--
-- Name: alertas_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.alertas_id_seq OWNED BY siga_saas.alertas.id;


--
-- Name: carrito_pos; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.carrito_pos (
    id integer NOT NULL,
    usuario_id integer,
    local_id integer,
    producto_id integer,
    cantidad integer NOT NULL,
    precio_unitario numeric(10,2) NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT carrito_pos_cantidad_check CHECK ((cantidad > 0)),
    CONSTRAINT carrito_pos_precio_unitario_check CHECK ((precio_unitario >= (0)::numeric))
);


ALTER TABLE siga_saas.carrito_pos OWNER TO hector;

--
-- Name: TABLE carrito_pos; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.carrito_pos IS 'Carrito temporal del POS (puede limpiarse periódicamente)';


--
-- Name: carrito_pos_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.carrito_pos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.carrito_pos_id_seq OWNER TO hector;

--
-- Name: carrito_pos_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.carrito_pos_id_seq OWNED BY siga_saas.carrito_pos.id;


--
-- Name: categorias; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.categorias (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text,
    activa boolean DEFAULT true,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    usuario_comercial_id integer
);


ALTER TABLE siga_saas.categorias OWNER TO hector;

--
-- Name: TABLE categorias; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.categorias IS 'Categorías de productos. Separadas por empresa mediante usuario_comercial_id. Todas las categorías deben tener usuario_comercial_id asignado.';


--
-- Name: COLUMN categorias.usuario_comercial_id; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.categorias.usuario_comercial_id IS 'ID del usuario comercial (dueño) al que pertenece esta categoría';


--
-- Name: categorias_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.categorias_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.categorias_id_seq OWNER TO hector;

--
-- Name: categorias_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.categorias_id_seq OWNED BY siga_saas.categorias.id;


--
-- Name: detalles_venta; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.detalles_venta (
    id integer NOT NULL,
    venta_id integer,
    producto_id integer,
    cantidad integer NOT NULL,
    precio_unitario numeric(10,2) NOT NULL,
    subtotal numeric(10,2) NOT NULL,
    CONSTRAINT detalles_venta_cantidad_check CHECK ((cantidad > 0)),
    CONSTRAINT detalles_venta_precio_unitario_check CHECK ((precio_unitario >= (0)::numeric)),
    CONSTRAINT detalles_venta_subtotal_check CHECK ((subtotal >= (0)::numeric))
);


ALTER TABLE siga_saas.detalles_venta OWNER TO hector;

--
-- Name: TABLE detalles_venta; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.detalles_venta IS 'Detalles de cada venta (productos vendidos)';


--
-- Name: detalles_venta_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.detalles_venta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.detalles_venta_id_seq OWNER TO hector;

--
-- Name: detalles_venta_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.detalles_venta_id_seq OWNED BY siga_saas.detalles_venta.id;


--
-- Name: locales; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.locales (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    direccion text,
    ciudad character varying(100),
    activo boolean DEFAULT true,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    usuario_comercial_id integer
);


ALTER TABLE siga_saas.locales OWNER TO hector;

--
-- Name: TABLE locales; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.locales IS 'Locales/bodegas. Separados por empresa mediante usuario_comercial_id. Todos los locales deben tener usuario_comercial_id asignado.';


--
-- Name: COLUMN locales.usuario_comercial_id; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.locales.usuario_comercial_id IS 'ID del usuario comercial (dueño) al que pertenece este local';


--
-- Name: locales_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.locales_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.locales_id_seq OWNER TO hector;

--
-- Name: locales_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.locales_id_seq OWNED BY siga_saas.locales.id;


--
-- Name: metodos_pago; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.metodos_pago (
    id integer NOT NULL,
    nombre character varying(50) NOT NULL,
    activo boolean DEFAULT true,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE siga_saas.metodos_pago OWNER TO hector;

--
-- Name: TABLE metodos_pago; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.metodos_pago IS 'Métodos de pago disponibles (EFECTIVO, TARJETA_DEBITO, TARJETA_CREDITO, TRANSFERENCIA)';


--
-- Name: metodos_pago_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.metodos_pago_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.metodos_pago_id_seq OWNER TO hector;

--
-- Name: metodos_pago_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.metodos_pago_id_seq OWNED BY siga_saas.metodos_pago.id;


--
-- Name: movimientos; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.movimientos (
    id integer NOT NULL,
    producto_id integer,
    local_id integer,
    tipo character varying(20) NOT NULL,
    cantidad integer NOT NULL,
    cantidad_anterior integer,
    cantidad_nueva integer,
    usuario_id integer,
    venta_id integer,
    observaciones text,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT movimientos_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['ENTRADA'::character varying, 'SALIDA'::character varying, 'VENTA'::character varying, 'AJUSTE'::character varying, 'TRASLADO'::character varying])::text[])))
);


ALTER TABLE siga_saas.movimientos OWNER TO hector;

--
-- Name: TABLE movimientos; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.movimientos IS 'Historial de movimientos de stock';


--
-- Name: movimientos_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.movimientos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.movimientos_id_seq OWNER TO hector;

--
-- Name: movimientos_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.movimientos_id_seq OWNED BY siga_saas.movimientos.id;


--
-- Name: permisos; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.permisos (
    id integer NOT NULL,
    codigo character varying(50) NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text,
    categoria character varying(50) NOT NULL,
    activo boolean DEFAULT true,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE siga_saas.permisos OWNER TO hector;

--
-- Name: TABLE permisos; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.permisos IS 'Catálogo de permisos del sistema (operaciones disponibles)';


--
-- Name: COLUMN permisos.codigo; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.permisos.codigo IS 'Código único del permiso (ej: PRODUCTOS_CREAR)';


--
-- Name: COLUMN permisos.categoria; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.permisos.categoria IS 'Categoría del permiso (PRODUCTOS, STOCK, VENTAS, etc.)';


--
-- Name: permisos_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.permisos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.permisos_id_seq OWNER TO hector;

--
-- Name: permisos_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.permisos_id_seq OWNED BY siga_saas.permisos.id;


--
-- Name: productos; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.productos (
    id integer NOT NULL,
    nombre character varying(200) NOT NULL,
    descripcion text,
    categoria_id integer,
    codigo_barras character varying(50),
    precio_unitario numeric(10,2),
    activo boolean DEFAULT true,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    usuario_comercial_id integer
);


ALTER TABLE siga_saas.productos OWNER TO hector;

--
-- Name: TABLE productos; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.productos IS 'Productos del sistema. Separados por empresa mediante usuario_comercial_id. Todos los productos deben tener usuario_comercial_id asignado.';


--
-- Name: COLUMN productos.usuario_comercial_id; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.productos.usuario_comercial_id IS 'ID del usuario comercial (dueño) al que pertenece este producto';


--
-- Name: productos_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.productos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.productos_id_seq OWNER TO hector;

--
-- Name: productos_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.productos_id_seq OWNED BY siga_saas.productos.id;


--
-- Name: roles_permisos; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.roles_permisos (
    rol character varying(20) NOT NULL,
    permiso_id integer NOT NULL,
    CONSTRAINT roles_permisos_rol_check CHECK (((rol)::text = ANY ((ARRAY['ADMINISTRADOR'::character varying, 'OPERADOR'::character varying, 'CAJERO'::character varying])::text[])))
);


ALTER TABLE siga_saas.roles_permisos OWNER TO hector;

--
-- Name: TABLE roles_permisos; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.roles_permisos IS 'Permisos por defecto de cada rol (plantillas base)';


--
-- Name: stock; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.stock (
    id integer NOT NULL,
    producto_id integer,
    local_id integer,
    cantidad integer DEFAULT 0 NOT NULL,
    cantidad_minima integer DEFAULT 0,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT stock_cantidad_check CHECK ((cantidad >= 0)),
    CONSTRAINT stock_cantidad_minima_check CHECK ((cantidad_minima >= 0))
);


ALTER TABLE siga_saas.stock OWNER TO hector;

--
-- Name: TABLE stock; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.stock IS 'Stock por producto y local';


--
-- Name: stock_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.stock_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.stock_id_seq OWNER TO hector;

--
-- Name: stock_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.stock_id_seq OWNED BY siga_saas.stock.id;


--
-- Name: transacciones_pos; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.transacciones_pos (
    id integer NOT NULL,
    venta_id integer,
    turno_caja_id integer,
    metodo_pago_id integer,
    monto numeric(10,2) NOT NULL,
    cambio numeric(10,2) DEFAULT 0,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    estado character varying(20) DEFAULT 'COMPLETADA'::character varying NOT NULL,
    CONSTRAINT transacciones_pos_cambio_check CHECK ((cambio >= (0)::numeric)),
    CONSTRAINT transacciones_pos_estado_check CHECK (((estado)::text = ANY ((ARRAY['COMPLETADA'::character varying, 'CANCELADA'::character varying, 'REEMBOLSADA'::character varying])::text[]))),
    CONSTRAINT transacciones_pos_monto_check CHECK ((monto >= (0)::numeric))
);


ALTER TABLE siga_saas.transacciones_pos OWNER TO hector;

--
-- Name: TABLE transacciones_pos; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.transacciones_pos IS 'Transacciones del punto de venta';


--
-- Name: transacciones_pos_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.transacciones_pos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.transacciones_pos_id_seq OWNER TO hector;

--
-- Name: transacciones_pos_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.transacciones_pos_id_seq OWNED BY siga_saas.transacciones_pos.id;


--
-- Name: turnos_caja; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.turnos_caja (
    id integer NOT NULL,
    local_id integer,
    usuario_id integer,
    fecha_apertura timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre timestamp without time zone,
    monto_inicial numeric(10,2) DEFAULT 0 NOT NULL,
    monto_final numeric(10,2),
    estado character varying(20) DEFAULT 'ABIERTO'::character varying NOT NULL,
    CONSTRAINT turnos_caja_estado_check CHECK (((estado)::text = ANY ((ARRAY['ABIERTO'::character varying, 'CERRADO'::character varying])::text[]))),
    CONSTRAINT turnos_caja_monto_final_check CHECK ((monto_final >= (0)::numeric)),
    CONSTRAINT turnos_caja_monto_inicial_check CHECK ((monto_inicial >= (0)::numeric))
);


ALTER TABLE siga_saas.turnos_caja OWNER TO hector;

--
-- Name: TABLE turnos_caja; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.turnos_caja IS 'Turnos de caja por local y usuario';


--
-- Name: turnos_caja_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.turnos_caja_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.turnos_caja_id_seq OWNER TO hector;

--
-- Name: turnos_caja_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.turnos_caja_id_seq OWNED BY siga_saas.turnos_caja.id;


--
-- Name: usuarios; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.usuarios (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100),
    rol character varying(20) NOT NULL,
    activo boolean DEFAULT true,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    usuario_comercial_id integer,
    CONSTRAINT usuarios_rol_check CHECK (((rol)::text = ANY ((ARRAY['ADMINISTRADOR'::character varying, 'OPERADOR'::character varying, 'CAJERO'::character varying])::text[])))
);


ALTER TABLE siga_saas.usuarios OWNER TO hector;

--
-- Name: TABLE usuarios; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.usuarios IS 'Usuarios operativos del sistema. Ahora separados por empresa mediante usuario_comercial_id.';


--
-- Name: COLUMN usuarios.rol; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.usuarios.rol IS 'Rol del usuario: ADMINISTRADOR, OPERADOR o CAJERO';


--
-- Name: COLUMN usuarios.usuario_comercial_id; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.usuarios.usuario_comercial_id IS 'ID del usuario comercial (dueño) al que pertenece este usuario operativo. NULL para usuarios legacy.';


--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.usuarios_id_seq OWNER TO hector;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.usuarios_id_seq OWNED BY siga_saas.usuarios.id;


--
-- Name: usuarios_locales; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.usuarios_locales (
    usuario_id integer NOT NULL,
    local_id integer NOT NULL
);


ALTER TABLE siga_saas.usuarios_locales OWNER TO hector;

--
-- Name: TABLE usuarios_locales; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.usuarios_locales IS 'Relación entre usuarios y locales asignados (OPERADOR solo ve sus locales)';


--
-- Name: usuarios_permisos; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.usuarios_permisos (
    usuario_id integer NOT NULL,
    permiso_id integer NOT NULL,
    fecha_asignacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    asignado_por integer
);


ALTER TABLE siga_saas.usuarios_permisos OWNER TO hector;

--
-- Name: TABLE usuarios_permisos; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.usuarios_permisos IS 'Permisos adicionales asignados a usuarios específicos (más allá de su rol base)';


--
-- Name: COLUMN usuarios_permisos.asignado_por; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.usuarios_permisos.asignado_por IS 'ID del administrador que asignó el permiso';


--
-- Name: ventas; Type: TABLE; Schema: siga_saas; Owner: hector
--

CREATE TABLE siga_saas.ventas (
    id integer NOT NULL,
    local_id integer,
    usuario_id integer,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    total numeric(10,2) NOT NULL,
    estado character varying(20) DEFAULT 'COMPLETADA'::character varying NOT NULL,
    observaciones text,
    usuario_comercial_id integer,
    CONSTRAINT ventas_estado_check CHECK (((estado)::text = ANY ((ARRAY['COMPLETADA'::character varying, 'CANCELADA'::character varying, 'PENDIENTE'::character varying])::text[]))),
    CONSTRAINT ventas_total_check CHECK ((total >= (0)::numeric))
);


ALTER TABLE siga_saas.ventas OWNER TO hector;

--
-- Name: TABLE ventas; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON TABLE siga_saas.ventas IS 'Ventas del sistema. Separadas por empresa mediante usuario_comercial_id. Todas las ventas deben tener usuario_comercial_id asignado.';


--
-- Name: COLUMN ventas.usuario_comercial_id; Type: COMMENT; Schema: siga_saas; Owner: hector
--

COMMENT ON COLUMN siga_saas.ventas.usuario_comercial_id IS 'ID del usuario comercial (dueño) al que pertenece esta venta';


--
-- Name: ventas_id_seq; Type: SEQUENCE; Schema: siga_saas; Owner: hector
--

CREATE SEQUENCE siga_saas.ventas_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE siga_saas.ventas_id_seq OWNER TO hector;

--
-- Name: ventas_id_seq; Type: SEQUENCE OWNED BY; Schema: siga_saas; Owner: hector
--

ALTER SEQUENCE siga_saas.ventas_id_seq OWNED BY siga_saas.ventas.id;


--
-- Name: carritos id; Type: DEFAULT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.carritos ALTER COLUMN id SET DEFAULT nextval('siga_comercial.carritos_id_seq'::regclass);


--
-- Name: facturas id; Type: DEFAULT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.facturas ALTER COLUMN id SET DEFAULT nextval('siga_comercial.facturas_id_seq'::regclass);


--
-- Name: pagos id; Type: DEFAULT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.pagos ALTER COLUMN id SET DEFAULT nextval('siga_comercial.pagos_id_seq'::regclass);


--
-- Name: planes id; Type: DEFAULT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.planes ALTER COLUMN id SET DEFAULT nextval('siga_comercial.planes_id_seq'::regclass);


--
-- Name: suscripciones id; Type: DEFAULT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.suscripciones ALTER COLUMN id SET DEFAULT nextval('siga_comercial.suscripciones_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.usuarios ALTER COLUMN id SET DEFAULT nextval('siga_comercial.usuarios_id_seq'::regclass);


--
-- Name: alertas id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.alertas ALTER COLUMN id SET DEFAULT nextval('siga_saas.alertas_id_seq'::regclass);


--
-- Name: carrito_pos id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.carrito_pos ALTER COLUMN id SET DEFAULT nextval('siga_saas.carrito_pos_id_seq'::regclass);


--
-- Name: categorias id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.categorias ALTER COLUMN id SET DEFAULT nextval('siga_saas.categorias_id_seq'::regclass);


--
-- Name: detalles_venta id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.detalles_venta ALTER COLUMN id SET DEFAULT nextval('siga_saas.detalles_venta_id_seq'::regclass);


--
-- Name: locales id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.locales ALTER COLUMN id SET DEFAULT nextval('siga_saas.locales_id_seq'::regclass);


--
-- Name: metodos_pago id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.metodos_pago ALTER COLUMN id SET DEFAULT nextval('siga_saas.metodos_pago_id_seq'::regclass);


--
-- Name: movimientos id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.movimientos ALTER COLUMN id SET DEFAULT nextval('siga_saas.movimientos_id_seq'::regclass);


--
-- Name: permisos id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.permisos ALTER COLUMN id SET DEFAULT nextval('siga_saas.permisos_id_seq'::regclass);


--
-- Name: productos id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.productos ALTER COLUMN id SET DEFAULT nextval('siga_saas.productos_id_seq'::regclass);


--
-- Name: stock id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.stock ALTER COLUMN id SET DEFAULT nextval('siga_saas.stock_id_seq'::regclass);


--
-- Name: transacciones_pos id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.transacciones_pos ALTER COLUMN id SET DEFAULT nextval('siga_saas.transacciones_pos_id_seq'::regclass);


--
-- Name: turnos_caja id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.turnos_caja ALTER COLUMN id SET DEFAULT nextval('siga_saas.turnos_caja_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios ALTER COLUMN id SET DEFAULT nextval('siga_saas.usuarios_id_seq'::regclass);


--
-- Name: ventas id; Type: DEFAULT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.ventas ALTER COLUMN id SET DEFAULT nextval('siga_saas.ventas_id_seq'::regclass);


--
-- Name: carritos carritos_pkey; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.carritos
    ADD CONSTRAINT carritos_pkey PRIMARY KEY (id);


--
-- Name: carritos carritos_usuario_id_key; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.carritos
    ADD CONSTRAINT carritos_usuario_id_key UNIQUE (usuario_id);


--
-- Name: facturas facturas_numero_factura_key; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.facturas
    ADD CONSTRAINT facturas_numero_factura_key UNIQUE (numero_factura);


--
-- Name: facturas facturas_pkey; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.facturas
    ADD CONSTRAINT facturas_pkey PRIMARY KEY (id);


--
-- Name: pagos pagos_pkey; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.pagos
    ADD CONSTRAINT pagos_pkey PRIMARY KEY (id);


--
-- Name: planes planes_nombre_key; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.planes
    ADD CONSTRAINT planes_nombre_key UNIQUE (nombre);


--
-- Name: planes planes_pkey; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.planes
    ADD CONSTRAINT planes_pkey PRIMARY KEY (id);


--
-- Name: suscripciones suscripciones_pkey; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.suscripciones
    ADD CONSTRAINT suscripciones_pkey PRIMARY KEY (id);


--
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: alertas alertas_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.alertas
    ADD CONSTRAINT alertas_pkey PRIMARY KEY (id);


--
-- Name: carrito_pos carrito_pos_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.carrito_pos
    ADD CONSTRAINT carrito_pos_pkey PRIMARY KEY (id);


--
-- Name: categorias categorias_nombre_key; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.categorias
    ADD CONSTRAINT categorias_nombre_key UNIQUE (nombre);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: detalles_venta detalles_venta_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.detalles_venta
    ADD CONSTRAINT detalles_venta_pkey PRIMARY KEY (id);


--
-- Name: locales locales_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.locales
    ADD CONSTRAINT locales_pkey PRIMARY KEY (id);


--
-- Name: metodos_pago metodos_pago_nombre_key; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.metodos_pago
    ADD CONSTRAINT metodos_pago_nombre_key UNIQUE (nombre);


--
-- Name: metodos_pago metodos_pago_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.metodos_pago
    ADD CONSTRAINT metodos_pago_pkey PRIMARY KEY (id);


--
-- Name: movimientos movimientos_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.movimientos
    ADD CONSTRAINT movimientos_pkey PRIMARY KEY (id);


--
-- Name: permisos permisos_codigo_key; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.permisos
    ADD CONSTRAINT permisos_codigo_key UNIQUE (codigo);


--
-- Name: permisos permisos_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.permisos
    ADD CONSTRAINT permisos_pkey PRIMARY KEY (id);


--
-- Name: productos productos_codigo_barras_key; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.productos
    ADD CONSTRAINT productos_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: productos productos_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.productos
    ADD CONSTRAINT productos_pkey PRIMARY KEY (id);


--
-- Name: roles_permisos roles_permisos_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.roles_permisos
    ADD CONSTRAINT roles_permisos_pkey PRIMARY KEY (rol, permiso_id);


--
-- Name: stock stock_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.stock
    ADD CONSTRAINT stock_pkey PRIMARY KEY (id);


--
-- Name: stock stock_producto_id_local_id_key; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.stock
    ADD CONSTRAINT stock_producto_id_local_id_key UNIQUE (producto_id, local_id);


--
-- Name: transacciones_pos transacciones_pos_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.transacciones_pos
    ADD CONSTRAINT transacciones_pos_pkey PRIMARY KEY (id);


--
-- Name: turnos_caja turnos_caja_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.turnos_caja
    ADD CONSTRAINT turnos_caja_pkey PRIMARY KEY (id);


--
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- Name: usuarios_locales usuarios_locales_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios_locales
    ADD CONSTRAINT usuarios_locales_pkey PRIMARY KEY (usuario_id, local_id);


--
-- Name: usuarios_permisos usuarios_permisos_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios_permisos
    ADD CONSTRAINT usuarios_permisos_pkey PRIMARY KEY (usuario_id, permiso_id);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: ventas ventas_pkey; Type: CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.ventas
    ADD CONSTRAINT ventas_pkey PRIMARY KEY (id);


--
-- Name: idx_carritos_usuario; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_carritos_usuario ON siga_comercial.carritos USING btree (usuario_id);


--
-- Name: idx_facturas_estado; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_facturas_estado ON siga_comercial.facturas USING btree (estado);


--
-- Name: idx_facturas_pago; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_facturas_pago ON siga_comercial.facturas USING btree (pago_id);


--
-- Name: idx_facturas_plan_id; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_facturas_plan_id ON siga_comercial.facturas USING btree (plan_id);


--
-- Name: idx_facturas_suscripcion; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_facturas_suscripcion ON siga_comercial.facturas USING btree (suscripcion_id);


--
-- Name: idx_facturas_usuario_id; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_facturas_usuario_id ON siga_comercial.facturas USING btree (usuario_id);


--
-- Name: idx_pagos_estado; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_pagos_estado ON siga_comercial.pagos USING btree (estado);


--
-- Name: idx_pagos_fecha; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_pagos_fecha ON siga_comercial.pagos USING btree (fecha_pago);


--
-- Name: idx_pagos_suscripcion; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_pagos_suscripcion ON siga_comercial.pagos USING btree (suscripcion_id);


--
-- Name: idx_suscripciones_estado; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_suscripciones_estado ON siga_comercial.suscripciones USING btree (estado);


--
-- Name: idx_suscripciones_plan; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_suscripciones_plan ON siga_comercial.suscripciones USING btree (plan_id);


--
-- Name: idx_suscripciones_usuario; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_suscripciones_usuario ON siga_comercial.suscripciones USING btree (usuario_id);


--
-- Name: idx_usuarios_plan_id; Type: INDEX; Schema: siga_comercial; Owner: hector
--

CREATE INDEX idx_usuarios_plan_id ON siga_comercial.usuarios USING btree (plan_id);


--
-- Name: idx_alertas_leida; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_alertas_leida ON siga_saas.alertas USING btree (leida);


--
-- Name: idx_alertas_local; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_alertas_local ON siga_saas.alertas USING btree (local_id);


--
-- Name: idx_alertas_producto; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_alertas_producto ON siga_saas.alertas USING btree (producto_id);


--
-- Name: idx_carrito_local; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_carrito_local ON siga_saas.carrito_pos USING btree (local_id);


--
-- Name: idx_carrito_usuario; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_carrito_usuario ON siga_saas.carrito_pos USING btree (usuario_id);


--
-- Name: idx_categorias_comercial_id; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_categorias_comercial_id ON siga_saas.categorias USING btree (usuario_comercial_id);


--
-- Name: idx_detalles_producto; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_detalles_producto ON siga_saas.detalles_venta USING btree (producto_id);


--
-- Name: idx_detalles_venta; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_detalles_venta ON siga_saas.detalles_venta USING btree (venta_id);


--
-- Name: idx_locales_comercial_id; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_locales_comercial_id ON siga_saas.locales USING btree (usuario_comercial_id);


--
-- Name: idx_movimientos_fecha; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_movimientos_fecha ON siga_saas.movimientos USING btree (fecha);


--
-- Name: idx_movimientos_local; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_movimientos_local ON siga_saas.movimientos USING btree (local_id);


--
-- Name: idx_movimientos_producto; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_movimientos_producto ON siga_saas.movimientos USING btree (producto_id);


--
-- Name: idx_permisos_categoria; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_permisos_categoria ON siga_saas.permisos USING btree (categoria);


--
-- Name: idx_permisos_codigo; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_permisos_codigo ON siga_saas.permisos USING btree (codigo);


--
-- Name: idx_productos_comercial_id; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_productos_comercial_id ON siga_saas.productos USING btree (usuario_comercial_id);


--
-- Name: idx_roles_permisos_rol; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_roles_permisos_rol ON siga_saas.roles_permisos USING btree (rol);


--
-- Name: idx_stock_local; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_stock_local ON siga_saas.stock USING btree (local_id);


--
-- Name: idx_stock_producto; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_stock_producto ON siga_saas.stock USING btree (producto_id);


--
-- Name: idx_transacciones_turno; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_transacciones_turno ON siga_saas.transacciones_pos USING btree (turno_caja_id);


--
-- Name: idx_transacciones_venta; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_transacciones_venta ON siga_saas.transacciones_pos USING btree (venta_id);


--
-- Name: idx_turnos_estado; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_turnos_estado ON siga_saas.turnos_caja USING btree (estado);


--
-- Name: idx_turnos_local; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_turnos_local ON siga_saas.turnos_caja USING btree (local_id);


--
-- Name: idx_turnos_usuario; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_turnos_usuario ON siga_saas.turnos_caja USING btree (usuario_id);


--
-- Name: idx_usuarios_comercial_id; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_usuarios_comercial_id ON siga_saas.usuarios USING btree (usuario_comercial_id);


--
-- Name: idx_usuarios_permisos_permiso; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_usuarios_permisos_permiso ON siga_saas.usuarios_permisos USING btree (permiso_id);


--
-- Name: idx_usuarios_permisos_usuario; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_usuarios_permisos_usuario ON siga_saas.usuarios_permisos USING btree (usuario_id);


--
-- Name: idx_ventas_comercial_id; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_ventas_comercial_id ON siga_saas.ventas USING btree (usuario_comercial_id);


--
-- Name: idx_ventas_fecha; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_ventas_fecha ON siga_saas.ventas USING btree (fecha);


--
-- Name: idx_ventas_local; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_ventas_local ON siga_saas.ventas USING btree (local_id);


--
-- Name: idx_ventas_usuario; Type: INDEX; Schema: siga_saas; Owner: hector
--

CREATE INDEX idx_ventas_usuario ON siga_saas.ventas USING btree (usuario_id);


--
-- Name: carritos carritos_plan_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.carritos
    ADD CONSTRAINT carritos_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES siga_comercial.planes(id);


--
-- Name: carritos carritos_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.carritos
    ADD CONSTRAINT carritos_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: facturas facturas_pago_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.facturas
    ADD CONSTRAINT facturas_pago_id_fkey FOREIGN KEY (pago_id) REFERENCES siga_comercial.pagos(id) ON DELETE SET NULL;


--
-- Name: facturas facturas_plan_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.facturas
    ADD CONSTRAINT facturas_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES siga_comercial.planes(id);


--
-- Name: facturas facturas_suscripcion_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.facturas
    ADD CONSTRAINT facturas_suscripcion_id_fkey FOREIGN KEY (suscripcion_id) REFERENCES siga_comercial.suscripciones(id) ON DELETE SET NULL;


--
-- Name: facturas facturas_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.facturas
    ADD CONSTRAINT facturas_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: pagos pagos_suscripcion_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.pagos
    ADD CONSTRAINT pagos_suscripcion_id_fkey FOREIGN KEY (suscripcion_id) REFERENCES siga_comercial.suscripciones(id) ON DELETE CASCADE;


--
-- Name: suscripciones suscripciones_plan_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.suscripciones
    ADD CONSTRAINT suscripciones_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES siga_comercial.planes(id);


--
-- Name: suscripciones suscripciones_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.suscripciones
    ADD CONSTRAINT suscripciones_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: usuarios usuarios_plan_id_fkey; Type: FK CONSTRAINT; Schema: siga_comercial; Owner: hector
--

ALTER TABLE ONLY siga_comercial.usuarios
    ADD CONSTRAINT usuarios_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES siga_comercial.planes(id);


--
-- Name: alertas alertas_local_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.alertas
    ADD CONSTRAINT alertas_local_id_fkey FOREIGN KEY (local_id) REFERENCES siga_saas.locales(id) ON DELETE CASCADE;


--
-- Name: alertas alertas_producto_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.alertas
    ADD CONSTRAINT alertas_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES siga_saas.productos(id) ON DELETE CASCADE;


--
-- Name: carrito_pos carrito_pos_local_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.carrito_pos
    ADD CONSTRAINT carrito_pos_local_id_fkey FOREIGN KEY (local_id) REFERENCES siga_saas.locales(id) ON DELETE CASCADE;


--
-- Name: carrito_pos carrito_pos_producto_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.carrito_pos
    ADD CONSTRAINT carrito_pos_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES siga_saas.productos(id) ON DELETE CASCADE;


--
-- Name: carrito_pos carrito_pos_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.carrito_pos
    ADD CONSTRAINT carrito_pos_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_saas.usuarios(id) ON DELETE CASCADE;


--
-- Name: categorias categorias_usuario_comercial_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.categorias
    ADD CONSTRAINT categorias_usuario_comercial_id_fkey FOREIGN KEY (usuario_comercial_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: detalles_venta detalles_venta_producto_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.detalles_venta
    ADD CONSTRAINT detalles_venta_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES siga_saas.productos(id) ON DELETE CASCADE;


--
-- Name: detalles_venta detalles_venta_venta_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.detalles_venta
    ADD CONSTRAINT detalles_venta_venta_id_fkey FOREIGN KEY (venta_id) REFERENCES siga_saas.ventas(id) ON DELETE CASCADE;


--
-- Name: movimientos fk_movimientos_venta; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.movimientos
    ADD CONSTRAINT fk_movimientos_venta FOREIGN KEY (venta_id) REFERENCES siga_saas.ventas(id) ON DELETE SET NULL;


--
-- Name: locales locales_usuario_comercial_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.locales
    ADD CONSTRAINT locales_usuario_comercial_id_fkey FOREIGN KEY (usuario_comercial_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: movimientos movimientos_local_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.movimientos
    ADD CONSTRAINT movimientos_local_id_fkey FOREIGN KEY (local_id) REFERENCES siga_saas.locales(id) ON DELETE CASCADE;


--
-- Name: movimientos movimientos_producto_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.movimientos
    ADD CONSTRAINT movimientos_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES siga_saas.productos(id) ON DELETE CASCADE;


--
-- Name: movimientos movimientos_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.movimientos
    ADD CONSTRAINT movimientos_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_saas.usuarios(id);


--
-- Name: productos productos_categoria_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.productos
    ADD CONSTRAINT productos_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES siga_saas.categorias(id);


--
-- Name: productos productos_usuario_comercial_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.productos
    ADD CONSTRAINT productos_usuario_comercial_id_fkey FOREIGN KEY (usuario_comercial_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: roles_permisos roles_permisos_permiso_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.roles_permisos
    ADD CONSTRAINT roles_permisos_permiso_id_fkey FOREIGN KEY (permiso_id) REFERENCES siga_saas.permisos(id) ON DELETE CASCADE;


--
-- Name: stock stock_local_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.stock
    ADD CONSTRAINT stock_local_id_fkey FOREIGN KEY (local_id) REFERENCES siga_saas.locales(id) ON DELETE CASCADE;


--
-- Name: stock stock_producto_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.stock
    ADD CONSTRAINT stock_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES siga_saas.productos(id) ON DELETE CASCADE;


--
-- Name: transacciones_pos transacciones_pos_metodo_pago_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.transacciones_pos
    ADD CONSTRAINT transacciones_pos_metodo_pago_id_fkey FOREIGN KEY (metodo_pago_id) REFERENCES siga_saas.metodos_pago(id);


--
-- Name: transacciones_pos transacciones_pos_turno_caja_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.transacciones_pos
    ADD CONSTRAINT transacciones_pos_turno_caja_id_fkey FOREIGN KEY (turno_caja_id) REFERENCES siga_saas.turnos_caja(id) ON DELETE CASCADE;


--
-- Name: transacciones_pos transacciones_pos_venta_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.transacciones_pos
    ADD CONSTRAINT transacciones_pos_venta_id_fkey FOREIGN KEY (venta_id) REFERENCES siga_saas.ventas(id) ON DELETE CASCADE;


--
-- Name: turnos_caja turnos_caja_local_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.turnos_caja
    ADD CONSTRAINT turnos_caja_local_id_fkey FOREIGN KEY (local_id) REFERENCES siga_saas.locales(id) ON DELETE CASCADE;


--
-- Name: turnos_caja turnos_caja_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.turnos_caja
    ADD CONSTRAINT turnos_caja_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_saas.usuarios(id);


--
-- Name: usuarios_locales usuarios_locales_local_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios_locales
    ADD CONSTRAINT usuarios_locales_local_id_fkey FOREIGN KEY (local_id) REFERENCES siga_saas.locales(id) ON DELETE CASCADE;


--
-- Name: usuarios_locales usuarios_locales_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios_locales
    ADD CONSTRAINT usuarios_locales_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_saas.usuarios(id) ON DELETE CASCADE;


--
-- Name: usuarios_permisos usuarios_permisos_asignado_por_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios_permisos
    ADD CONSTRAINT usuarios_permisos_asignado_por_fkey FOREIGN KEY (asignado_por) REFERENCES siga_saas.usuarios(id);


--
-- Name: usuarios_permisos usuarios_permisos_permiso_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios_permisos
    ADD CONSTRAINT usuarios_permisos_permiso_id_fkey FOREIGN KEY (permiso_id) REFERENCES siga_saas.permisos(id) ON DELETE CASCADE;


--
-- Name: usuarios_permisos usuarios_permisos_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios_permisos
    ADD CONSTRAINT usuarios_permisos_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_saas.usuarios(id) ON DELETE CASCADE;


--
-- Name: usuarios usuarios_usuario_comercial_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.usuarios
    ADD CONSTRAINT usuarios_usuario_comercial_id_fkey FOREIGN KEY (usuario_comercial_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: ventas ventas_local_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.ventas
    ADD CONSTRAINT ventas_local_id_fkey FOREIGN KEY (local_id) REFERENCES siga_saas.locales(id) ON DELETE CASCADE;


--
-- Name: ventas ventas_usuario_comercial_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.ventas
    ADD CONSTRAINT ventas_usuario_comercial_id_fkey FOREIGN KEY (usuario_comercial_id) REFERENCES siga_comercial.usuarios(id) ON DELETE CASCADE;


--
-- Name: ventas ventas_usuario_id_fkey; Type: FK CONSTRAINT; Schema: siga_saas; Owner: hector
--

ALTER TABLE ONLY siga_saas.ventas
    ADD CONSTRAINT ventas_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES siga_saas.usuarios(id);


--
-- PostgreSQL database dump complete
--

\unrestrict EXx27ZUJvHmLzM6k2iiNHJsehcPBqahSYgbEBZOV6gFFOBZ5wC9Rce9DNvGfcvl

