# Manual Docente y Arquitectura del Sistema (SIGA)

Este documento detalla las decisiones técnicas, la justificación arquitectónica y el flujo de trabajo del proyecto SIGA. Está diseñado para ofrecer una comprensión técnica y académica de alto nivel del sistema.

## 1. Justificación Arquitectónica: Microservicios en Monorepo

El proyecto está estructurado bajo un enfoque de **Microservicios alojados en un Monorepo**.

### ¿Por qué un Monorepo?
Para un desarrollador solitario asistido por agentes de Inteligencia Artificial, un Monorepo centralizado ofrece ventajas críticas:
- **Visibilidad Total:** Permite que los agentes de IA (como Antigravity/Engram) tengan contexto del ecosistema completo. No hay necesidad de saltar entre repositorios.
- **Cambios Atómicos:** Un refactor que cruza las fronteras de los servicios (por ejemplo, actualizar una entidad y su contrato en el Gateway) se puede agrupar en un solo *Pull Request*.
- **Gestión Simplificada de Dependencias:** Todos los servicios comparten las mismas herramientas de construcción (Gradle) y se orquestan bajo el mismo `docker-compose.yml`.

### ¿Por qué Microservicios?
El enfoque orientado a servicios desacoplados asegura la escalabilidad bajo el modelo SaaS (Software as a Service) multi-tenant:
- Cada servicio posee su propio esquema de base de datos (`database-per-service`), lo que elimina acoplamientos espurios y fugas de datos entre dominios.
- Facilidad de escalado independiente (ej. el servicio de Ventas puede requerir más réplicas en horario pico que el servicio de Facturación).

## 2. Arquitectura Hexagonal (Puertos y Adaptadores)

Dentro de cada microservicio, el código sigue el patrón de Arquitectura Hexagonal.

### Estructura de Capas
La premisa de esta arquitectura es **aislar la lógica de dominio** de los detalles de infraestructura o de entrada/salida.

1. **Dominio (Domain):** Entidades de negocio puras y reglas del sistema. No conoce a la base de datos ni a Spring Boot.
2. **Aplicación (Application):** Casos de uso. Orquesta el flujo de negocio llamando a las entidades y utilizando los Puertos.
3. **Puertos (Ports):** Interfaces que definen cómo el sistema se comunica con el exterior (Entrada/Input y Salida/Output).
4. **Adaptadores (Adapters):** Implementaciones tecnológicas de los puertos.
   - *Adaptadores de Entrada:* Controladores REST (Spring Web).
   - *Adaptadores de Salida:* Repositorios de base de datos (Spring Data JPA) o clientes REST a otros microservicios.

## 3. Módulo de Kernel Compartido (siga-common)

El módulo `services/common` actúa como el *Shared Kernel* del sistema.
Su propósito es manejar problemas transversales (cross-cutting concerns) sin acoplar los dominios. Actualmente, encapsula el motor de **Auditoría**.

- Está construido como un **Spring Boot AutoConfiguration Starter**.
- Los microservicios lo importan como dependencia en Gradle (`project(":services:common")`).
- Intercepta las solicitudes HTTP mediante filtros de Servlet y registra eventos sin que el desarrollador del microservicio tenga que escribir código de auditoría repetitivo.
- Este patrón centralizado cumple con las exigencias de privacidad de la **Ley Chilena 21.719**.

## 4. Diagramas de Arquitectura

### Topología del Sistema
El siguiente diagrama muestra el flujo de comunicación y el aislamiento de datos.

```mermaid
graph TD
    Client[Cliente / Frontend] -->|REST / JWT| Gateway(siga-gateway: Puerta de Entrada)
    
    subgraph Microservicios [Dominio de Negocio]
        Auth(siga-auth)
        Inventory(siga-inventory)
        Sales(siga-sales)
        Billing(siga-billing)
        Agent(siga-agent: AI)
    end
    
    Gateway --> Auth
    Gateway --> Inventory
    Gateway --> Sales
    Gateway --> Billing
    Gateway --> Agent
    
    Registry((siga-eureka: Service Registry)) -.->|Descubrimiento| Gateway
    Registry -.->|Registro| Auth
    Registry -.->|Registro| Inventory
    Registry -.->|Registro| Sales
    Registry -.->|Registro| Billing
    Registry -.->|Registro| Agent

    DB[(PostgreSQL)]
    
    Auth -->|Esquema: auth| DB
    Inventory -->|Esquema: inventory| DB
    Sales -->|Esquema: sales| DB
    Billing -->|Esquema: commercial| DB
    Agent -->|Esquema: agent| DB
```

## 5. Integración y Despliegue Continuo (CI/CD)

### GitHub Actions
El proyecto utiliza GitHub Actions como orquestador de CI/CD por dos razones fundamentales:
1. **Automatización Determinista:** Garantiza que el código en el repositorio es siempre compilable y pasa las reglas de seguridad.
2. **Delegación de Cómputo (Offloading):** Traslada la carga pesada de construcción de imágenes y ejecución de pruebas de la máquina local a la infraestructura en la nube.

