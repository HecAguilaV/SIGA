# SIGA: Quality and Testing Strategy

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../es/README.md)*

This directory contains the strategic documentation for SIGA system testing. Our objective is to ensure security, stability, and compliance with **Law 21.719 (Data Pseudonymization)** through a bilingual and professional approach.

## Quality Philosophy
1. **Security by Design**: No code is written without considering credential shielding.
2. **Strict TDD**: We follow the Red-Green-Refactor cycle for every structural change (such as the UUID migration).
3. **Strategic Memory**: Every found and overcome failure must be documented to prevent regressions.

## SIGA Testing Pyramid

### 1. Static Audit (Security Scans)
- **Gitleaks**: Preventive secrets scanning in Git history.
- **Semgrep**: Static code analysis to detect insecure security patterns.
- *Documentation:* [SECURITY_AUDITS.md](SECURITY_AUDITS.md)

### 2. Integration Tests (Harness)
- **BaseIntegrationTest**: Our "master key" for testing microservices with Spring Boot, MockMvc, and H2.
- **UUID Validation**: We ensure that every generated entity complies with the 128-bit standard.
- *Documentation:* [INTEGRATION_HARNESS.md](INTEGRATION_HARNESS.md)

### 3. Persistence Tests
- Validation of multiple schemas in PostgreSQL and their emulation in H2.
- Bilingual referential integrity tests.

---
*SIGA - Intelligent Asset Management System*
