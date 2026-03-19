# Evaluación Final Transversal: Caso SIGA (Sistema Inteligente de Gestión de Activos)

**Autor:** Héctor Águila  
**Proyecto:** SIGA  
**Enfoque Filosófico y Técnico:** Framework Héctor (Prioridad en la Capa Humana)

---

## Introducción

El presente informe detalla la concepción, diseño y desarrollo de SIGA (Sistema Inteligente de Gestión de Activos), una plataforma tecnológica orientada a resolver la parálisis operativa que enfrentan las Micro, Pequeñas y Medianas Empresas (PYMEs). A diferencia de los enfoques tradicionales centrados únicamente en el almacenamiento de datos, SIGA ha sido diseñado bajo el paradigma del "Framework Héctor", cuya métrica de éxito suprema es la "Capa Humana": la efectividad del sistema para devolver tiempo y reducir la carga cognitiva del emprendedor. 

Para lograr este objetivo axiológico, se ha implementado una arquitectura de microservicios moderna, empleando patrones de diseño resilientes y un flujo de trabajo ágil respaldado por control de versiones estricto (Monorepo) y aseguramiento de la calidad mediante pruebas automatizadas.

---

## A. Diseño de la arquitectura de microservicios

### Justificación del Cambio Arquitectónico
Las PYMEs suelen utilizar software fragmentado (puntos de venta, inventario, atención al cliente) que no dialogan entre sí, lo que genera silos de información. La solución a este problema fue el diseño de una **Arquitectura de Microservicios**. La división del dominio en servicios aislados responde a la necesidad crítica de **escalabilidad independiente** y **resiliencia a fallos**. 

### Módulos y Separación de Responsabilidades
El ecosistema SIGA se subdivide en los siguientes microservicios logísticos y comerciales:
1. **SIGA-Inventario**: El núcleo del negocio, encargado de gestionar el ciclo de vida de los productos (creación, stock, mermas).
2. **SIGA-Ventas-POS**: Controlador de las transacciones comerciales, cajones de venta y cierre de flujos.
3. **SIGA-Asistente-IA**: Orquestador de agentes conversacionales para interactuar con el operario mediante lenguaje natural, delegando carga cognitiva a la máquina.
4. **SIGA-Comercial**: Gestión de suscripciones de clientes B2B.

### Infraestructura de Comunicación y API Gateway
Para evitar que los clientes (Frontend Web y Móvil) lidien con la complejidad de descubrir y rutear peticiones a múltiples servicios, se estipuló el uso de un **API Gateway**. Este componente sirve como fachada del sistema y punto único de entrada. 

### Alineación Ética, Sostenibilidad y Responsabilidad
El diseño de los microservicios obedece estrictamente a políticas de **Sostenibilidad Cognitiva y Blindaje**. Se eligió utilizar un patrón de Autenticación Delegada (Identity Provider) en el Frontend / Gateway para no reinventar esquemas criptográficos vulnerables. Al aislar las bases de datos por microservicio, SIGA mitiga el riesgo de vulnerabilidades masivas (como la Inyección SQL), garantizando que un fallo en el servicio de Ventas no comprometa jamás la integridad del servicio de Seguridad o Inventario.

---

## B. Decisiones en el desarrollo de componentes backend y frontend

La meta en el desarrollo no fue utilizar las herramientas de vanguardia por mera tendencia, sino elegir la tecnología que garantice la mayor "Simplicidad Radical" (Filosofía Haiku) y eficiencia en la entrega de valor al operario.

### Frontend: La Capa Humana en Acción
La solución demanda interfaces que se adapten al entorno del usuario, ya sea frente a un mostrador o en el almacén de logística. 
- Se adoptó **React y Next.js** para el portal comercial web. La elección se basa en el ecosistema robusto de componentes y el "Server-Side Rendering" que asegura cargas ultrarrápidas en entornos de red limitados.
- Para el entorno operativo, se priorizó **React Native**, otorgando al usuario movilidad absoluta dentro de su almacén sin perder fluidez nativa.
La decisión principal de diseño fue mantener la User Experience (UX) por sobre la estética superficial (UI). Cada botón y formulario está pensado para que el flujo de trabajo tome la mitad del tiempo que requeriría en un sistema monolito convencional.

### Backend: El Motor Kotlin
El motor principal fue construido utilizando **Kotlin apoyado en Spring Boot**. 
- Kotlin proporciona una red de seguridad contra excepciones en tiempo de ejecución (Null Safety), vital para el manejo de estructuras financieras y de inventarios críticos. 
- **BFF (Backend for Frontend)**: Una decisión arquitectónica bisagra fue la codificación de servicios intermedios (BFF). En lugar de que la App Móvil solicite datos pesados a tres microservicios distintos, el BFF aglutina, filtra y formatea una única petición que viaja a través del Gateway. Esto minimiza el consumo de datos de red celular del emprendedor y disminuye el uso de batería, siendo una auténtica respuesta a la "Capa Humana".

---

## C. Aplicación de patrones de diseño

La arquitectura limpia no puede existir sin patrones que organicen el flujo y promuevan la mantenibilidad del código fuente a largo plazo. 

