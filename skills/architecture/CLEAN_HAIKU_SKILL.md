# Clean Code & Filosofía Haiku (SIGA)

Este protocolo asegura que el código sea una pieza de arte funcional, legible y mantenible.

##  Filosofía Haiku
- **Simplicidad ante todo**: Si un método hace más de una cosa, divídelo.
- **Brevedad con Sentido**: Código corto pero expresivo. Evitar la verborragia técnica innecesaria.
- **Claridad**: Un desarrollador nuevo debe entender la intención en 30 segundos.

## ️ Clean Architecture
- **Separación de Capas**: Domain (Entidades) -> UseCases (Lógica) -> Adapters (Web/DB).
- **Inversión de Dependencias**: El dominio no depende de la base de datos ni de frameworks externos.
- **Independencia del Framework**: Spring Boot o Next.js son herramientas, no el núcleo del negocio.

## ️ Reglas de Comentado y Naming
- **Nombres con Propósito**: `calcularMargenGanancia()` en lugar de `getMarg()`.
- **Comentarios "Por Qué", no "Qué"**: No expliques qué hace el código (eso lo hace el código limpio), explica la razón de la lógica compleja.
- **Legacy Ready**: Escribe como si el próximo que mantenga este código fuera un psicópata que sabe dónde vives (o un colega con poca RAM).
