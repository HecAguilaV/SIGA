# Plan de Cirugía Documental (Módulo Backend) 

Tras ejecutar la *Skill DOC_AUDITOR*, se han analizado los 41 archivos `.md` presentes en `services/backend`. 

##  Diagnóstico de la Auditoría
Héctor tenía razón: hay un nivel crítico de "ruido documental". Durante la etapa de repositorios aislados, el Backend se usó como núcleo de comunicación para todos los equipos. Esto generó una clonación masiva de información.

**Patrones de Ruido Identificados:**
1. **Carpetas Espejo**: Existen directorios enteros (`EQUIPO_APP_MOVIL`, `EQUIPO_WEBAPP`, `EQUIPO_WEB_COMERCIAL`) que contienen copias exactas o variaciones mínimas de los mismos archivos (`API_ENDPOINTS.md`, `ENDPOINTS_OPERATIVOS.md`).
2. **Duplicación de Origen**: La carpeta `docs/ECOSISTEMA` vuelve a duplicar especificaciones de frontend.
3. **Archivos de Entregables Antiguos**: `docs/entregables_finales` contiene versiones específicas para entregas pasadas (ERS, Manual de Usuario).

##  Plan de Acción (La Cirugía)

Para seguir el **Framework Héctor** (simplicidad y cero ruido), propongo la siguiente purga y consolidación en la rama `organizacion-documental`:

### 1. Eliminación Definitiva (El Ruido)
Se eliminarán las siguientes carpetas del backend por ser redundantes y pertenecer al pasado. Su historial de todos modos vive en los repositorios antiguos:
-  `docs/EQUIPO_APP_MOVIL/`
-  `docs/EQUIPO_WEBAPP/`
-  `docs/EQUIPO_WEB_COMERCIAL/`
-  `docs/ECOSISTEMA/`

### 2. Consolidación (El Haiku)
Los múltiples archivos de endpoints (`API_ENDPOINTS.md`, `ENDPOINTS_OPERATIVOS.md`, `ANALISIS_...`) que saturan la carpeta `docs` serán unificados en **UN SOLO** documento vivo y técnico:
- ️ `docs/BACKEND_API_CONTRACT.md`: Definirá los contratos actuales de los endpoints. *(Los 4-5 archivos actuales se borran)*.

### 3. Preservación (Lo Esencial)
Conservaremos en el backend solo lo que le pertenece estrictamente a su arquitectura:
-  `ARQUITECTURA_BACKEND.md` (Para la estructura interna de Kotlin).
-  `ESQUEMAS_DATABASE.md` (Para el mapa de Postgres).
-  `RAILWAY.md` (Para el despliegue).

## ️ Conclusión
Pasaremos de **41 archivos confusos a menos de 5 documentos técnicos sólidos**. El backend dejará de ser el "basurero documental" de los otros equipos y volverá a ser simplemente el motor lógico de SIGA.

---
> Un Soñador con Poca RAM  & Misael
