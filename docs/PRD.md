# NutriCare API — Product Requirements Document

|                  |                                                                                                                                      |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| **Status**       | Draft                                                                                                                                |
| **Owner**        | Huy Nguyễn                                                                                                                           |
| **Last updated** | 2026-08-21                                                                                                                           |
| **Related docs** | [`README.md`](../README.md) (tech stack, domain schema, API draft) · [`CLAUDE.md`](../CLAUDE.md) (architecture & contribution rules) |

## Executive Summary

NutriCare API is a Spring Boot backend that calculates personalized daily
nutrition needs and helps consumers act on them — via a food database and
intake tracking — for populations whose requirements shift materially by
life stage (adults, pregnant/breastfeeding women, infants, children, the
elderly). It uses **configurable nutrition standards** (Vietnam NIN,
WHO/FAO, US DRI), so results are authoritative and swappable rather than
generic. The project is built with Clean Architecture + DDD.

## Project Purpose

General-purpose calorie/nutrition apps calculate needs for a generic
adult and don't correctly adjust for pregnancy trimester, breastfeeding,
infant age bands, or elderly health conditions — populations where
under- or over-shooting nutrient targets has real health consequences.
NutriCare API exists to close three currently-disconnected problems under
one `Profile` per person: knowing how much a person needs (calculation,
standard-selectable), knowing what's in the food available to them (a
searchable composition database), and knowing whether what they actually
ate closes that gap (intake tracking with a per-nutrient gap report).

## Target Users

Since v1 has no authentication, these describe the *actor* invoking each
capability, not an authenticated account:

| Actor                                      | Use case                                                                                                                                                                         |
|--------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **End consumer** (via a future client app) | Create a profile for themselves or a dependent (e.g. a parent creating an `INFANT` profile for their child); get nutrient targets; log meals; see a saved menu.                  |
| **Nutrition-conscious household**          | Track multiple profiles (e.g. a pregnant mother and a child) under the same eventual `User` account once auth ships.                                                             |
| **Content/data admin**                     | Populate and curate the shared food catalog (`food_item` where `owner_profile_id IS NULL`); the currently-open food-CRUD endpoints are flagged for role-gating once auth exists. |
| **Front-end/client developer**             | Consumes `/api/v1/standards` and reference-table endpoints to build correct, standard-aware input forms without hardcoding age bands or activity multipliers client-side.        |

## Key Features & Capabilities

_TBD_

## Technical Requirements

_TBD_

## Success Metrics

_TBD_

## Technical Architecture

See [`CLAUDE.md`](../CLAUDE.md) for the full architectural rules (Clean
Architecture layering, the Dependency Rule, DDD building-block
definitions, and the ArchUnit-enforced package structure) and
[`README.md`](../README.md) §1 and §3 for the tech stack and domain
schema/entity relationships. This PRD does not duplicate those — it
covers product scope; those docs cover implementation structure.

## Use Cases

_TBD_

## Constraints & Limitations

_TBD_

## Risks & Mitigation

_TBD_

## Dependencies & Integration

_TBD_

## Compliance & Standards

_TBD_

## Glossary

_TBD_

## Appendix

_TBD_
