# Skill Registry: SIGA (V3.0)

## Global User Skills
| Skill | Trigger | Description |
| :--- | :--- | :--- |
| **branch-pr** | creating PR, opening PR | PR creation workflow following issue-first. |
| **go-testing** | Go tests, Bubbletea | Go testing patterns (for Go services). |
| **issue-creation** | creating GitHub issue | Issue creation workflow. |
| **judgment-day** | judgment day, juzgar | Parallel adversarial review protocol. |
| **sdd-*** | sdd phases | Spec-Driven Development phases. |
| **skill-creator** | create new skill | Create new AI agent skills. |

## Project Conventions (auto-resolved)
- **Architecture**: Hexagonal Architecture (Ports & Adapters).
- **Security**: Law 21.719 Compliance (UUID Mandatory).
- **Testing**: Strict TDD Mode. JUnit 5, MockMvc, H2 Multi-schema.
- **Paths**: API Versioning `/api/v1/`.

## Compact Rules
```markdown
### Project Standards
1. Use UUID for ALL primary and foreign keys.
2. Implement Hexagonal Architecture for external integrations (Payments, Mail).
3. API paths must follow /api/v1/{service}/{resource}.
4. Every PR/Change must be verified with Integration Tests.
```
