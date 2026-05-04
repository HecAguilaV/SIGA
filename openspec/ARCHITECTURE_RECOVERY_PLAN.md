# Architecture Recovery Plan: Transition to Hexagonal

## Status Audit (May 3, 2026)
The current implementation of SIGA microservices (`inventory`, `sales`) follows a **Coupled Layered Architecture** (anti-pattern):
- **Entities**: Coupled to JPA (`@Entity`). These are not Domain Models.
- **Controllers**: Coupled to Repositories. No Use Case or Service layer.
- **Consumers**: Coupled to both Kafka and Persistence logic. Business logic is trapped in Infrastructure Adapters.
- **Contract**: The DB schema is being leaked as the REST API contract.

## Goal
Decouple the Business Domain from Spring Boot and JPA to ensure long-term stability and scalability to Big Data/Lambda architectures.

## Strategy: The Strategic Boilerplate
We will implement the **Hexagonal Architecture** pattern:
1. **Domain Layer**: Pure Kotlin classes for logic. No frameworks.
2. **Application Layer**: Use Cases that orchestrate the domain using **Ports** (Interfaces).
3. **Infrastructure Layer**: 
   - **Adapters In**: Controllers, Kafka Consumers (calling Use Cases).
   - **Adapters Out**: Persistence implementations (JPA), Kafka Producers.

## Immediate Next Steps (Inventory Service)
1. **Model Extraction**: Create `com.siga.inventory.domain.model.Product` (pure class).
2. **Port Definition**: Create `com.siga.inventory.domain.port.ProductRepositoryPort`.
3. **Mapper Implementation**: Create mappers to transform `ProductEntity` <-> `Product`.
4. **Use Case Migration**: Move stock reservation logic from `SaleEventConsumer` to `com.siga.inventory.application.usecase.ReserveStockUseCase`.

## Infrastructure Impact
- **Docker Compose**: NO IMPACT. Architecture changes are internal to the JVM. The containers, ports, and volumes remain identical.
- **Database**: NO IMPACT. The schema remains the same; only the way we interact with it from code changes.
