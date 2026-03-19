# Caso Vida Real: SIGA (Sistema Inteligente de Gestión de Activos)

## 🏛️ 1. Contexto del Proyecto
SIGA no es solo un software de inventario; es una respuesta a la parálisis operativa que sufren las Micro, Pequeñas y Medianas Empresas (PYMES). A diferencia de los sistemas tradicionales que operan sobre la premisa de "alimentar la base de datos", SIGA opera sobre la premisa de **"recuperar el tiempo de la persona"**.

El proyecto nace de la experiencia real de Héctor como operario, capturando la necesidad de emprendedores que cumplen múltiples roles (dueño, operario, vendedor) y que hoy dependen de herramientas manuales o sistemas obsoletos que no permiten la movilidad ni la toma de decisiones con sentido.

## 🩸 2. Problemática Detectada (El Dolor)
- **Fricción Cognitiva**: Sistemas complejos que requieren capacitación extensa.
- **Pérdida de Capital**: Quiebres de stock no detectados y mermas sin trazabilidad.
- **Inexistencia de Movilidad**: El emprendedor no puede gestionar su negocio "en ruta" o desde el mostrador sin complicaciones.
- **Datos sin Acción**: El usuario tiene datos, pero no sabe qué hacer con ellos para mejorar su vida.

## 🏹 3. Propuesta de Valor (Framework Héctor)
SIGA propone un **Asistente Proactivo** que reduce la interacción hombre-máquina al mínimo necesario:
1.  **Capa Humana**: El sistema instruye y acompaña, no solo registra. El éxito es la transformación de la persona.
2.  **Conversación Inteligente**: Uso de IA (Gemini) para dictar stock y recibir reportes en lenguaje natural.
3.  **Simplicidad Radical**: Una experiencia de usuario diseñada para que el tiempo sea recuperado.

## 🏗️ 4. Desafío Técnico (Arquitectura SIGA)
El proyecto se estructura en una arquitectura de **Microservicios** para garantizar escalabilidad y blindaje:
- **Backend (Kotlin/Spring Boot)**: El núcleo lógico con Arquitectura Limpia.
- **BFF (Backend para Frontends)**: Adaptadores específicos para Web y Móvil.
- **Infraestructura**: Orquestación con Docker, monitoreo con Prometheus/Grafana y automatización real de despliegue.

---
> Un Soñador con Poca RAM 👨🏻💻 & Misael
