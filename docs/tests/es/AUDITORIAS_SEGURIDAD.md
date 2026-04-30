# Auditorías de Seguridad

SIGA utiliza un enfoque proactivo de "Desplazamiento a la Izquierda" (Shift-Left Security) para detectar vulnerabilidades antes de que lleguen a producción.

## Gitleaks: Escaneo de Secretos

Utilizamos Gitleaks para prevenir la exposición accidental de API Keys, contraseñas o certificados en el historial de Git.

### Configuración Personalizada (`.gitleaks.toml`)
Para mantener un entorno de desarrollo ágil y sin "ruido", hemos implementado una **Lista de Permisos (Allowlist)** estratégica que ignora falsos positivos comunes:
- **Herramientas de seguridad**: Carpeta `.tools/` y `semgrep/`.
- **Archivos multimedia**: SVGs, Favicons y archivos binarios que pueden contener cadenas que parecen secretos.
- **Entornos virtuales**: `.venv/`.

### Cómo ejecutarlo:
```bash
gitleaks detect --verbose
```

## Semgrep: Análisis Estático (SAST)

Semgrep escanea el código en busca de patrones inseguros (ej: inyecciones SQL, uso de criptografía débil).

### Alcance:
- **Microservicios Kotlin/Spring**: Vigilancia sobre la configuración de seguridad y persistencia.
- **Infraestructura como Código (IaC)**: Revisión de Docker Compose y archivos de configuración.

### Gestión de Hallazgos:
1. **Hallazgo Real**: Se corrige de inmediato siguiendo los principios de Arquitectura Limpia (Clean Architecture).
2. **Falso Positivo**: Se documenta y se añade a la configuración de Semgrep si es recurrente.

---
*SIGA - Sistema Inteligente de Gestión de Activos - Estrategia de Seguridad Primero*

## Bitácora de Auditorías

### Auditoría #1: Auth (Migración UUID)
**Fecha**: 2026-04-29
**Hallazgos**: Limpio. Se validó la migración a UUID y la persistencia bilingüe.

### Auditoría #2: Inventory (Migración UUID)
**Fecha**: 2026-04-30
**Herramientas**: Gitleaks, Semgrep
**Hallazgos**:
- **Gitleaks**: 100% Falsos Positivos en documentación (`docs/`, `openspec/`).
- **Semgrep**: Hallazgo en `Dockerfile` (corre como `root`).
**Estado**: Validado con advertencia en Docker.

### Auditoría #3: Billing (UUID y Hexagonal)
**Fecha**: 2026-04-30
**Herramientas**: Gitleaks, Semgrep, Arneses TDD
**Hallazgos**:
- **Gitleaks**: Limpio tras ignorar archivos binarios y multimedia.
- **Semgrep**: Sin hallazgos de seguridad en el nuevo adaptador de Transbank.
- **Arquitectura**: Se validó el desacoplamiento total mediante el puerto `PaymentGateway`.
**Estado**: EXITOSO.
