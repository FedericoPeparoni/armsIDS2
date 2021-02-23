ALTER TABLE accounts
DROP COLUMN IF EXISTS self_care_portal_user_name,
DROP COLUMN IF EXISTS self_care_portal_user_password,
ADD COLUMN is_self_care boolean not null default false,
ADD COLUMN self_care_user_id integer;

ALTER TABLE accounts
  ADD CONSTRAINT accounts_self_care_user_id_fkey FOREIGN KEY (self_care_user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
