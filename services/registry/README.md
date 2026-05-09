# Service Registry (siga-registry)

*Read this in other languages: [![English](README.en.md)](README.en.md)*

Servidor de descubrimiento de servicios para el ecosistema de microservicios.

## Stack Tecnológico
- **Framework**: Spring Cloud Netflix Eureka Server

## Propósito
Permite que los microservicios se localicen entre sí mediante nombres lógicos (ej: `siga-sales`) en lugar de direcciones IP estáticas, facilitando el escalado horizontal.

## Interrelaciones
- **Clientes**: Todos los microservicios de SIGA se registran en este servidor al iniciar.
- **Gateway**: Es el cliente principal que consulta este registro para enrutar el tráfico.


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
