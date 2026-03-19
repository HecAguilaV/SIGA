# Evaluacion Final Transversal: Caso SIGA (Sistema Inteligente de Gestion de Activos)

**Autor:** Hector Aguila  
**Proyecto:** SIGA  
**Enfoque Filosofico y Tecnico:** Framework Hector (Prioridad en la Capa Humana)

---

## Introduccion

El presente informe detalla la concepcion, diseño y desarrollo de SIGA (Sistema Inteligente de Gestion de Activos), una plataforma tecnologica orientada a resolver la paralisis operativa que enfrentan las Micro, Pequeñas y Medianas Empresas (PYMEs). A diferencia de los enfoques tradicionales centrados unicamente en el almacenamiento de datos, SIGA ha sido diseñado bajo el paradigma del "Framework Hector", cuya metrica de exito suprema es la "Capa Humana": la efectividad del sistema para devolver tiempo y reducir la carga cognitiva del emprendedor. 

Para lograr este objetivo axiologico, se ha implementado una arquitectura de microservicios moderna, empleando patrones de diseño resilientes y un flujo de trabajo agil respaldado por control de versiones estricto (Monorepo) y aseguramiento de la calidad mediante pruebas automatizadas.

---

## A. Diseño de la arquitectura de microservicios

### Justificacion del Cambio Arquitectonico
Las PYMEs suelen utilizar software fragmentado (puntos de venta, inventario, atencion al cliente) que no dialogan entre si, lo que genera silos de informacion. La solucion a este problema fue el diseño de una **Arquitectura de Microservicios**. La division del dominio en servicios aislados responde a la necesidad critica de **escalabilidad independiente** y **resiliencia a fallos**. 

### Modulos y Separacion de Responsabilidades
El ecosistema SIGA se subdivide en los siguientes microservicios logisticos y comerciales:
1. **SIGA-Inventario**: El nucleo del negocio, encargado de gestionar el ciclo de vida de los productos (creacion, stock, mermas).
2. **SIGA-Ventas-POS**: Controlador de las transacciones comerciales, cajones de venta y cierre de flujos.
3. **SIGA-Asistente-IA**: Orquestador de agentes conversacionales para interactuar con el operario mediante lenguaje natural, delegando carga cognitiva a la maquina.
4. **SIGA-Comercial**: Gestion de suscripciones de clientes B2B.

### Infraestructura de Comunicacion y API Gateway
Para evitar que los clientes (Frontend Web y Movil) lidien con la complejidad de descubrir y rutear peticiones a multiples servicios, se estipulo el uso de un **API Gateway**. Este componente sirve como fachada del sistema y punto unico de entrada. 

### Alineacion Etica, Sostenibilidad y Responsabilidad
El diseño de los microservicios obedece estrictamente a politicas de **Sostenibilidad Cognitiva y Blindaje**. Se eligio utilizar un patron de Autenticacion Delegada (Identity Provider) en el Frontend / Gateway para no reinventar esquemas criptograficos vulnerables. Al aislar las bases de datos por microservicio, SIGA mitiga el riesgo de vulnerabilidades masivas (como la Inyeccion SQL), garantizando que un fallo en el servicio de Ventas no comprometa jamas la integridad del servicio de Seguridad o Inventario.

---

## B. Decisiones en el desarrollo de componentes backend y frontend

La meta en el desarrollo no fue utilizar las herramientas de vanguardia por mera tendencia, sino elegir la tecnologia que garantice la mayor "Simplicidad Radical" (Filosofia Haiku) y eficiencia en la entrega de valor al operario.

### Frontend: La Capa Humana en Accion
La solucion demanda interfaces que se adapten al entorno del usuario, ya sea frente a un mostrador o en el almacen de logistica. 
- Se adopto **React y Next.js** para el portal comercial web. La eleccion se basa en el ecosistema robusto de componentes y el "Server-Side Rendering" que asegura cargas ultrarrapidas en entornos de red limitados.
- Para el entorno operativo, se priorizo **React Native**, otorgando al usuario movilidad absoluta dentro de su almacen sin perder fluidez nativa.
La decision principal de diseño fue mantener la User Experience (UX) por sobre la estetica superficial (UI). Cada boton y formulario esta pensado para que el flujo de trabajo tome la mitad del tiempo que requeriria en un sistema monolito convencional.

### Backend: El Motor Kotlin
El motor principal fue construido utilizando **Kotlin apoyado en Spring Boot**. 
- Kotlin proporciona una red de seguridad contra excepciones en tiempo de ejecucion (Null Safety), vital para el manejo de estructuras financieras y de inventarios criticos. 
- **BFF (Backend for Frontend)**: Una decision arquitectonica bisagra fue la codificacion de servicios intermedios (BFF). En lugar de que la App Movil solicite datos pesados a tres microservicios distintos, el BFF aglutina, filtra y formatea una unica peticion que viaja a traves del Gateway. Esto minimiza el consumo de datos de red celular del emprendedor y disminuye el uso de bateria, siendo una autentica respuesta a la "Capa Humana".

---

## C. Aplicacion de patrones de diseño

La arquitectura limpia no puede existir sin patrones que organicen el flujo y promuevan la mantenibilidad del codigo fuente a largo plazo. 

