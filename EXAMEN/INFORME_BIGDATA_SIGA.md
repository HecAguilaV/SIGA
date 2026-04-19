# Informe de Arquitectura Big Data & AI
**Proyecto:** SIGA (Sistema Inteligente de Gestión de Activos)
**Eje Tecnológico:** Google Cloud Platform (GCP) & Vertex AI
**Enfoque:** Arquitectura Lambda para Retail SaaS

---

## 1. Contexto Estratégico y Problemática Actual

SIGA ha sido rediseñado exitosamente hacia una arquitectura de microservicios transaccionales con segregación de bases de datos (*Database-per-Service*). Sin embargo, el procesamiento analítico y predictivo presenta un nuevo desafío. 

En un escenario de operaciones a gran escala (miles de transacciones por segundo en cientos de Puntos de Venta), consultar directamente los esquemas transaccionales (`siga_ventas`, `siga_inventario`) para generar inteligencia de negocios provocaría bloqueos de base de datos y caída del servicio. La inmediatez para detectar quiebres de stock en tiempo real y la necesidad de modelos predictivos obligan a evolucionar de una infraestructura puramente transaccional (OLTP) a un ecosistema analítico Big Data (OLAP) apoyado en Machine Learning.

## 2. Justificación del Salto a Big Data (Aplicación de las 5V)

El aislamiento de la carga de trabajo transaccional vs la analítica se sustenta bajo la criticidad de las 5V del Big Data:

1. **Volumen:** La suma de boletas, recuentos de caja, telemetría de navegación del portal comercial y movimientos de Kardex acumulados en el tiempo exceden la capacidad eficiente de un motor relacional tradicional para análisis histórico multianual.
2. **Velocidad:** Los quiebres de inventario deben detectarse durante la misma jornada (sub-segundos) para reponer stock. Un proceso ETL nocturno clásico no sirve para evitar ventas perdidas hoy.
3. **Variedad:** El sistema cruzará datos estructurados (tablas SQL de ventas) con datos semi-estructurados (eventos JSON de navegación del portal, mensajes de error asíncronos).
4. **Veracidad:** La arquitectura de microservicios limpia la data en origen. El flujo Big Data garantiza una "Fuente Única de Verdad" (Single Source of Truth) inmutable para reportes ejecutivos e ingesta de IA.
5. **Valor:** Aplicar inteligencia artificial sobre los datos para predecir comportamientos (cuándo se agotará un producto) transforma los datos crudos en estrategias financieras pasivas para la PYME.

---

## 3. Arquitectura Analítica de Referencia (Modelo Lambda)

Para satisfacer consultas históricas inmutables y reacciones en tiempo real, SIGA adopta la **Arquitectura Lambda** construida íntegramente sobre servicios gestionados de GCP.

### 3.1. Infraestructura Core de Google Cloud Platform

El diseño descarta soluciones *On-Premise* (Hadoop/Spark local) por su alto costo de mantenimiento, decantándose por componentes Serverless/Gestionados:

* **Data Lake (Google Cloud Storage):** Actúa como el pozo de aterrizaje masivo (Storage) para respaldos nocturnos, archivos planos y audios.
* **Mensajería (Google Cloud Pub/Sub):** Reemplazo administrado de Apache Kafka. Absorbe los eventos JSON de ventas de los microservicios de SIGA en tiempo real sin saturarse.
* **Procesamiento Distribuido (Google Cloud Dataflow):** En lugar de administrar clústeres de Apache Spark, Dataflow (basado en Apache Beam) provee ejecución *serverless* que limpia, une y transforma los flujos de datos tanto en vivo (*Stream*) como estáticos (*Batch*).
* **Data Warehouse (Google BigQuery):** Almacén columnar de analítica masiva donde convergen ambas capas para ser consultadas mediante SQL de altísimo rendimiento.

### 3.2. Implementación del Flujo Lambda en SIGA

