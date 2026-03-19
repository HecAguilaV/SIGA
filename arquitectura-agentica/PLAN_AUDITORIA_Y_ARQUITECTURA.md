# Plan de Auditoría y Propuesta de Arquitectura (SIGA)

Este documento detalla la hoja de ruta para la transformación técnica de SIGA hacia un estándar de excelencia.

---

## 🔍 FASE 1: Auditoría de "Limpieza Extrema"
Analizaremos el código actual del backend buscando:
- **Acoplamiento Directo**: Evitaremos que los controladores toquen la base de datos sin pasar por el dominio.
- **Lógica de Negocio Fugada**: Recuperaremos validaciones que hoy están dispersas.
- **Blindaje de Seguridad**: Aseguraremos que cada consulta esté protegida contra Inyección SQL.

## 🏗️ FASE 2: Propuesta de Microservicios (Dominio Sólido)
Dividiremos la lógica actual en servicios independientes y blindados:

1.  **🚀 SIGA-Seguridad**: Gestión de identidades y accesos (Seguridad JWT).
2.  **📦 SIGA-Inventario**: El corazón del sistema (stock, movimientos, mermas). 
3.  **🛒 SIGA-Ventas-POS**: Lógica de transacciones y carritos.
4.  **🧠 SIGA-Asistente-IA**: Orquestador de la inteligencia conversacional.
5.  **🏦 SIGA-Comercial**: Gestión de suscripciones y facturación.

## 📡 FASE 3: Orquestación y Observabilidad
- **API Gateway**: Punto de entrada único.
- **Service Discovery**: Localización automática de servicios.
- **Loki/Prometheus/Grafana**: Monitoreo en tiempo real de la "salud" del sistema.

---
## 🏁 Próximos Pasos (¡Dale!)
1.  Auditoría profunda del módulo de **Inventario**.
2.  Aplicación de **Filosofía Haiku** para simplificar el código.
3.  Implementación del primer Pipeline de CI/CD.

---
> Un Soñador con Poca RAM 👨🏻💻 & Misael
