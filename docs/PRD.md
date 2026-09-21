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

As of UC07, these actors act as an authenticated account rather than
anonymously — see UC07 for registration/sign-in and how profile ownership
is enforced:

| Actor                                      | Use case                                                                                                                                                                         |
|--------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **End consumer** (via a future client app) | Create a profile for themselves or a dependent (e.g. a parent creating an `INFANT` profile for their child); get nutrient targets; log meals; see a saved menu.                  |
| **Nutrition-conscious household**          | Track multiple profiles (e.g. a pregnant mother and a child) under the same `User` account.                                                             |
| **Content/data admin**                     | Populate and curate the shared food catalog (`food_item` where `owner_profile_id IS NULL`); the food-CRUD endpoints are still open to any authenticated account pending role-gating by the `ADMIN` role UC07 introduced. |
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
1. User selects an existing saved `Profile` (calculating without first saving a profile is not supported).
2. User selects a `NutritionStandard` (e.g. Vietnam NIN, WHO/FAO, US DRI) to calculate against.
3. User requests calculation.
4. The system calculates calorie and nutrient targets for the profile against the selected standard. If the standard has no matching requirement data for the profile's group, age band, or trimester for a given nutrient, that nutrient is left out of the result and flagged as unresolved rather than failing the whole calculation.
5. The system responds with the calculation result: calorie target, resolved nutrient targets, and the list of any flagged/unresolved nutrients.
6. User chooses to save the result or not.
   - If saved, it is kept in the profile's calculation history and becomes retrievable via UC02.
   - If not saved, it is discarded immediately after the response and cannot be retrieved again without recalculating.

**Outcome**: User obtains a nutrient-needs calculation for their profile under a chosen standard, with any data gaps explicitly flagged, and optionally persists it to their calculation history.

### UC02: Browse nutrient calculation history
**Actor**: User
</br>
**Precondition**: An existing saved `Profile` whose history is being browsed.
</br>
**Goal**: Browse and view the details of a specific profile's past saved calculations.
</br>
**Flow**:
1. User enters the calculation history view for a chosen profile.
2. The system lists that profile's saved calculations (e.g. newest first). Filtering, pagination, and retention limits are out of scope for v1.
3. User selects a specific result from the list.
4. The system retrieves that result and displays it exactly as it was calculated — calorie target, when it was calculated, standard used, and nutrient targets — as a frozen snapshot, not recomputed against current standard data (a standard's reference values may change after the calculation was saved).
5. User views the chosen result's details.

**Outcome**: User can browse a profile's saved calculation history and view the exact, unmodified snapshot of any past result.

### UC03: Manage nutrient database
**Actor**: Admin
</br>
**Precondition**: None to create; an existing nutrient to view, update, or delete.
</br>
**Goal**: Admin maintains the shared reference list of nutrients (e.g. vitamin A, Iron, Protein) that nutrient requirements and food composition values are defined against.
</br>
**Flow**:
1. **Create**: Admin submits a new nutrient's reference code, name, and unit of measure. The system rejects the request if a nutrient with that code already exists — the code is the unique business key across the nutrient database.
2. **View**: Admin looks up a nutrient.
3. **Update**: Admin submits changes to an existing nutrient's code, name, and/or unit. If the update would change the code to a value already used by another nutrient, the system rejects it.
4. **Delete**: Admin removes a nutrient.

**Outcome**: Admin can create, view, update, and delete nutrients in the reference database, with code uniqueness enforced on both create and update.

### UC04: Manage food composition database
**Actor**: Admin (shared catalog) or End consumer (their own food items)
</br>
**Precondition**: None to create; an existing `FoodItem` (by `id`) to view, update, or delete. Every nutrient listed in a food item's composition must already exist in the nutrient database (UC03).
</br>
**Goal**: Maintain a database of foods and how much of each nutrient they provide per serving, so future use cases can compare what someone eats against their calculated needs.
</br>
**Flow**:
1. **Create**: User submits a new food item's name, category, serving size, tags, and its nutrient composition (each nutrient it contains, and how much per 100g). The system rejects the submission if the name is missing, the serving size isn't a positive amount, any nutrient amount isn't positive, or the same nutrient is listed more than once for the same food item.
2. **Ownership**: A food item is either shared with everyone (curated by an Admin) or personal to a single user — visible only to its owner.
3. **View (list)**: User browses food items a page at a time.
4. **View (details)**: User looks up a single food item and sees its full nutrient composition alongside its core details.
5. **Update**: User submits changes to an existing food item's name, category, serving size, tags, and/or nutrient composition. The composition is replaced as a whole, not merged entry-by-entry — the same duplicate-nutrient and positive-amount rules from creation apply.
6. **Delete**: User removes a food item.

**Outcome**: The system maintains a food composition database — shared or personal — with each food item's nutrient makeup validated for positive amounts and no duplicate nutrients, ready to back future intake-tracking and food-search use cases.

### UC05: Log daily intake
**Actor**: User
</br>
**Precondition**: An existing saved `Profile` for the person whose intake is being logged. The food being logged must already exist in the food composition database (UC04) — either the shared catalog or that profile's own private items.
</br>
**Goal**: Record what a profile actually ate on a given day, by meal slot, as the factual counterpart to a planned menu.
</br>
**Flow**:
1. User selects a saved `Profile` and a date. If that profile has no intake log for the date yet, the system creates one; otherwise the existing log for that date is returned. A profile has at most one log per date.
2. **Add an entry**: User submits a food (by `id`), a quantity, and a meal slot (e.g. breakfast, lunch, dinner, snack) to add to the day's log. Unlike a saved menu, the same food can be logged more than once in the same meal slot (e.g. two snacks), so entries are not deduplicated by food-and-slot.
3. **Food not in the catalog**: If the food someone ate isn't already in the database, the user adds it as a new private `FoodItem` scoped to their own profile (per UC04's create flow) instead of the log entry being blocked.
4. **View**: User retrieves a day's log and sees all its entries — food, quantity, and meal slot.
5. **Update an entry**: User changes an existing entry's quantity and/or meal slot.
6. **Remove an entry**: User deletes an existing entry from the day's log.

