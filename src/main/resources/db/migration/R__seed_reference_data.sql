-- Reference data.
-- Repeatable migration: it is (re)applied whenever this file changes, so it must
-- stay idempotent. Uses the row-alias syntax (MySQL 8.0.19+) to update existing
-- rows on conflict.

INSERT INTO user_role (id_role, name, description)
VALUES
    (1, 'ADMIN', 'Administrator with full access rights'),
    (2, 'USER',  'Regular user with limited access rights')
AS new
ON DUPLICATE KEY UPDATE name = new.name, description = new.description;
