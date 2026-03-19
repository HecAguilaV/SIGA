# Blindaje y Manejo Seguro (Security Skill)

Protocolo para proteger la integridad de SIGA y la privacidad del usuario.

## 🛡️ SQL Injection & Data Security
- **Prepared Statements**: NUNCA concatenar strings en consultas SQL. Usar parámetros bind.
- **Validación de Tipos**: Sanitizar cada input antes de que llegue a la capa de persistencia.
- **ORM Seguro**: Usar las capacidades de JPA/Hibernate para evitar queries crudas si no es estrictamente necesario.

## 🚫 Manejo de Errores (Error Leaks)
- **Global Exception Handler**: Capturar errores en un punto central.
- **Máscara de Error**: El usuario y la consola del navegador nunca verán el StackTrace ni detalles del servidor (ej. "Error interno" para el cliente, log detallado para Misael).
- **Cero Datos Sensibles**: No imprimir tokens, passwords o IDs internos en `console.log` o logs de producción.

## 🧹 Limpieza de Consola
- **No Spam**: Solo logs informativos esenciales en desarrollo.
- **Debug Flags**: Usar flags para activar logs de depuración solo cuando sea necesario.
