# Skill Registry: SIGA (V4.0)

> Updated: 2026-05-01 | Stack: Kotlin + Spring Boot + Kafka

## Global User Skills
| Skill | Trigger | Description |
| :--- | :--- | :--- |
| **branch-pr** | creating PR, opening PR | PR creation workflow following issue-first. |
| **go-testing** | Go tests, Bubbletea | Go testing patterns (N/A for this project). |
| **issue-creation** | creating GitHub issue | Issue creation workflow. |
| **judgment-day** | judgment day, juzgar | Parallel adversarial review protocol. |
| **sdd-*** | sdd phases | Spec-Driven Development phases. |
| **skill-creator** | create new skill | Create new AI agent skills. |

## Project Conventions (auto-resolved)
- **Architecture**: Hexagonal Architecture (Ports & Adapters), Microservices Monorepo.
- **Security**: Law 21.719 Compliance (UUID v4 Mandatory for all PKs).
- **Testing**: Strict TDD Mode. Kotest + MockK (Inventory), JUnit 5 (Sales). JaCoCo coverage.
- **Paths**: API Versioning `/api/v1/{service}/{resource}`.
- **Events**: Apache Kafka (Confluent). SAGA Choreography pattern.
- **CI/CD**: GitHub Actions (Docker matrix build + security scans).
- **Commits**: Spanish, Conventional Commits format.

## Compact Rules
```markdown
### Project Standards
1. Use UUID v4 for ALL primary and foreign keys (Ley 21.719).
2. Implement Hexagonal Architecture for external integrations.
3. API paths: /api/v1/{service}/{resource}.
4. Tests: BDD naming `given_{context}_when_{action}_then_{result}`.
5. Kafka events: include eventId (UUID), saleId, tenantId, timestamp. Use JSON serialization.
6. Idempotency: Mandatory check in `processed_events` table (eventId PK) before processing any consumer logic.
7. SAGA: Sales -> Inventory choreography. Inventory MUST emit STOCK_RESERVED or STOCK_FAILED. Sales MUST update status.
8. Testing: All SAGA steps must have integration tests with @EmbeddedKafka.
9. Commits in Spanish, Conventional Commits format.
```
