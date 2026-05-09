# SIGA Security Manifesto

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../../es/security/MANIFIESTO_SEGURIDAD.md)*

## 1. Security Vision
SIGA operates under the **Zero-Trust** principle. No request is trusted by default, whether internal or external. Security is not a surface layer but the structural foundation of our distributed architecture.

## 2. Legal Framework and Compliance (Law 21.719)
This system is designed to comply with **Law 21.719 (Framework Law on Cybersecurity and Personal Data Protection)** of Chile. We implement the following articles technically:

*   **Privacy by Design (Art. 14 quáter):** The microservices architecture ensures that personal data is isolated in independent schemas, limiting the blast radius of any potential exposure.
*   **Pseudonymization and Encryption (Art. 14 quinquies):** Mandatory use of **UUID v4** instead of sequential IDs prevents data crawling and acts as a technical pseudonymization measure.
*   **Duty of Confidentiality:** All data flows between microservices are encrypted and subject to centralized identity validation.

## 3. Technical Protection Pillars

### A. Multi-tenant Isolation
Isolation is **physical-logical**. Although we share the PostgreSQL database engine, each company (Tenant) operates in a vacuum:
- Every SQL query is forced by the Gateway interceptor to include the `tenant_id`.
- It is technically impossible for a user from Company A to view records from Company B, even if they know the product ID.

### B. Zero-Trust Architecture
- **Single Entry Point:** The API Gateway is the sole entry point. Internal microservices do not have public IPs.
- **JWT Validation:** Every request must carry a valid and current token. The Gateway rejects any packet without a verifiable digital signature.

### C. AI Agent Governance (AI Safety)
Artificial Intelligence in SIGA has no privileges of its own:
- **Permission Inheritance:** The AI Agent operates under the same "security umbrella" as the human user who invokes it.
- **CRUD Restriction:** If a user does not have permission to delete stock, the Agent will receive an `Access Denied` from central services if it attempts to execute that action.

## 4. Resilience and Reporting
In compliance with **Art. 14 sexies**, SIGA will integrate an immutable audit log system to detect and report security breaches immediately to competent authorities and affected users.

---
*This manifesto is the security contract that protects the integrity of SIGA and its clients.*
