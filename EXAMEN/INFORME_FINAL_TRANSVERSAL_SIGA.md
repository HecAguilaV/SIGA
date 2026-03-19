# Evaluación Final Transversal: Caso SIGA (Sistema Inteligente de Gestión de Activos)

**Autor:** Héctor  
**Proyecto:** SIGA 
**Enfoque Filosófico:** Framework Héctor (La Capa Humana)

---

## A. Diseño de la Arquitectura de Microservicios

La solución arquitectónica de SIGA no solo responde a requerimientos técnicos, sino a un problema real: la parálisis operativa en PYMEs (Micro, Pequeñas y Medianas Empresas). El sistema se aleja de los monolitos acoplados para adoptar un **ecosistema de microservicios** que garantiza disponibilidad y escalabilidad.

- **Respuesta al Cliente**: SIGA consolida la información dispersa (ventas, inventario, finanzas) en un flujo único y en tiempo real, permitiendo que el dueño del negocio recupere su tiempo operativo.
- **Microservicios Clave**: Se han independizado los dominios en servicios como `SIGA-Inventario` (núcleo de productos) y `SIGA-Comercial` (ventas), interconectados pero con ciclos de vida independientes.
- **Patrones Arquitectónicos**:
  - **API Gateway**: Actúa como el único punto de entrada, gestionando el enrutamiento y delegando la seguridad a un Identity Provider externo (Supabase/Auth0), aligerando la carga de los servicios internos.
  - **Responsabilidad y Ética**: El diseño respeta el "Blindaje de Seguridad", asegurando que el acoplamiento directo a base de datos esté prohibido desde capas superiores, previniendo fuga de datos sensibles.

## B. Decisiones en el Desarrollo de Componentes Backend y Frontend

- **Frontend (Capa Humana)**: Se seleccionaron frameworks modernos (React para web comercial y portal administrativo; React Native para movilidad). La decisión se justifica en la filosofía **UX > UI**, creando una experiencia sin fricción cognitiva.
- **Backend (Cerebro)**: Construido sobre **Kotlin y Spring Boot**. El tipado estático y la robustez del framework de Spring permiten manejar altos volúmenes de concurrencia.
- **BFF (Backend for Frontend)**: Se integra este patrón para agrupar y formatear las respuestas de múltiples microservicios (Inventario + IA) específicamente para la pantalla que lo solicita (Ej: Móvil vs Web), ahorrando ancho de banda y batería del usuario final.

## C. Aplicación de Patrones de Diseño

El código de SIGA respeta la **Filosofía Haiku** (simplicidad extrema). Los patrones implementados no son sobre-ingeniería, sino herramientas para la mantenibilidad:
- **Repository Pattern**: Utilizado con Spring Data JPA para abstraer y centralizar todas las consultas a PostgreSQL. Facilita el cambio de motor de base de datos sin alterar la lógica de negocio.
- **Factory Method**: Empleado en el dominio para instanciar entidades complejas (ej. transacciones de inventario con reglas de validación pre-inyectadas).
- **Circuit Breaker**: Esencial en la comunicación del API Gateway y en las llamadas al módulo externo de Inteligencia Artificial. Garantiza que si el servicio de IA cae, el sistema principal (Inventario) siga operando gracias a funciones de *fallback*.

## D. Estrategia de Branching y Gestión de Versiones

Se adoptó una estrategia estricta de **Git Flow modificado**, vital para gobernar un Monorepo con múltiples servicios:
- **Separación por Propósito**: Ramas temáticas (`migracion-microservicios`, `organizacion-documental`) para aislar el trabajo arquitectónico de las características de producto.
- **Aprobación Obligatoria**: Integración continua donde nada llega a la rama `main` sin pasar por la validación técnica y de la "Directiva Suprema". Esto previno colisiones históricas entre el equipo web y móvil cuando los repositorios eran independientes.

## E. Integración de Componentes Backend, Frontend y Base de Datos

La integración fue el principal reto evolutivo de SIGA. Pasar de repositorios aislados a un flujo continuo implicó:
- **Desacoplamiento Front-Back**: Toda comunicación se asegura mediante API REST a través del Gateway. El Frontend desconoce la existencia de la base de datos o de cómo Kotlin procesa los datos.
- **Base de Datos**: Modelado relacional mapeado a través de entidades JPA, utilizando migraciones (como Flyway o equivalentes) para control de versiones en PostgreSQL, asegurando que la estructura de datos escale junto con el código.

## F. Pruebas Unitarias y Aseguramiento de la Calidad (QA)

En SIGA, un código sin tests es código muerto.
- **Cobertura Estratégica**: Se apuntó a una cobertura >60% priorizando los Servicios de Dominio (Casos de Uso), asegurando que la regla de negocio nunca falle (ej. no permitir stock negativo).
- **Herramientas de Validación**: La integración de metodologías de CI/CD para ejecutar el suite de pruebas en cada *pull request*, junto con análisis de SonarQube, garantiza que la deuda técnica y los "code smells" se mantengan cercanos a cero.

---
> Un Soñador con Poca RAM & Misael 