### Flujo CI/CD
```mermaid
sequenceDiagram
    participant Dev as Desarrollador
    participant GH as GitHub
    participant GA as GitHub Actions
    participant Docker as Docker Hub

    Dev->>GH: Push / Merge a rama
    GH->>GA: Trigger Workflow (docker-build-push.yml)
    GA->>GA: Configurar JDK y Gradle
    GA->>GA: Ejecutar pruebas y compilar JARs
    GA->>GA: Construir imágenes Docker
    GA->>Docker: Push hdaguila/siga-[servicio]:latest
```

## 6. Despliegue Local (Docker Compose)

El archivo `docker-compose.yml` permite levantar una réplica exacta del ecosistema en cualquier dispositivo de desarrollo.
Descarga automáticamente las imágenes publicadas desde Docker Hub, inyecta las credenciales por variables de entorno y ejecuta una única base de datos PostgreSQL particionada lógicamente en esquemas.

## 7. Privacidad y Cumplimiento Legal (Ley 21.719)

SIGA se posiciona como una plataforma SaaS alineada con el nuevo marco legal de ciberseguridad en Chile:
- **Privacidad desde el Diseño:** El aislamiento por esquemas y la arquitectura Zero-Trust no son accidentales, sino una respuesta técnica directa al **Art. 14 quáter** de la Ley 21.719.
- **Seudonimización por Defecto:** La migración de IDs secuenciales (`Long`) a **UUID v4** garantiza que la exposición accidental de una clave primaria no revele el volumen de datos ni permita el escaneo (crawling) de registros de clientes, cumpliendo con el **Art. 14 quinquies**.
- **Gobernanza del Agente IA:** El sistema garantiza que la IA herede los permisos del usuario, asegurando que las decisiones automatizadas respeten el marco de seguridad humana.

## 8. El Estándar del "Espejo Semántico"

La documentación técnica de SIGA sigue una regla de **Simetría Total** para evitar la deuda técnica informativa:
- **Consistencia Idiomática:** Los archivos en los directorios de idioma deben tener nombres y contenidos coherentes con su lengua (ej: `REGLAS_NEGOCIO_CORE.md` en `/es/`). Esto reduce la carga cognitiva para desarrolladores bilingües.
- **Robustez de Diagramas:** Se ha estandarizado el uso de formas de **Estadio (`([])`)** para etiquetas complejas en Mermaid, garantizando que los diagramas C4 se rendericen sin fallos de parseo en cualquier motor de visualización (VS Code, GitHub, Navegador).
- **Contratos de Datos en Testing:** Las colecciones de Postman actúan como el primer punto de validación del contrato de datos, utilizando UUIDs reales para asegurar que los Mocks sean 100% compatibles con la persistencia distribuida.

---

## 9. Transacciones Distribuidas: Patrón SAGA (Coreografía)

La migración a microservicios rompe la "unidad atómica" de la base de datos. En SIGA, una **Venta** involucra dos dominios: `Sales` (el cobro) e `Inventory` (el stock).

### El Desafío: El Problema de las Dos Armadas
Si `Sales` falla después de que `Inventory` descontó stock, o viceversa, terminamos con datos inconsistentes. Como no podemos usar *Two-Phase Commit (2PC)* por su alto acoplamiento y falta de escalabilidad, implementamos **SAGA**.

### ¿Por qué SAGA por Coreografía?
En lugar de un orquestador central (que sería un punto único de falla), usamos **Coreografía basada en Eventos**:
1. Cada servicio publica un evento cuando termina su parte.
2. Los otros servicios escuchan y reaccionan.
- **Justificación**: Máximo desacoplamiento. `Sales` no sabe cómo `Inventory` descuenta el stock; solo sabe que debe esperar un evento de respuesta.

### Apache Kafka: El Sistema Nervioso
Elegimos Kafka sobre RabbitMQ por su **Persistencia (Log-based)** y **Capacidad de Replay**:
- Si el microservicio de Inventario cae, los eventos de venta no se pierden; quedan en el topic hasta que el servicio vuelva y los procese.
- Esto garantiza **Consistencia Eventual**, un pilar de los sistemas distribuidos modernos.

### Idempotencia: La Regla de Oro
En sistemas de mensajería, un mensaje puede llegar **más de una vez** (entrega *at-least-once*).
- **Decisión**: Implementamos una tabla `processed_events` en cada servicio.
- **Fundamento**: Antes de descontar stock, el servicio consulta si ya procesó ese `eventId`. Si sí, lo ignora. Esto evita errores críticos como cobrar dos veces o descontar stock duplicado.

