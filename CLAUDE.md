# NutriCare API

## Project Overview

NutriCare API is a Spring Boot backend for a nutrition-care application — it manages user health/nutrition **Profiles** (adult, child, elderly, pregnant, breastfeeding, infant) and will drive nutrition recommendations from them. It is built with **Clean Architecture** (2 physical top-level packages, 4 conceptual dependency rings) and **Domain-Driven Design (DDD)**. The goal of this document is to keep contributions — human or AI — consistent with those principles.

- **Language / stack:** Java 21, Spring Boot 4.0.7 (Data JPA, Validation, Flyway, HATEOAS, WebMVC, springdoc-openapi for API docs), PostgreSQL (runtime) / H2 (tests), JUnit 5 + AssertJ + Mockito, ArchUnit 1.5.0 for architecture enforcement.
- **Bounded contexts:** currently a single context (user nutrition Profiles). Sub-package by bounded context under `domain`/`application` only once a second context appears — don't pre-split.
- **Primary entry points:** Spring Boot HTTP API (`NutricareApiApplication`, `com.nutricare.nutricare_api.infrastructure.config`).

## Architectural Principles

### The Dependency Rule

Source code dependencies point **inward only**. Inner layers must never import from, or know about, outer layers.

```
infrastructure.config     (Frameworks & Drivers — outermost)
        ↓
infrastructure.adapter    (Interface Adapters)
        ↓
core.application          (Application / Use Cases)
        ↓
core.domain                (Domain / Entities — innermost)
```

This project uses **2 physical top-level packages** (`core`, `infrastructure`), each split into the two rings it hosts:

- **`core.domain`** — Entities, value objects, domain services. Zero dependencies on frameworks, databases, HTTP, or any library beyond the JDK and a small allowed set (`org.slf4j..`, `lombok..` — a logging facade and a compile-time-only annotation processor, neither of which couples the domain to a concrete framework).
- **`core.application`** — Use cases/application services. Depends only on `core.domain`. Orchestrates domain objects via repository/port interfaces; contains no business logic of its own.
- **`infrastructure.adapter`** — Controllers, repository implementations (JPA), external clients, DTO/mappers. Depends on `core.domain` and `core.application`, implements the ports they define.
- **`infrastructure.config`** — Spring Boot entry point (`NutricareApiApplication`) and `@Configuration`/`@Bean` wiring. Depends on everything else. **Nothing depends on it** — this is also why `@SpringBootApplication` lives here rather than at the project root: its implicit component scan is deliberately confined to `infrastructure..`, so `core` stays framework-agnostic and beans that wire `core` classes must be defined explicitly, not discovered via `@Component`/`@Service` annotations on domain/application classes.

If you're about to add an import that points outward (e.g. `core.domain` importing a JPA `@Entity`, or a use case importing a Spring `HttpServletRequest`), stop — that's a layer violation. Introduce a port/interface instead and implement it in `infrastructure`.

**This rule is enforced by a build-breaking test, not just convention:** `src/test/java/com/nutricare/nutricare_api/archunit/CleanArchitectureTest.java` uses ArchUnit's `layeredArchitecture()` to check inward-only dependencies across all 4 rings, plus a whitelist rule restricting `core` to JDK + the allowed utility libraries above. Run `./mvnw test` after any package move — a layer violation fails here before it fails anywhere else.

### Folder Structure

```
src/main/java/com/nutricare/nutricare_api/
  core/
    domain/
      entity/            # Entities, value objects (Profile, GroupType, SexType, ActivityLevel, ...)
      # add: repository/ (interfaces only), domain-service/, domain-event/ as they're needed
    application/          # use cases, ports (interfaces the app layer needs), dto — not created yet
  infrastructure/
    adapter/               # controllers, JPA repository impls, mappers — not created yet
    config/                 # NutricareApiApplication, @Configuration/@Bean wiring — not created yet
```

