# Food composition crawler

Fetches Vietnam's food composition data (foods + per-100g nutrient values) from
the JSON endpoint that powers viendinhduong.vn's food lookup page, and emits a
SQL seed file matching `nutrient` / `food_item` / `food_item_tag` /
`food_item_nutrient` in [V1__initial_schema.sql](../../src/main/resources/db/migration/V1__initial_schema.sql).

There's no published API for this — the script just calls the same endpoint
the site's own frontend calls (`GET /api/fe/foodNatunal/getPageFoodData`).

## Usage

```
node crawl.mjs [--out <path>]
```

Defaults to writing `output/seed_food_nutrient_data.sql`. Requires Node 18+
(uses the built-in `fetch`).

## What it does

- Pages through all ~850 foods, collecting a deduplicated nutrient dictionary
  (code/name/unit) plus a synthetic `energy` (kcal) nutrient, since energy is
  returned outside the per-food `nutrition[]` array.
- Nutrients are upserted by `code` (`ON CONFLICT (code) DO NOTHING`).
- Each food is tagged `source:vdd:<original food code>` via `food_item_tag`.
  Re-running the script is safe: a food whose tag already exists is skipped
  (no duplicate `food_item` rows), and its nutrient amounts are refreshed via
  `ON CONFLICT (food_item_id, nutrient_id) DO UPDATE`.
- `serving_size_g` is seeded as `100` since all source values are per 100g.

## Applying the output

The generated file is plain SQL, not a Flyway migration. Review it, then either:

- run it manually: `psql <connection> -f output/seed_food_nutrient_data.sql`, or
- copy/rename it into `src/main/resources/db/migration/` as the next
  `V<n>__seed_food_nutrient_data.sql` if you want it version-controlled and
  applied automatically by Flyway.