**Outcome**: The system maintains a per-day, per-profile diary of actual food intake, editable entry-by-entry, ready to be compared against the profile's nutrient targets in UC06.

### UC06: Daily intake gap report and supplement recommendation
**Actor**: User
</br>
**Precondition**: An existing saved `Profile`; a day logged via UC05 with at least one entry; nutrient targets for that profile resolvable under a chosen standard using the same target-resolution logic as UC01.
</br>
**Goal**: See how a day's actual eating compares against the profile's nutrient targets, with shortfalls and over-intake called out, without re-deriving how those targets are resolved.
</br>
**Flow**:
1. User selects an existing saved `Profile`, a logged day, and a `NutritionStandard` to compare against.
2. The system totals that day's nutrient intake by combining each logged entry's quantity with its food's per-100g composition.
3. The system resolves that profile's nutrient targets for the selected standard using the same resolution rules as UC01 (by group, age band or trimester, and standard).
4. The system compares the day's totals against the resolved targets, nutrient by nutrient:
   - Below the recommended target: flagged as a shortfall, with the amount still needed to reach the target.
   - Above the maximum safe amount (the standard's UL, when one is defined for that nutrient): flagged as an over-intake warning.
   - Within range: recorded as no action needed.
   - Left unresolved by UC01's target resolution (no matching requirement data): excluded from the shortfall/over-intake comparison and separately listed as not evaluable, rather than silently treated as met.
5. The system responds with the full gap report: per-nutrient status, shortfall/over-intake amounts where applicable, and the list of any not-evaluable nutrients.
6. User views the report.

**Outcome**: User sees a per-nutrient gap report for a logged day — what's short, what's over the safe maximum, and what's on target.

### UC07: Register and sign in
**Actor**: End consumer or Admin
</br>
**Precondition**: None to register; an existing registered account to sign in.
</br>
**Goal**: Obtain an authenticated identity so that every other use case can be tied to the account that owns it, instead of being open to anyone who can reach the API.
</br>
**Flow**:
1. **Register**: User submits an email and password. The system rejects the request if that email is already registered — email is the unique business key for an account. On success, the account is created with the standard member role and the user is immediately signed in.
2. **Sign in**: User submits their email and password. The system rejects the request with a generic invalid-credentials response if the email isn't registered or the password doesn't match — it does not reveal which of the two was wrong.
3. On successful registration or sign-in, the system issues an access token the user presents on every subsequent request to prove who they are.
4. **Enforcement**: Every use case other than registration and sign-in now requires a valid access token. Where a use case acts on a specific `Profile` (UC01, UC05, UC06), the system also verifies the token's account owns that profile, rejecting the request otherwise.

**Outcome**: The system has authenticated accounts, and every profile-scoped use case is restricted to the account that owns the profile — closing the "anyone can act as anyone" gap called out in the Target Users section.

## Constraints & Limitations

- **UC01 does not refine targets by health condition (allergy, hypertension, diabetes, etc.) in v1.** `Profile.conditions` exists, but no condition→nutrient adjustment rule set is defined yet — deferring until that rule model (which condition adjusts which nutrient, by how much, sourced from where) is designed as its own use case. Allergy specifically belongs to food selection/intake logging, not nutrient-target calculation, and is out of scope for this use case regardless.

- **Reusing a previously saved calculation (UC01) instead of recalculating (relevant to UC06) will not detect an age-band change on its own.** For profiles in an age-dependent life stage (`CHILD`, `INFANT`), the nutrient targets a `NutritionStandard` resolves can change purely because time has passed and the profile aged into a new band — even with no edit to the profile itself. Any reuse mechanism judged only by whether the profile's own data has changed will miss this and can keep serving a now-outdated calculation until the profile is next recalculated. Accepted as a known limitation until reuse is designed in detail.

- **UC03 delete does not check whether a `Nutrient` is still referenced by a `NutrientRequirement` or `FoodNutrient` composition value.** Deleting a referenced nutrient can orphan those records. Deferred because neither of those aggregates exists in the codebase yet, and enforcing the check now would mean expanding the `Nutrient` aggregate's boundary to know about consumers outside it — revisit once those aggregates exist, via a domain event or an existence check at that boundary, not by pulling the check into `Nutrient` itself.

- **UC04 delete does not check whether a `FoodItem` is still referenced.

- **UC06 does not generate specific food or supplement suggestions in v1.** The gap report surfaces the shortfall/over-intake amount per nutrient, not a recommended food or supplement to close it — that's a possible later addition once the gap report itself has shipped and been validated.

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
