# Service Discovery Skill (Netflix Eureka)

Protocolo para gestionar el registro y descubrimiento de microservicios.

##  Conceptos Clave
- **Eureka Server**: El directorio central donde se registran los servicios.
- **Eureka Client**: Cada microservicio que se anuncia al servidor.
- **Self-Preservation**: Evitar que servicios se marquen como caídos por problemas de red temporales.

## ️ Configuración Estándar (Spring Cloud)
- **Port**: Por defecto `8761`.
- **Hostname**: Usar nombres de servicio en Docker (`eureka-server`).
- **Healthchecks**: Activar el monitoreo del actuador.

## ️ Reglas de Registro
1.  No exponer el Eureka Server a internet (uso interno).
2.  Usar nombres descriptivos (`siga-inventory-service`).
3.  Configurar tiempos de `lease-renewal-interval` según la carga.
