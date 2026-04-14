# Skill Registry: SIGA

This registry tracks available skills for the SIGA project, combining global and project-specific patterns.

## Global Skills

- **branch-pr**: PR creation workflow following issue-first enforcement.
- **go-testing**: Go testing patterns for Gentleman.Dots.
- **issue-creation**: GitHub issue reporting and feature requests.
- **judgment-day**: Parallel adversarial review protocol.
- **sdd-***: Full Spec-Driven Development cycle (Explore, Propose, Spec, Design, Tasks, Apply, Verify, Archive).
- **skill-creator**: Tool for creating new AI agent skills.
- **skill-registry**: Maintenance of this file.

## Project Skills

- **qa-testing-protocol**: QA best practices and UI testing strategies for SIGA.
  - Trigger: Phase verify, smoke tests, accessibility audits.
  - Source: [SKILL.md](file:///Users/hector/Desktop/PROYECTOS/SIGA/skills/qa/SKILL.md)
- **jpa-entity-mapping-kotlin**: JPA & Kotlin entity design rules.
  - Trigger: writing JPA entities in Kotlin, Spring Data repositories.
  - Source: [SKILL.md](file:///Users/hector/Desktop/PROYECTOS/SIGA/skills/kotlin/SKILL.md)
- **supabase-postgres-best-practices**: Postgres optimization for Supabase.
  - Trigger: SQL queries, schema design, RLS.
  - Source: [SKILL.md](file:///Users/hector/Desktop/PROYECTOS/SIGA/skills/supabase/SKILL.md)
- **microservices-design-patterns**: Architecture guidelines for distributed services.
  - Trigger: microservice extraction, communication patterns, resiliency.
  - Source: [SKILL.md](file:///Users/hector/Desktop/PROYECTOS/SIGA/skills/architecture/SKILL.md)
- **code-reviewer**: Professional code review criteria.
  - Trigger: code review, PR feedback.
  - Source: [SKILL.md](file:///Users/hector/Desktop/PROYECTOS/SIGA/skills/google-gemini/SKILL.md)

## Compact Rules

### General
- Use Hexagonal/Clean Architecture patterns.
- Follow Conventional Commits in Spanish.
- Prioritize "Organización Documental" and "Centralización de la Verdad".

### Backend (Kotlin)
- Do NOT use `data class` for JPA entities.
- Keep DTOs and entities separate.
- Use Gradle Kotlin DSL.

### Frontend (SvelteKit/React)
- Use SvelteKit for the main webapp.
- Use Bulma/React for commercial services.
- Follow Atomic Design principles.