Don't invent a `<bounded-context>` or `interfaces`/`presentation` top-level package — this project intentionally merges Interface Adapters and Frameworks & Drivers into `infrastructure`, and Entities/Use Cases into `core`. If you're unsure where new code goes, match this structure rather than the generic 4-top-level-folder layout DDD tutorials often show.

## DDD Building Blocks — Definitions and Rules

| Concept                            | Rule                                                                                                                                                                                                                    |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Entity**                         | Has a stable identity (an ID), not defined by its attributes. Identity comparison, not structural comparison.                                                                                                           |
| **Value Object**                   | Immutable, defined entirely by its attributes, no identity. Always validate invariants in the constructor/factory — a Value Object should never exist in an invalid state.                                              |
| **Aggregate**                      | A cluster of entities/value objects treated as a single consistency boundary. Has one **Aggregate Root**, the only object outer layers may reference directly. All invariants for the aggregate are enforced inside it. |
| **Aggregate Root**                 | The entry point of an aggregate. External code must go through it — never reach into an aggregate's internals directly.                                                                                                 |
| **Domain Event**                   | Represents something meaningful that happened in the domain. Named in past tense (`ProfileCreated`, `TrimesterUpdated`). Raised by aggregates, published by infrastructure.                                             |
| **Domain Service**                 | Stateless logic that doesn't naturally belong to one entity/value object (often because it spans multiple aggregates).                                                                                                  |
| **Repository**                     | Interface defined in `core.domain` or `core.application`, one per aggregate root. Implementation lives in `infrastructure.adapter`. Repositories persist and reconstitute whole aggregates — never partial ones.        |
| **Application Service / Use Case** | Orchestrates a single business operation: loads aggregate(s) via repository, calls domain methods, persists changes, publishes events. Contains **no business logic** of its own — that belongs in the domain.          |
| **Factory**                        | Encapsulates complex creation logic for entities/aggregates, especially when creation requires enforcing invariants across multiple objects.                                                                            |
| **Specification**                  | Encapsulates a business rule as an object that can be combined and reused, especially for querying or validation.                                                                                                       |

### Ubiquitous Language

Code should read like the business talks. Class, method, and variable names must match the terms used by domain experts — not generic CRUD nouns. `Profile`, `GroupType`, `ActivityLevel` are the current ubiquitous-language terms (see Glossary); extend this vocabulary rather than introducing generic synonyms (`UserRecord`, `Category`, etc.).

## Rules for Claude When Working in This Repo

