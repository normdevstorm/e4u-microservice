--liquibase formatted sql

--changeset e4u-team:001-migrate-roles-to-e4u
--comment: Drop old Hibernate-generated role check constraint, migrate data, recreate constraint with new roles
--  USER   -> LEARNER  (regular students / default registrants)
--  RENTER -> LEARNER  (consumer role — maps to LEARNER)
--  OWNER  -> TEACHER  (content-creator role — maps to TEACHER)
--  ADMIN  stays unchanged

-- 1. Drop the old Hibernate-generated CHECK constraint (allows USER, ADMIN, OWNER, RENTER)
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- 2. Migrate existing role values
UPDATE users SET role = 'LEARNER' WHERE role = 'USER';
UPDATE users SET role = 'LEARNER' WHERE role = 'RENTER';
UPDATE users SET role = 'TEACHER' WHERE role = 'OWNER';

-- 3. Add new CHECK constraint aligned with the updated Role enum
ALTER TABLE users ADD CONSTRAINT users_role_check
    CHECK (role::text = ANY (ARRAY['LEARNER'::text, 'TEACHER'::text, 'ADMIN'::text]));

--rollback ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
--rollback UPDATE users SET role = 'OWNER'  WHERE role = 'TEACHER';
--rollback UPDATE users SET role = 'USER'   WHERE role = 'LEARNER';
--rollback ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role::text = ANY (ARRAY['USER'::text, 'ADMIN'::text, 'OWNER'::text, 'RENTER'::text]));