### Patrones Implementados y su Impacto Analítico:
1. **Repository Pattern (JPA)**: Aplicado en la capa de infraestructura del backend. En lugar de embeber consultas SQL nativas en los controladores, el ecosistema abstrae el acceso a PostgreSQL mediante interfaces de Spring Data JPA. *Beneficio*: Desacoplamiento total entre la lógica de negocio y el motor relacional.
2. **Factory Method**: Los flujos del dominio, como la creación de una orden de venta o la inserción de nuevos activos al inventario, requieren de objetos complejos con validaciones pre-construidas e instanciadas condicionalmente. El patrón Factory aísla la complejidad de instanciación.
3. **Circuit Breaker**: Implementado para gobernar las conexiones intercomunicadas entre los microservicios (Backend a Asistente IA). *Beneficio*: Si el servicio externo de Inteligencia Artificial presenta lentitud o timeout, el Circuit Breaker "corta" la conexión al vuelo e invoca un comportamiento de rescate ("Fallback") que permite que el Inventario principal siga funcionando sin asistencia conversacional.

### Reflexión sobre Alternativas
El principal desafío en la elección de patrones fue evitar la sobre-ingeniería. A menudo, el exceso de patrones de creación (como Builder o Abstract Factory) ralentiza el entendimiento ("Filosofía Haiku"). Por tanto, se limitó el uso de patrones de diseño exclusivamente a los cuellos de botella detectados durante el levantamiento inicial de requerimientos.

---

## D. Estrategia de branching y gestión de versiones

El ecosistema SIGA se orquesta en su totalidad en un **Monorepo**. Integrar Backend, App Móvil y Web bajo el paraguas de un solo repositorio impuso la necesidad de una gobernanza git estricta.

### Git Flow Modificado y SDD (Spec-Driven Development)
Se implementó un modelo basado en Git Flow pero orquestado bajo la metodología de Especificaciones (SDD). 
- Toda iteración de desarrollo se ejecuta en ramas temáticas, ejemplificadas en ramas como `migracion-microservicios` o `organizacion-documental`.
- **Merge Conflicts Previstos**: Se utilizó el sistema de control de ramas para proteger el ambiente `main`. La estrategia consiste en que cada desarrollador (o agente orquestador) trabaje asíncronamente. El principal desafío ha sido resolver conflictos de "merge" al interactuar en áreas de documentación compartida (por ejemplo, el `README.md`). 
- **Resolución**: El branching obligó a centralizar todo el conocimiento y contratos en directorios exclusivos, mejorando significativamente la coordinación de equipos concurrentes al aislar las fuentes de conflicto.

---

## E. Integración de componentes backend, frontend y base de datos

El diseño final del sistema brilla en su integración continua, asegurando que las capas jamás de crucen en sus responsabilidades.

### Protocolos de Comunicación Restful
La cohesión entre los clientes y los servicios radica en un marco API RESTful. El Frontend negocia estrictamente vía HTTP(s) contra los "Endpoints" expuestos y auditados del API Gateway. 

### Persistencia y Cohesión
La persistencia de datos descansa sobre los pilares de la base de datos PostgreSQL, administrada a nivel de aplicación por la especificación JPA. 
*Desafíos en Inserción*: El "Cold Start" o inicio frío del sistema requirió de flujos asíncronos para poblar los almacenes sin bloquear al cliente. Al independizar el frontend de los hilos del backend, el sistema es capaz de desplegar interfaces interactivas incluso antes de resolver todas las consultas de la persistencia de bases de datos.

---

## F. Pruebas unitarias y aseguramiento de la calidad

Para un sistema crítico dedicado al inventario transaccional, una prueba que falla en producción significa dinero perdido para el cliente. 

### Cobertura y Estabilidad
- Se forzó políticamente una cobertura de código superior al 60%, evaluada en etapas de Integración Continua (CI).
- **Enfoque Pruebas de Dominio**: Las pruebas unitarias fueron orientadas exhaustivamente al Casos de Uso (ej: Validación de Stock con Saldo Insuficiente, rechazos de tokens inválidos).
- **Herramientas de QA**: Utilizando metodologías combinadas con motores asíncronos y el escaneo estático de SonarQube, se validaron code_smells (malas prácticas) e iteraciones innecesarias, cumpliendo así la consigna filosófica de un código tan claro y conciso como un Haiku japonés.

### Dificultades Detectadas
El proceso de refactorización bajo pruebas encontró la mayor dificultad al "mockear" (simular) llamadas al módulo de IA y la carga del API Gateway. La solución residió en crear dobles de pruebas (Mocks) apoyados en frameworks de Testing, asegurando una estabilidad total antes de la llegada del producto a su fase transicional de presentación.

---

## Conclusiones Generales

SIGA no es un proyecto teórico, es un artefacto de ingeniería que busca solucionar dolores estructurales. El haber pasado desde una estructura monolítica a un sistema desacoplado, auditado, y cimentado sobre sólidas decisiones de arquitectura, desarrollo y control versionado, garantiza la mantenibilidad del proyecto para el mediano y largo plazo. 

Este Caso Semestral consolida el tránsito de un equipo de desarrollo tradicional al dominio y organización orquestal moderna, colocando la efectividad técnica e ingenieril enteramente al servicio de la herramienta más valiosa del siglo: el tiempo de la persona humana.
