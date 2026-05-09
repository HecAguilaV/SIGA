# Security Audits

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../es/AUDITORIAS_SEGURIDAD.md)*

SIGA uses a proactive "Shift-Left Security" approach to detect vulnerabilities before they reach production.

## Gitleaks: Secrets Scanning

We use Gitleaks to prevent accidental exposure of API Keys, passwords, or certificates in the Git history.

### Custom Configuration (`.gitleaks.toml`)
To maintain an agile development environment without "noise", we have implemented a strategic **Allowlist** that ignores common false positives:
- **Security Tools**: `.tools/` and `semgrep/` folders.
- **Multimedia Files**: SVGs, Favicons, and binary files that may contain strings that look like secrets.
- **Virtual Environments**: `.venv/`.

### How to run it:
```bash
gitleaks detect --verbose
```

## Semgrep: Static Analysis (SAST)

Semgrep scans the code for insecure patterns (e.g., SQL injections, use of weak cryptography).

### Scope:
- **Kotlin/Spring Microservices**: Vigilance over security configuration and persistence.
- **Infrastructure-as-Code**: Review of Docker Compose and configuration files.

### Findings Management:
1. **Real Finding**: Corrected immediately following Clean Architecture principles.
2. **False Positive**: Documented and added to the Semgrep configuration if recurrent.

---
*SIGA - Intelligent Asset Management System - Security First Strategy*

## Audit Log

### Audit #1: Auth (UUID Migration)
**Date**: 2026-04-29
**Findings**: Clean. Validated UUID migration and bilingual persistence.

### Audit #2: Inventory (UUID Migration)
**Date**: 2026-04-30
**Tools**: Gitleaks, Semgrep
**Findings**:
- **Gitleaks**: 100% False Positives in documentation (`docs/`, `openspec/`).
- **Semgrep**: Finding in `Dockerfile` (runs as `root`).
**Status**: Validated with Docker warning.

### Audit #3: Billing (UUID & Hexagonal)
**Date**: 2026-04-30
**Tools**: Gitleaks, Semgrep, TDD Harnesses
**Findings**:
- **Gitleaks**: Clean after ignoring binary and multimedia files.
- **Semgrep**: No security findings in the new Transbank adapter.
- **Architecture**: Validated full decoupling through the `PaymentGateway` port.
**Status**: SUCCESSFUL.
