-- Dev/test seed accounts.
-- Passwords are bcrypt-hashed (cost 10, matching BCryptPasswordHasher):
--   admin@nutricare.local / Admin@123
--   user@nutricare.local  / User@123

INSERT INTO users (email, password_hash, role, created_at, updated_at)
VALUES
    ('admin@nutricare.local', '$2a$10$xtSkWrPJq93elx4ZoazGtOHaJE6bgf3jqWPb/SwCQasgmp3Y6Asdu', 'ADMIN', NOW(), NOW()),
    ('user@nutricare.local', '$2a$10$horMuPY3VjUTTA01AUHfCuRQltgCgHfw35QO8k7MfBsWFQWT67BIG', 'USER', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Plain adult male profile for the seeded user account (no child/infant/pregnant/breastfeeding special casing).
INSERT INTO profile (user_id, name, group_type, sex_type, birth_date, weight_kg, height_cm, activity_level, created_at, updated_at)
SELECT u.id, 'Demo User', 'ADULT', 'MALE', DATE '1995-06-15', 70, 175, 'MODERATE', NOW(), NOW()
FROM users u
WHERE u.email = 'user@nutricare.local'
  AND NOT EXISTS (SELECT 1 FROM profile p WHERE p.user_id = u.id);
