# Manifiesto de Seguridad SIGA

## 1. Visión de Seguridad
SIGA se rige bajo el principio de **Confianza Cero (Zero-Trust)**. Ninguna petición es de confianza por defecto, ya sea interna o externa. La seguridad no es una capa superficial, sino la base estructural de nuestra arquitectura distribuida.

## 2. Marco Legal y Cumplimiento (Ley 21.719)
Este sistema ha sido diseñado para cumplir con la **Ley 21.719 (Ley Marco de Ciberseguridad y Protección de Datos Personales)** de Chile. Implementamos los siguientes artículos de forma técnica:

*   **Protección desde el Diseño (Art. 14 quáter):** La arquitectura de microservicios garantiza que los datos personales estén aislados en esquemas independientes, limitando el radio de exposición.
*   **Seudonimización y Cifrado (Art. 14 quinquies):** El uso obligatorio de **UUID v4** en lugar de IDs secuenciales evita el escaneo de datos y actúa como una medida de seudonimización técnica.
*   **Deber de Confidencialidad:** Todo flujo de datos entre microservicios viaja cifrado y bajo validación de identidad centralizada.

## 3. Pilares Técnicos de Protección

### A. Aislamiento Multi-tenant
El aislamiento es **físico-lógico**. Aunque compartamos el motor de base de datos PostgreSQL, cada empresa (Tenant) opera en un vacío:
- Cada consulta SQL está forzada por el interceptor del Gateway a incluir el `tenant_id`.
- Es técnicamente imposible que un usuario de la Empresa A visualice registros de la Empresa B, incluso si conoce el ID del producto.

### B. Arquitectura Zero-Trust
- **Puerta Única:** El API Gateway es el único punto de entrada. Los microservicios internos no tienen IP pública.
- **Validación JWT:** Cada petición debe portar un token válido y vigente. El Gateway rechaza cualquier paquete sin firma digital comprobable.

### C. Gobernanza de Agentes IA (AI Safety)
La Inteligencia Artificial en SIGA no tiene privilegios propios:
- **Herencia de Permisos:** El Agente IA opera bajo el mismo "paraguas de seguridad" que el usuario humano que lo invoca.
- **Restricción CRUD:** Si un usuario no tiene permiso para borrar stock, el Agente recibirá un `Access Denied` por parte de los servicios centrales si intenta ejecutar esa acción.

## 4. Resiliencia y Reporte
En cumplimiento con el **Art. 14 sexies**, SIGA integrará un sistema de logs de auditoría inmutables para detectar y reportar vulneraciones de seguridad de forma inmediata a las autoridades competentes y a los usuarios afectados.

---
*Este manifiesto es el contrato de seguridad que protege la integridad de SIGA y de sus clientes.*
