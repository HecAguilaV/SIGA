# Observabilidad Maestría (Prometheus, Grafana, Loki)

Este protocolo asegura que SIGA sea transparente. No adivinamos qué falla, lo vemos en tiempo real.

## 📈 Métricas (Prometheus)
- **Exposición**: Usar Spring Boot Actuator para exponer el endpoint `/actuator/prometheus`.
- **Custom Metrics**: Definir métricas de negocio (ej. "ventas_procesadas_total", "errores_asistente_ia").
- **Alertmanager**: Configurar alertas críticas para latencia > 2s o errores 500.

## 🎨 Visualización (Grafana)
- **Dashboards Operativos**: Estado de los microservicios, uso de CPU/RAM.
- **Dashboards de Negocio**: Ventas del día, mermas detectadas.
- **Unificación**: Grafana es el portal único para métricas y logs.

## 📜 Logging Centralizado (Loki)
- **Promtail**: Capta los logs de los contenedores Docker.
- **Labeling**: Etiquetar logs por `service_name`, `env` (dev/prod) y `trace_id`.
- **Correlación**: Un ID de traza debe unir la métrica con el log exacto en Grafana.

## 🛡️ Reglas de Oro de Observabilidad
1.  Si no se mide, no existe.
2.  Logs sin contexto son ruido.
3.  Gráficos sin acciones son decoración.