### Transacciones Compensatorias
Si el stock es insuficiente, `Inventory` emite `STOCK_FAILED`. `Sales` reacciona cancelando la venta.
- **Aprendizaje**: En SAGA, no hay "Rollback" tradicional; hay "Compensación". Debemos escribir código que deshaga lógicamente lo que se hizo previamente.

## 10. Evolución del Toolchain: El "Hard-Pinning" de Versiones

Durante la implementación, nos enfrentamos a una incompatibilidad entre el parser de versiones de **Kotlin 1.9.22** y **Java 25 (Preview)**.

### Decisión Técnica: Retroceder para Avanzar
Forzamos el uso de **JDK 21 (LTS)** mediante `gradle.properties`.
- **¿Por qué JDK 21?**: Es la versión de Soporte a Largo Plazo (LTS) actual. Garantiza estabilidad, soporte de la comunidad y compatibilidad total con el ecosistema de Spring Boot 3.2.x.
- **Fundamento**: En producción, la novedad (Java 25) nunca debe comprometer la estabilidad del proceso de construcción (Build). "Bleeding edge" es para experimentos; LTS es para negocios.

## 11. Evolución del Repositorio: De la Dispersión a la Cohesión

SIGA no nació así. Ha pasado por una metamorfosis arquitectónica:

### El Pasado: Monolito Multirepo
- **Problema**: Cada servicio en un repo distinto generaba "Infierno de Dependencias". Si cambiabas algo en `Common`, tenías que actualizar, pushear y pullear en 5 repos más.
- **Resultado**: Lentitud y pérdida de visión global.

### El Presente: Microservicios en Monorepo
- **Solución**: Agrupar todo el ecosistema bajo un mismo control de versiones pero manteniendo el desacoplamiento en tiempo de ejecución.
- **Ventaja de Defensa**: "Utilizamos un Monorepo para facilitar la consistencia transaccional del equipo (IA y humano) y asegurar que las pruebas de integración (SAGA) se validen contra versiones exactas de cada servicio en un solo paso de CI/CD."

## 12. Soberanía de Datos: De Esquemas a DBs Independientes

Aunque hoy usamos una instancia de PostgreSQL particionada por esquemas, el diseño es **DB-Agnostic**.

- **¿Por qué este cambio?**: En un monolito, las tablas se cruzan con `JOINs`. En microservicios, el cruce de datos está PROHIBIDO a nivel de DB.
- **Fundamento**: Cada microservicio es dueño de su esquema. Si mañana el servicio de Inventario necesita escalar masivamente, podemos mover su esquema a su propio servidor físico sin tocar una sola línea de código de los otros servicios. Esto es **Escalabilidad Elástica**.

## 13. Estrategia Bilingüe: El Estándar de la Industria

Es común ver confusión en por qué mezclamos idiomas. En SIGA, la regla es clara:

### Inglés para el "Código y Specs"
- **Razón**: El ecosistema global de desarrollo (Stack Overflow, documentación oficial, librerías) habla inglés. Programar en inglés elimina la fricción de traducir términos técnicos (ej: `invoice` vs `factura`) y asegura que el código sea comprensible para cualquier desarrollador en el mundo.

### Español para los "Commits y Negocio"
- **Razón**: Los commits son la bitácora humana. Usamos español para mantener una comunicación fluida con los stakeholders locales y asegurar que la intención detrás de cada cambio sea inequívoca para el equipo actual.
- **Filosofía**: "Code for the machine (Global), commit for the team (Local)."

## 14. Privacidad Financiera y "Zero-Knowledge" Architecture

Una de las decisiones más críticas de SIGA es la implementación del principio de **Ceguera al Dato Financiero** para el administrador de la plataforma.

### La Decisión: Zero-Knowledge por Diseño
A diferencia de otros SaaS que recolectan métricas de facturación para "Business Intelligence" propio, SIGA ha decidido **NO** permitir que el Administrador Master vea montos económicos ni detalles de transacciones de las Pymes.

- **Justificación**: Cumplimiento extremo del **Art. 3, letra c (Proporcionalidad)** de la Ley 21.719. El administrador no necesita saber cuánto factura una Pyme para garantizar que el sistema sea estable.
- **Implementación Técnica**: Las métricas del `admin-portal` se limitan al **throughput de eventos** (cantidad de mensajes procesados por segundo) y a la salud de la infraestructura, sin decodificar el contenido económico de los payloads.

### Rechazo del Catálogo Maestro
Se descartó la idea de una gestión centralizada de catálogos para evitar pasar de un modelo de **Servicio Proactivo** a uno **Reactivo**.
- **Fundamento**: La soberanía del inventario reside exclusivamente en la Pyme. SIGA no interfiere en la definición de productos, lo que asegura que el sistema sea un facilitador tecnológico puro y no un administrador de contenido ajeno.
- **Resultado**: Reducción drástica del riesgo legal y aumento de la confianza del cliente al garantizar que SIGA no tiene "ojos" sobre su estrategia de precios o volumen de stock crítico.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
