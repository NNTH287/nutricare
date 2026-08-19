# NutriCare API

A REST API that calculates daily nutritional needs — with dedicated support
for pregnant women, breastfeeding women, infants, children, and the
elderly — organizes food composition data, and suggests menus that meet
those needs.

> **Architecture & contribution rules:** this project follows Clean
> Architecture + DDD. Before touching code, read [`CLAUDE.md`](./CLAUDE.md)
> for layering, DDD building blocks, naming conventions, and dev commands.
> This README covers the *product* and *domain model*; `CLAUDE.md` covers
> the *codebase structure*.

---

## 1. Tech Stack

| Concern | Choice |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 4.0.7 (Data JPA, Validation, Flyway, HATEOAS, WebMVC) |
| Database | PostgreSQL (runtime), H2 (tests) |
| API docs | springdoc-openapi (OpenAPI/Swagger, auto-generated) |
| Testing | JUnit 5, AssertJ, Mockito, ArchUnit 1.5.0 (architecture enforcement) |
| Validation | Bean Validation (`jakarta.validation`) |
| Auth | **Not in v1** — see [§6](#6-authentication-v1-scope) |

## 2. Target Profile Types

Each group has distinct inputs and calculation rules, so the domain models
them as a single `Profile` entity distinguished by `GroupType`, rather than
one calculator per type:

| GroupType | Key inputs |
|---|---|
| `ADULT` (male/female) | age, weight, height, activity level |
| `PREGNANT` | trimester (1–3), pre-pregnancy weight, activity level |
| `BREASTFEEDING` | months postpartum, single/multiple infants |
| `INFANT` (0–12 months) | age in months, weight, feeding type (breast/formula/mixed) |
| `CHILD` (1–18 years) | age, weight, height, growth stage, activity level |
| `ELDERLY` | age, weight, height, activity/health condition |
| Special condition *(later)* | diabetes, hypertension, allergies, etc. |

**Nutrition standards are configurable**, not hardcoded to one country:
Vietnam NIN, WHO/FAO, and US DRI are all first-class `NutritionStandard`
records, selectable per calculation.

## 3. Domain Model / Schema

### Enums

`GroupType`, `SexType` (`MALE`/`FEMALE`), `ActivityLevel` (`SEDENTARY` →
`VERY_ACTIVE`), `MealSlot` (`BREAKFAST`/`LUNCH`/`DINNER`/`SNACK`),
`MenuStatus` (`DRAFT`/`FINAL`).

### Core tables

| Table | Purpose | Notable design points |
|---|---|---|
| **`profile`** | A person's nutrition profile — the aggregate root candidate. | `user_id` is nullable from day one so a `User` entity can own multiple profiles later without a migration. Checks: `birth_date` or `age_in_months` required; `trimester` only valid 1–3 and only when `group_type = PREGNANT`. |
| **`nutrition_standard`** | A selectable standard (`VN_NIN`, `WHO_FAO`, `US_DRI`, …). | `code` unique. |
| **`nutrient`** | A nutrient definition (protein, iron, folate, …). | `unit` is the single source of truth for that nutrient's unit — not repeated on every requirement row. |
| **`nutrient_requirement`** | The RDA/AI/UL for a `(standard, group_type, nutrient)` combination, optionally scoped by age band or trimester. | `recommended_value` and `max_value` (UL) are independent columns since a nutrient can have either or both. Unique index over the full lookup key prevents duplicate/ambiguous requirement rows. |
| **`food_item`** | A food, with category, serving size, and tags (allergens, "suitable for infants 6m+", etc. — vocabulary enforced at the app layer). | |
| **`food_nutrient`** | Nutrient composition per 100g for a food item. | Composite PK `(food_item_id, nutrient_id)`; indexed on `nutrient_id` for "find foods rich in X" queries. |
| **`menu`** | A saved daily/weekly menu for a profile. | `label` supports non-date-scoped menus ("Week of Aug 10"). |
| **`menu_item`** | A food + quantity within a menu, tagged by `meal_slot`. | |
| **`calculation_result`** | A snapshot of a nutrient calculation run for a profile against a standard. | `nutrient_targets` stored as JSON since it's a computed snapshot, not queried directly. Indexed on `(profile_id, calculated_at)` for history lookups. |

### Relationships

```
nutrition_standard ─< nutrient_requirement >─ nutrient
food_item ─< food_nutrient >─ nutrient
profile ─< menu ─< menu_item >─ food_item
profile ─< calculation_result >─ nutrition_standard
```

Only `Profile` is currently a confirmed aggregate root; `Menu`/`MenuItem`
and `CalculationResult` reference it by ID rather than by object graph, so
cross-aggregate consistency stays eventual, not embedded.

## 4. Functional Modules

1. **Nutrition Calculation** — daily energy needs (Mifflin-St Jeor / WHO
   equations) adjusted by activity factor; macro (protein/fat/carb) and
   micro (iron, calcium, folate, vitamin D, iodine, zinc, …) targets;
   group-specific adjustments for pregnancy (per trimester),
   breastfeeding, infant age bands (0–6mo/6–12mo, not scaled adult
   values), and child growth stages; standard selectable per request.
2. **Food Database** — CRUD for foods and per-100g composition; tagging
   (allergens, pregnancy/infant suitability); search/filter by nutrient,
   category, tag, or dietary restriction; seeded from public food
   composition datasets (§7).
3. **Menu Suggestion** — generate a daily/weekly menu meeting a profile's
   targets under constraints (allergies, dislikes, budget, meal count,
   dietary preference); show generated-vs-target nutrient totals; allow
   manual swap/recalculate; save menus per profile.
4. **Profile & History** — create/update profiles; store calculation
   history and saved menus per profile, addressable by profile ID (no
   login required yet — access is by ID/token, not a credentialed
   account).
5. **Reference/Config** — expose supported standards and reference tables
   (age bands, activity multipliers, trimester definitions) so a front-end
   can build correct forms.

## 5. Authentication (v1 scope) {#6-authentication-v1-scope}

**v1 ships without authentication.** The data model is still shaped so
auth can be added later without restructuring:

- `profile.user_id` is nullable from the start.
- Access in v1 is by profile ID/issued token, not a credentialed account.
- Planned follow-up (see [Roadmap](#7-roadmap)): introduce a `User`
  entity + Spring Security/JWT, tie existing profiles to accounts via the
  already-reserved `userId`, and role-gate the currently-open food-CRUD
  admin endpoints.

## 6. API Endpoints (draft)

```
POST   /api/v1/profiles                  Create a profile
GET    /api/v1/profiles/{id}             Get profile
PUT    /api/v1/profiles/{id}             Update profile

GET    /api/v1/standards                 List supported nutrition standards

POST   /api/v1/profiles/{id}/calculate   Calculate nutrient needs
                                          (query param: standard=VN_NIN)
GET    /api/v1/profiles/{id}/history     Past calculations

GET    /api/v1/foods                     Search/filter food items
POST   /api/v1/foods                     Add a food item (admin)
GET    /api/v1/foods/{id}

POST   /api/v1/profiles/{id}/menus       Generate a menu suggestion
GET    /api/v1/profiles/{id}/menus       List saved menus
GET    /api/v1/menus/{id}                Get menu detail + nutrient totals
PUT    /api/v1/menus/{id}/items/{itemId} Swap/edit a menu item
```

## 7. Non-Functional Requirements

- Relational DB (PostgreSQL) for structured, relationally-integral
  nutrient/food/profile/menu data.
- Consistent JSON error format via `@ControllerAdvice`.
- Bean Validation on all inputs (age ≥ 0, trimester 1–3, etc.).
- i18n: nutrient names/units support at least Vietnamese + English, since
  standards differ by locale.
- Menu generation should stay fast against food DBs in the thousands of
  items — index on nutrient fields (`food_nutrient.nutrient_id` already
  indexed).

## 8. Roadmap

1. **Foundation** — project setup, DB schema, Profile CRUD, seed one
   nutrition standard (Vietnam NIN).
2. **Calculation Engine** — calorie/macro/micro logic per group type,
   unit-tested against known reference values.
3. **Food Database** — CRUD, search/filter, seed from a public dataset.
4. **Menu Engine** — start rule-based/greedy before anything
   optimization-based.
5. **Multi-standard support** — add a second standard (WHO/FAO) to prove
   the config-driven design generalizes.
6. **Auth** — introduce `User`, Spring Security + JWT, wire up the
   reserved `userId`.

## 9. External Data Sources to Investigate

- Vietnam Food Composition Table (Bảng thành phần thực phẩm Việt Nam —
  National Institute of Nutrition).
- Vietnam RDA tables (Bộ Y tế / NIN recommended intake tables).
- USDA FoodData Central.
- WHO/FAO joint nutrient requirement reports.

These need normalization into this project's own `NutrientRequirement` and
`FoodItem` schema, since source formats differ.