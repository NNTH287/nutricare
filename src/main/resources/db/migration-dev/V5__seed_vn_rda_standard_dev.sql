-- Minimal reference data so calorie/nutrient target calculations have
-- something to resolve against for the seeded ADULT/MALE dev profile
-- (V4__seed_dev_accounts.sql: 'Demo User', 70kg / 175cm / MODERATE).
--
-- NOTE: the energy_coefficient and nutrient_requirement values below are
-- illustrative placeholders for local development/testing, not transcribed
-- from the official "Nhu cau dinh duong khuyen nghi cho nguoi Viet Nam"
-- publication:
--   - energy_coefficient uses the Mifflin-St Jeor BMR equation for men
--     (10*weight_kg + 6.25*height_cm - 5*age + 5) with age fixed at 30,
--     the midpoint of the 18-59y ADULT band, folded into the intercept.
--   - nutrient_requirement rows use commonly-cited general adult male RDA
--     figures (energy/protein/fat/carb/calcium/iron/vitamin C).
-- Replace with the authoritative VN RDA figures before relying on this for
-- anything beyond local dev/testing.

INSERT INTO nutrition_standard (code, name, description)
VALUES (
    'VN_RDA_DEV',
    'VN RDA (dev placeholder)',
    'Placeholder nutrition standard for local development/testing, pending the official Vietnam RDA (Nhu cau dinh duong khuyen nghi cho nguoi Viet Nam) figures.'
)
ON CONFLICT (code) DO NOTHING;

-- Energy coefficient: ADULT/MALE, 18-59 years (216-719 months).
INSERT INTO energy_coefficient (standard_id, group_type, sex_type, age_months_min, age_months_max, trimester, weight_coefficient, height_coefficient, intercept)
SELECT s.id, 'ADULT', 'MALE', 216, 719, NULL, 10, 6.25, -145
FROM nutrition_standard s
WHERE s.code = 'VN_RDA_DEV'
  AND NOT EXISTS (
      SELECT 1 FROM energy_coefficient ec
      WHERE ec.standard_id = s.id
        AND ec.group_type = 'ADULT'
        AND ec.sex_type = 'MALE'
        AND ec.age_months_min = 216
        AND ec.age_months_max = 719
  );

-- A handful of nutrient requirements for the same standard/group/age band
-- (nutrient_requirement has no sex_type column, so this applies to any
-- ADULT profile in that age range regardless of sex).
INSERT INTO nutrient_requirement (standard_id, group_type, nutrient_id, age_months_min, age_months_max, trimester, recommended_val, max_val)
SELECT s.id, 'ADULT', n.id, 216, 719, NULL, v.recommended_val, v.max_val
FROM nutrition_standard s
JOIN (VALUES
    ('energy', 2200.0, NULL::double precision),
    ('protein', 56.0, NULL),
    ('total-lipid-fat', 61.0, 86.0),
    ('carbohydrate-by-difference', 300.0, NULL),
    ('ca', 1000.0, NULL),
    ('fe', 8.0, NULL),
    ('vit-c', 90.0, NULL)
) AS v(nutrient_code, recommended_val, max_val) ON true
JOIN nutrient n ON n.code = v.nutrient_code
WHERE s.code = 'VN_RDA_DEV'
  AND NOT EXISTS (
      SELECT 1 FROM nutrient_requirement nr
      WHERE nr.standard_id = s.id
        AND nr.group_type = 'ADULT'
        AND nr.nutrient_id = n.id
        AND nr.age_months_min = 216
        AND nr.age_months_max = 719
  );