1. **Respect the dependency rule.** Before adding an import, check it points from `infrastructure.config` → `infrastructure.adapter` → `core.application` → `core.domain`, never the reverse. If a use case needs infrastructure (DB, email, clock, ID generation), define a port/interface in `core.application` (or `core.domain`) and implement it in `infrastructure.adapter`.
2. **Put business rules in the domain layer.** If you find yourself writing `if` statements that encode business policy inside a controller, use case, or repository implementation, move that logic into an entity, value object, or domain service instead. Example already in the codebase: `Profile.setTrimester()` enforces "only a `PREGNANT` profile may have a trimester, and it must be 1–3" — that's exactly where this kind of rule belongs.
3. **Never let outer-layer types leak inward.** `core.domain`/`core.application` must not reference JPA `@Entity`/`@Repository` types, Spring `HttpServletRequest`/`ResponseEntity`, or other framework-specific types. Use DTOs/mappers at the `infrastructure.adapter` boundary.
4. **One repository per aggregate root.** Don't create repositories for entities that are only accessed through an aggregate root.
5. **Keep aggregates small.** Prefer referencing other aggregates by ID, not by object reference. Cross-aggregate consistency is handled via domain events / eventual consistency, not by expanding the aggregate.
6. **Value Objects are immutable.** Any "change" produces a new instance; never mutate in place. (Entities like `Profile` may mutate via explicit, invariant-checked setters — that's expected; Value Objects like `GroupType`/`SexType` never should.)
7. **Validate invariants at construction.** Entities and Value Objects should be impossible to construct in an invalid state — validate inside constructors/factory methods, not after the fact.
8. **Domain validation failures are unchecked, domain-specific exceptions — not `Exception`.** Don't throw the raw `java.lang.Exception` type (see `Profile.setTrimester()` for what to avoid going forward) — callers can't catch it selectively and it's invisible to ArchUnit's "core only depends on JDK/allowed utilities" rule as a *semantic* signal even though it compiles fine. Define a small unchecked exception type in `core.domain` (e.g. `InvalidTrimesterException`) instead.
9. **Use cases orchestrate, they don't decide.** An application service loads data, delegates to the domain, persists results, and raises events. It should not contain conditional business logic.
10. **Match existing bounded-context boundaries.** Don't introduce a dependency from one bounded context's domain into another's. Cross-context communication goes through well-defined interfaces or events.
11. **Naming follows the ubiquitous language**, not technical/generic terms. Prefer `Profile.setTrimester()` over a generic `ProfileService.updateField(profile, "trimester", value)`.
12. **Tests mirror the architecture.** Domain logic gets fast, framework-free unit tests. Use cases get tests with mocked ports. Infrastructure/integration tests are separate and slower — don't blend the two. Never weaken `CleanArchitectureTest` (e.g. adding a package to the allowed-dependencies whitelist, or removing a layer rule) to make a violation pass — fix the dependency direction instead.
13. **When unsure which layer something belongs in**, default to pushing it inward (toward `core.domain`) rather than outward, and flag the uncertainty rather than guessing silently.

## Commands

```bash
# Run all tests (includes the architecture rules in CleanArchitectureTest)
./mvnw test          # macOS/Linux
.\mvnw.cmd test       # Windows PowerShell

# Run only the architecture tests
./mvnw test -Dtest=CleanArchitectureTest

# Run the app locally
./mvnw spring-boot:run

# Build
./mvnw package
```

## Testing Strategy

- **`core.domain`:** pure unit tests, no mocks needed beyond other domain objects, no I/O.
- **`core.application`:** unit tests with mocked ports/repositories (interfaces, not concrete infrastructure).
- **`infrastructure.adapter`/`infrastructure.config`:** integration tests against real or containerized dependencies (DB via Testcontainers/H2, etc.).
- **Architecture itself:** `CleanArchitectureTest` (ArchUnit) — the layering and dependency-whitelist rules described above. Treat a failure here as a design problem to fix, not a test to loosen.

## Things to Avoid

- Anemic domain models (entities that are just data bags with getters/setters and no behavior).
- Fat controllers/services containing business rules.
- Repositories that leak query-building details (e.g. ORM query objects) into `core.domain`/`core.application`.
- Sharing a single generic "God" repository or service across unrelated aggregates.
- Skipping the use-case layer and calling repositories directly from controllers.
- Throwing raw `java.lang.Exception` from domain validation — use a specific unchecked domain exception (see Rule 8).
- Adding a new top-level package instead of using the existing `core.domain` / `core.application` / `infrastructure.adapter` / `infrastructure.config` split.

## Glossary

| Term              | Meaning                                                                                                                                                                      |
|-------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Profile**       | A user's nutrition/health profile: identity, `GroupType`, `SexType`, birth date, weight/height, optional trimester, health conditions. The current aggregate root candidate. |
| **GroupType**     | The nutritional life-stage category a Profile belongs to: `ADULT`, `CHILD`, `ELDERLY`, `PREGNANT`, `BREASTFEEDING`, `INFANT`. Drives which nutrition rules apply.            |
| **SexType**       | `MALE` or `FEMALE` — affects nutrition calculations.                                                                                                                         |
| **ActivityLevel** | Physical activity category used in energy-need calculations: `SEDENTARY`, `LIGHT`, `MODERATE`, `ACTIVE`, `VERY_ACTIVE`.                                                      |
| **Trimester**     | Pregnancy stage (1–3), only valid when `GroupType` is `PREGNANT` — an invariant enforced on `Profile` itself.                                                                |