# Admin Portal (Backoffice)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

This service is the central administration interface for the **SIGA** platform owners and operators.

## Purpose
Provide a global system view, tenant management, microservice health monitoring, and high-level analytics.

## Responsibilities
- **Tenant Management**: Lifecycle control of SME accounts (Activation, Deactivation, Payment suspension).
- **Infrastructure Monitoring**: Microservice health status and network latency by region.
- **Load Analytics**: Transaction volume processed (without access to financial amounts).
- **Audit Governance**: High-level access oversight (via `siga-common`) to ensure system integrity.

## Legal Compliance and Privacy (Law 21.719)
This portal is designed under the **Privacy by Design** principle:
- **Zero-Knowledge Architecture**: The platform administrator has **NO** visibility into financial amounts, end-customer details, or stock levels of SMEs.
- **Data Sovereignty**: Each SME is the absolute owner of its database. The Backoffice only manages service "capacity" and "availability".
- **Total Isolation**: Access to sensitive business data is prohibited, strictly complying with Chilean personal data protection regulations.

## Technical Details (Proposal)
- **Stack**: [PENDING DEFINITION] (Recommended: React or SvelteKit).
- **API Consumption**: Communicates exclusively through `siga-gateway`.
- **Authentication**: Integration with `siga-auth` using `ADMIN_MASTER` roles.

## Interconnections (Technical Flows)

The Admin Portal has no business database of its own; it consumes data from microservices through `siga-gateway`.

### 1. With Sales Microservice (`siga-sales`)
- **How**: Throughput monitoring and SAGA event volume.
- **Clear Example**: The SIGA admin sees how many "Sales Events" have been processed successfully to ensure the system is not saturated.
  - *Behind the scenes*: The portal calls `GET /api/v1/sales/metrics/throughput`.

### 2. With Inventory Microservice (`siga-inventory`)
- **How**: Data consistency oversight and Kafka broker health.
- **Clear Example**: Verify that stock reservation events are being processed without lag for all SMEs.
  - *Behind the scenes*: The portal queries lag metrics on inventory topics.

### 3. With Authentication Microservice (`siga-auth`)
- **How**: High-level access control and tenant management.
- **Clear Example**: An SME has not paid their subscription and the admin must suspend their system access.
  - *Behind the scenes*: The portal executes `PATCH /api/v1/auth/tenants/{uuid}/status` with status `SUSPENDED`.

### 4. With Audit Microservice (`siga-common`)
- **How**: Activity trace visualization for legal compliance.
- **Clear Example**: During a Law 21.719 audit, the admin searches who modified the price of product X in SME Y.
  - *Behind the scenes*: The portal queries centralized logs filtered by `entity_type: PRODUCT`.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
