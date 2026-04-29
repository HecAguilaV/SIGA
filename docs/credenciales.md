# Credenciales del Proyecto SIGA (Entorno de Desarrollo)

Este documento centraliza los accesos técnicos configurados en la arquitectura de microservicios.

> [!IMPORTANT]
> Estas credenciales son **solo para desarrollo local**. Deben ser rotadas mediante variables de entorno en producción.

## 🗄️ Infraestructura (Docker)

| Servicio | URL Local | Usuario | Contraseña |
| :--- | :--- | :--- | :--- |
| **PostgreSQL** | `localhost:5432` | `admin` | `password123` |
| **pgAdmin4** | [http://localhost:8081](http://localhost:8081) | `admin@siga.cl` | `admin` |
| **Eureka Server** | [http://localhost:8761](http://localhost:8761) | *(Sin Auth)* | *(Sin Auth)* |

## 🔐 Seguridad (JWT)

| Parámetro | Valor |
| :--- | :--- |
| **Secret Key** | `default-secret-key-too-long-to-be-secure-enough-probably-123456` |
| **Algoritmo** | HMAC256 |
| **Vigencia Token** | 24 Horas |

## 👥 Usuarios de Aplicación

| Rol | Método de Acceso | Estado |
| :--- | :--- | :--- |
| **Dueño (Comercial)** | Google SSO (OIDC) | Pendiente de Implementación |
| **Empleado (SaaS)** | Usuario / Password | Pendiente de Implementación |

> [!NOTE]
> La Base de Datos utiliza el nombre `siga_db` y contiene los esquemas aislados `siga_saas` y `siga_comercial`.
