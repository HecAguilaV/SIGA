# Systematic Debugging (Superpower)

Este protocolo rige CUALQUIER resolución de fallos en SIGA, priorizando la investigación de la causa raíz sobre los "parches" rápidos.

## ️ La Ley de Hierro
> **"NO HAY FIXES SIN INVESTIGACIÓN PREVIA DE LA CAUSA RAÍZ"**

##  Las 4 Fases
1.  **Investigación**: Leer errores, reproducir consistentemente, trazar flujo de datos.
2.  **Análisis de Patrones**: Comparar con ejemplos que SÍ funcionan. Identificar diferencias.
3.  **Hipótesis**: "Creo que X es la causa porque Y". Probar con el cambio más pequeño posible.
4.  **Implementación**: Crear test que falle, aplicar fix, verificar.

##  Banderas Rojas (STOP)
- "Un fix rápido por ahora".
- "Solo prueba cambiando X a ver si sirve".
- "Si 3+ intentos fallan: Cuestiona la arquitectura, no sigas parchando".
