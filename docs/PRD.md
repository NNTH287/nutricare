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

### UC01: Calculate nutrient need for one day
**Actor**: User
</br>
**Precondition**: An existing saved `Profile` for the person the calculation is for.
</br>
**Goal**: Get nutrient (micro, macro) needs for a saved profile under a chosen nutrition standard.
</br>
**Flow**:
1. User selects an existing saved `Profile` (ad-hoc calculation without a saved profile is not supported — `CalculationResult` requires a `profileId`).
2. User selects a `NutritionStandard` (e.g. Vietnam NIN, WHO/FAO, US DRI) to calculate against.
3. User requests calculation.
4. The system calculates calorie and nutrient targets for the profile against the selected standard. If the standard has no matching `NutrientRequirement` data for the profile's group/age-band/trimester for a given nutrient, that nutrient is omitted from the result and flagged as unresolved rather than failing the whole calculation.
5. The system responds with the calculation result: calorie target, resolved nutrient targets, and the list of any flagged/unresolved nutrients.
6. User chooses to save the result or not.
   - If saved, it is persisted as a `CalculationResult` and becomes retrievable via UC02.
   - If not saved, it is discarded immediately after the response and cannot be retrieved again without recalculating.

**Outcome**: User obtains a nutrient-needs calculation for their profile under a chosen standard, with any data gaps explicitly flagged, and optionally persists it to their calculation history.

### UC02: Browse nutrient calculation history
**Actor**: User
</br>
**Precondition**: An existing saved `Profile` (`profileId`) whose history is being browsed.
</br>
**Goal**: Browse and view the details of a specific profile's past saved calculations.
</br>
**Flow**:
1. User enters the calculation history view for a given `profileId`.
2. The system lists that profile's saved `CalculationResult`s (e.g. newest first). Filtering, pagination, and retention limits are out of scope for v1.
3. User selects a specific result from the list.
4. The system retrieves that result and displays it exactly as it was calculated — calorie target, `calculatedAt`, standard used, and nutrient targets — as a frozen snapshot, not recomputed against current standard data (a standard's reference values may change after the calculation was saved).
5. User views the chosen result's details.

**Outcome**: User can browse a profile's saved calculation history and view the exact, unmodified snapshot of any past result.

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