### Patrones Implementados y su Impacto Analitico:
1. **Repository Pattern (JPA)**: Aplicado en la capa de infraestructura del backend. En lugar de embeber consultas SQL nativas en los controladores, el ecosistema abstrae el acceso a PostgreSQL mediante interfaces de Spring Data JPA. *Beneficio*: Desacoplamiento total entre la logica de negocio y el motor relacional.
2. **Factory Method**: Los flujos del dominio, como la creacion de una orden de venta o la insercion de nuevos activos al inventario, requieren de objetos complejos con validaciones pre-construidas e instanciadas condicionalmente. El patron Factory aísla la complejidad de instanciacion.
3. **Circuit Breaker**: Implementado para gobernar las conexiones intercomunicadas entre los microservicios (Backend a Asistente IA). *Beneficio*: Si el servicio externo de Inteligencia Artificial presenta lentitud o timeout, el Circuit Breaker "corta" la conexion al vuelo e invoca un comportamiento de rescate ("Fallback") que permite que el Inventario principal siga funcionando sin asistencia conversacional.

### Reflexion sobre Alternativas
El principal desafio en la eleccion de patrones fue evitar la sobre-ingenieria. A menudo, el exceso de patrones de creacion (como Builder o Abstract Factory) ralentiza el entendimiento ("Filosofia Haiku"). Por tanto, se limito el uso de patrones de diseño exclusivamente a los cuellos de botella detectados durante el levantamiento inicial de requerimientos.

---

## D. Estrategia de branching y gestion de versiones

El ecosistema SIGA se orquesta en su totalidad en un **Monorepo**. Integrar Backend, App Movil y Web bajo el paraguas de un solo repositorio impuso la necesidad de una gobernanza git estricta.

### Git Flow Modificado y SDD (Spec-Driven Development)
Se implemento un modelo basado en Git Flow pero orquestado bajo la metodologia de Especificaciones (SDD). 
- Toda iteracion de desarrollo se ejecuta en ramas tematicas, ejemplificadas en ramas como `migracion-microservicios` o `organizacion-documental`.
- **Merge Conflicts Previstos**: Se utilizo el sistema de control de ramas para proteger el ambiente `main`. La estrategia consiste en que cada desarrollador (o agente orquestador) trabaje asincronamente. El principal desafio ha sido resolver conflictos de "merge" al interactuar en areas de documentacion compartida (por ejemplo, el `README.md`). 
- **Resolucion**: El branching obligo a centralizar todo el conocimiento y contratos en directorios exclusivos, mejorando significativamente la coordinacion de equipos concurrentes al aislar las fuentes de conflicto.

---

## E. Integracion de componentes backend, frontend y base de datos

El diseño final del sistema brilla en su integracion continua, asegurando que las capas jamas de crucen en sus responsabilidades.

### Protocolos de Comunicacion Restful
La cohesion entre los clientes y los servicios radica en un marco API RESTful. El Frontend negocia estrictamente via HTTP(s) contra los "Endpoints" expuestos y auditados del API Gateway. 

### Persistencia y Cohesion
La persistencia de datos descansa sobre los pilares de la base de datos PostgreSQL, administrada a nivel de aplicacion por la especificacion JPA. 
*Desafios en Insercion*: El "Cold Start" o inicio frio del sistema requirio de flujos asíncronos para poblar los almacenes sin bloquear al cliente. Al independizar el frontend de los hilos del backend, el sistema es capaz de desplegar interfaces interactivas incluso antes de resolver todas las consultas de la persistencia de bases de datos.

---

## F. Pruebas unitarias y aseguramiento de la calidad

Para un sistema critico dedicado al inventario transaccional, una prueba que falla en produccion significa dinero perdido para el cliente. 

### Cobertura y Estabilidad
- Se forzo politicamente una cobertura de codigo superior al 60%, evaluada en etapas de Integracion Continua (CI).
- **Enfoque Pruebas de Dominio**: Las pruebas unitarias fueron orientadas exhaustivamente al Casos de Uso (ej: Validacion de Stock con Saldo Insuficiente, rechazos de tokens invalidos).
- **Herramientas de QA**: Utilizando metodologias combinadas con motores asincronos y el escaneo estatico de SonarQube, se validaron code_smells (malas practicas) e iteraciones innecesarias, cumpliendo asi la consigna filosofica de un codigo tan claro y conciso como un Haiku japones.

### Dificultades Detectadas
El proceso de refactorizacion bajo pruebas encontro la mayor dificultad al "mockear" (simular) llamadas al modulo de IA y la carga del API Gateway. La solucion residio en crear dobles de pruebas (Mocks) apoyados en frameworks de Testing, asegurando una estabilidad total antes de la llegada del producto a su fase transicional de presentacion.

---

## Conclusiones Generales

SIGA no es un proyecto teorico, es un artefacto de ingenieria que busca solucionar dolores estructurales. El haber pasado desde una estructura monolítica a un sistema desacoplado, auditado, y cimentado sobre solidas decisiones de arquitectura, desarrollo y control versionado, garantiza la mantenibilidad del proyecto para el mediano y largo plazo. 

Este Caso Semestral consolida el transito de un equipo de desarrollo tradicional al dominio y organizacion orquestal moderna, colocando la efectividad tecnica e ingenieril enteramente al servicio de la herramienta mas valiosa del siglo: el tiempo de la persona humana.
