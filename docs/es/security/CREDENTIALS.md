# 🔐 Credenciales de Base de Datos (Entorno de Desarrollo)

Este documento contiene las credenciales necesarias para conectar los microservicios, clientes de escritorio (TablePlus, pgAdmin) y herramientas de BI al entorno local de SIGA.

> [!WARNING]
> Estas credenciales son solo para **DESARROLLO LOCAL**. Nunca utilices estas mismas claves en entornos de producción (GCP/Supabase).

---

## 🐘 PostgreSQL (Motor de Datos)

| Parámetro | Valor | Notas |
|-----------|-------|-------|
| **Host (Externo)** | `localhost` o `127.0.0.1` | Para TablePlus, DBeaver, etc. |
| **Host (Internal Docker)** | `siga-postgres` | Usar dentro de la red de Docker (ej. pgAdmin Web) |
| **Puerto** | `5432` | Estándar de PostgreSQL |
| **Base de Datos** | `siga_db` | Base de datos principal |
| **Usuario Admin** | `siga_admin` | Superusuario de desarrollo |
| **Password** | `siga_dev_2026` | Definido en `docker-compose.yml` |

### 📂 Esquemas Disponibles
Para conectar un microservicio específico, añade `?currentSchema=NOMBRE_SCHEMA` a la URL de conexión:
- `siga_auth` (Servicio de Autenticación)
- `siga_inventario` (Servicio de Inventario)
- `siga_ventas` (Servicio POS)
- `siga_comercial` (Servicio Backend/Billing)
- `siga_agente` (Servicio IA / Vector Store)

---

## 🌐 pgAdmin 4 (Interfaz Web)

Acceso vía navegador: [http://localhost:5050](http://localhost:5050)

| Credencial | Valor |
|------------|-------|
| **Usuario** | `admin@siga.cl` |
| **Password** | `admin` |

---

## 🛠 Cadenas de Conexión (Ejemplos)

### Kotlin / Java (JDBC)
```
jdbc:postgresql://localhost:5432/siga_db?currentSchema=siga_inventario
```

### Python (SQLAlchemy / Agente IA)
```
postgresql://siga_admin:siga_dev_2026@localhost:5432/siga_db?options=-csearch_path%3Dsiga_agente
```

### Shell (psql)
```bash
docker exec -it siga-postgres psql -U siga_admin -d siga_db
```