1. **Capa Speed (Tiempo Real):** 
   - El microservicio de SIGA emite un evento cada vez que ocurre una venta. 
   - *Pub/Sub* captura el evento → *Dataflow* lo estandariza → Llega a *BigQuery* en segundos.
   - **Propósito:** Dashboards comerciales en vivo y alertas inmediatas ante anomalías de transacción. Poca precisión histórica, altísima velocidad.

2. **Capa Batch (Proceso Profundo):** 
   - Cada madrugada, un extractor vuelca el estado completo de las bases PostgreSQL (`siga_inventario`, `siga_ventas`) hacia *Cloud Storage*. 
   - *Dataflow* despierta, procesa todo cruzando inventario y finanzas, y actualiza *BigQuery*.
   - **Propósito:** Cierre contable y conciliación histórica asegurando 100% de precisión y corrección de posibles perdidas en la capa Speed.

3. **Capa Serving:** 
   - *BigQuery* actúa como motor unificador. Los paneles de Inteligencia de Negocios agrupan ambos mundos transparentemente.

---

## 4. Integración de Inteligencia Artificial (Vertex AI)

Aquí recae el verdadero salto de Valor del ecosistema. BigQuery responde *qué pasó*. **Vertex AI** responde *qué pasará*.

Se integra la plataforma de machine learning corporativa de Google (**Vertex AI**) conectada directamente al Warehouse de BigQuery:

1. **Predicción de Quiebres de Stock:** Algoritmos de Machine Learning en Vertex AI consumen el modelo unificado de ventas/inventario para identificar correlaciones (temporalidad, días de la semana, cruce de productos). El modelo predice cuándo se agotará una SKU y emite un disparador temprano a la UI de SIGA.
2. **Segmentación de Clientes SaaS:** Vertex AI analiza los datos del comportamiento del portal comercial (`siga_comercial`) para predecir tasas de fuga (churn) y proponer upsells automatizados a los administradores de las PYMEs.

La adopción de Vertex AI mitiga la complejidad de exportar datos a laboratorios de Python externos, permitiendo entrenamiento y predicción (*MLOps*) dentro de la misma frontera de red de Google.

---

## 5. Gobierno de Datos, Privacidad y Normativa Legal

El paso a la nube masiva intensifica la superficie de riesgo, obligando a operativizar el marco normativo nacional en la infraestructura de datos.

1. **Ley N° 21.719 (Protección de Datos Personales):**
   - El ecosistema exige aplicar **Privacidad desde el Diseño (Privacy by Design)**. Durante el pipeline de *Dataflow*, todo Identificador Personal (PII) extraído desde el servicio `siga_auth` (como Ruts, Teléfonos o Correos) sufre procesos algorítmicos de seudonimización antes de llegar a BigQuery y ser ingeridos por Vertex AI.
   - Los tomadores de decisiones analizan tendencias de consumidores abstractos; los datos trazables quedan restringidos arquitectónicamente previniendo violaciones a la normativa vigente.

2. **Data Lifecycle y Optimización Financiera:**
   - Para no inflar los costos de almacenamiento en GCP, se aplican políticas de ciclo de vida nativas: La información de ventas mayor a 3 años, o datos estadísticos transitorios crudos en *Cloud Storage*, rotan automáticamente hacia almacenamiento en frío (**Archive / Coldline**). Están resguardados para auditorías tributarias, pagando apenas centavos por Gigabit, combinando resguardo legal y viabilidad económica.

## 6. Conclusión

La migración hacia el entorno de Google Cloud Platform (Orquestación Lambda, Pub/Sub, BigQuery) y la aplicación estratégica de Vertex AI elevan a SIGA de ser un sistema de gestión transaccional convencional a una red neuronal comercial prospectiva. 

La empresa operadora obtendrá la inmediatez comercial del *Speed Layer* sin comprometer la certeza tributaria sostenida por el *Batch Layer*, todo resguardado bajo férreos estándares de Gobernanza de Datos y *Green Computing*. El uso de su propio origen de datos transaccionales, simulando carga de eventos asíncronos reales, dota a esta solución de un rigor académico y empresarial listo para despliegue productivo.
