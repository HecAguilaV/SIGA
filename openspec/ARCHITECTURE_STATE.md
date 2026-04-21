# OpenSpec: Estado de la Arquitectura SIGA (V2.1)

## Resumen del Sistema
SIGA ha pasado de ser un monolito a una arquitectura de microservicios distribuidos con aislamiento de datos por esquema.

## Estado de los Componentes
| Componente | Estado | Acción Reciente |
| :--- | :--- | :--- |
| **Infraestructura (DB)** | ✅ LISTO | 5 esquemas creados en Postgres + PGVector. |
| **Entidades JPA** | ✅ NORMALIZADO | Todas las entidades apuntan a sus esquemas específicos. |
| **Documentación** | ✅ ALINEADO | Creado Manifiesto Core y Directorio de Servicios. |
| **Microservicios** | 🔄 PENDIENTE | Verificación de arranque y registro en Eureka. |

## Decisiones Técnicas Recientes
1. **Muerte de `siga_saas`**: El esquema monolítico fue eliminado en favor de la granularidad de 5 esquemas.
2. **Strangler Fig en Backend**: El directorio `backend` se mantiene solo para la lógica comercial hasta su futura migración.
3. **IA con Privilegios**: Se establece que los agentes heredan la seguridad del usuario.

## Próximos Objetivos
1. Smoke Test de los microservicios (Build & Run).
2. Diseño del Servicio de Fallback para el Agente IA.
3. Implementación de Permisos Granulares Dinámicos.
