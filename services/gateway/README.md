# Gateway (siga-gateway)

Punto de entrada único (API Gateway) para todo el tráfico de la plataforma.

## 🛠 Stack Tecnológico
- **Framework**: Spring Cloud Gateway
- **Reactive**: Spring WebFlux
- **Seguridad**: JWT Filter / CORS Centralizado

## 📡 Funciones Clave
- **Enrutamiento Dinámico**: Reenvía peticiones a microservicios registrados en Eureka.
- **Seguridad**: Validación de tokens JWT antes de permitir el paso.
- **Resiliencia**: Implementación de Circuit Breakers (Resilience4j).
- **Agregación Swagger**: Centralización de documentación de todos los servicios.

## ⛓️ Interrelaciones
- **Registry**: Consulta a `siga-registry` para encontrar las instancias activas.
- **Auth**: Delega la validación de seguridad.

---
> "La puerta al ecosistema SIGA."
