# Skill: Auditor Documental (Doc Auditor) 🧹📖

Este protocolo define cómo Misael (actuando como Subagente Documental) debe procesar, consolidar y limpiar la documentación heredada de los repositorios individuales hacia el monorepo SIGA.

## 🎯 Objetivo
El monorepo debe contener única y exclusivamente documentación **útil, accionable y estandarizada** para la arquitectura actual (Microservicios, SDD, Framework Héctor). El "ruido" histórico debe ser eliminado, ya que existe respaldo en los repositorios originales.

## 🔍 Reglas de Análisis (Pensar antes de Actuar)
1. **Inventario Completo**: Antes de borrar, se deben listar todos los archivos `.md`, `.pdf`, `.txt` en el módulo objetivo.
2. **Evaluación de Valor**:
   - *¿Este documento describe el estado actual o futuro de la arquitectura?* -> **Conservar / Refactorizar**.
   - *¿Este documento contiene Historias de Usuario, Reglas de Negocio activas o requerimientos funcionales?* -> **Consolidar en un documento central**.
   - *¿Este documento solo explicaba cómo levantar el microservicio de forma aislada en el pasado o actaba como puente temporal?* -> **Eliminar**.
3. **Consolidación**: Múltiples documentos pequeños sobre un mismo tema deben fusionarse en un solo documento conciso ("Haiku").

## 🛠️ Procedimiento de Ejecución
1. Fase **Descubrimiento**: Usar `list_dir` y `find_by_name` (buscando `.md`) en el módulo.
2. Fase **Lectura Rápida**: Leer los encabezados e índices de los archivos encontrados para entender su propósito.
3. Fase **Propuesta**: Presentar a Héctor un plan detallado de qué conservar, qué fusionar y qué eliminar.
4. Fase **Limpieza (Cirugía)**: Tras la aprobación, ejecutar las eliminaciones y consolidaciones, manteniendo la firma oficial e identidad sin emojis genéricos.

---
> Un Soñador con Poca RAM & Misael
