# Informe Final – Evaluación Parcial 1 (DSY1106)

## 1. Introducción y Contexto del Problema

SIGA (Sistema Inteligente de Gestión de Activos) nace como una solución para **PYMEs** que sufren de fricción cognitiva, pérdida de capital por falta de trazabilidad y ausencia de movilidad en sus procesos operativos. El proyecto se estructuró para superar las limitaciones de arquitecturas tradicionales centralizadas, diseñando un ecosistema nativo en la nube altamente distribuido y orientado a eventos. Esta arquitectura previene puntos únicos de falla, garantiza el escalamiento bajo demanda y permite que dominios como ventas, inventario y seguridad evolucionen de forma independiente.

## 2. Estrategia Arquitectónica y Patrones Distribuidos

La transformación del sistema abandona completamente el diseño monolítico para adoptar una **Arquitectura Orientada a Eventos (Event-Driven Microservices)** pura, apoyada en una orquestación rigurosa del tráfico y de los datos.

Para la integridad transaccional distribuida, se implementa el **Patrón SAGA** mediante mensajería asíncrona. Esto permite operaciones complejas (ej. registrar una venta y descontar stock simultáneamente) de manera coordinada entre bases de datos físicamente separadas, eliminando la latencia y bloqueos de las transacciones distribuidas clásicas (2PC).

Las herramientas clave en esta arquitectura son:

- **Kotlin sobre Spring Boot 4.0.6:** Desarrollo del backend core.
- **Apache Kafka:** Event streaming y backbone para la comunicación asíncrona entre microservicios (implementación SAGA).
- **Service Discovery (Netflix Eureka):** Registro dinámico (`siga-registry`) para localización de instancias sin depender de IPs estáticas.
- **Spring Cloud Gateway:** (`siga-gateway`) Actúa como único punto de entrada, enrutando peticiones y ocultando la topología de red interna.
- **Database-per-Service (PostgreSQL):** Aislamiento absoluto de esquemas físicos por dominio.

## 3. Arquitectura Externa e Interna

### 3.1 Arquitectura de Microservicios (El Mapa de la Ciudad)

El ecosistema SIGA se descompone en los siguientes microservicios independientes:

| Microservicio | Backend | Responsabilidad Core |
|---------------|-------------------|----------------------|
| `auth` | Spring Boot (Kotlin) | Autenticación, JWT, RBAC y control de acceso seguro. |
| `inventory` | Spring Boot (Kotlin) | Gestión de productos y kardex. Integra Kafka para sincronización asíncrona. |
| `sales` | Spring Boot (Kotlin) | Motor POS y transacciones. Emite eventos a Kafka para orquestar la saga de ventas. |
| `billing` | Spring Boot (Kotlin) | Emisión de comprobantes y gestión de facturación. |
| `agent` | Contenedor Aislado | Entorno para capacidades cognitivas y vectoriales (PGVector) vinculadas a IA. |
| `gateway` & `registry` | Spring Cloud | Resolución DNS interna (Eureka) y proxy inverso unificado. |

### 3.2 Visión de Arquitectura Interna (Clean Architecture)

A nivel de software, los microservicios aplican **Clean Architecture (Arquitectura Hexagonal)**. Este patrón aísla los Modelos de Dominio en el centro, asegurando que las reglas de negocio estén libres de dependencias del framework. La infraestructura (Controladores REST, Kafka Listeners, Repositorios JPA) opera únicamente como adaptadores en la capa externa. Esto garantiza que la lógica de dominio sea completamente testeable de forma aislada.

## 4. Gobernanza de Datos y Cumplimiento Legal (Ley 21.719)

### Privacy by Design y Privacy by Default

La adopción del patrón **Database-per-Service** no solo responde a escalabilidad, sino que es el fundamento técnico para cumplir con la **Ley chilena 21.719** sobre Protección de Datos Personales.

Al separar físicamente los datos, se aplica la **Privacidad por Diseño** reduciendo el radio de ataque ('blast radius'). Un acceso no autorizado al módulo de inventario no permite comprometer credenciales de usuario de `auth` ni datos comerciales de `sales` debido a la ausencia de claves foráneas físicas cruzadas.

Las dependencias entre dominios se resuelven de forma lógica mediante identificadores opacos (**UUID v4**). De este modo, la plataforma garantiza la "Debida Diligencia" técnica mitigando proactivamente los riesgos de filtración.

## 5. Escalabilidad y Resiliencia (Green Computing)

- **Escalamiento Elástico por Dominio:** Si el nodo de `sales` experimenta un pico de tráfico masivo, es posible escalar horizontalmente de forma exclusiva ese microservicio mediante réplicas Docker, sin sobredimensionar la memoria requerida por módulos ociosos como `billing`. 
- **Resiliencia ante Caídas (Kafka):** La arquitectura asíncrona asegura que si el servicio `inventory` sufre una caída, las órdenes emitidas por `sales` se encolan en los tópicos de Kafka de forma segura. Al restablecerse el inventario, procesará los eventos encolados sin pérdida transaccional, garantizando tolerancia a fallos.

## 6. Conclusiones

La arquitectura construida para SIGA consolida de forma robusta los preceptos modernos del ecosistema **FullStack 3**. 

La adopción de una orquestación centralizada mediante **Spring Cloud Gateway y Eureka**, junto con la integridad distribuida que aporta el **Patrón SAGA mediante Apache Kafka**, demuestra la viabilidad técnica para soportar sistemas de alta transaccionalidad. La estricta separación de responsabilidades, tanto a nivel de código (Arquitectura Hexagonal) como de bases de datos, posiciona a SIGA como un ecosistema empresarial altamente resiliente y escalable.
