ALTER TABLE users
ADD COLUMN is_selfcare_user boolean,
ADD COLUMN force_password_change boolean,
ADD COLUMN password_history varchar,
ADD COLUMN email_activation_key varchar(100),
ADD COLUMN activation_key_expiration timestamp,
ADD COLUMN registration_status boolean;

-- Set registration_status for existing users
UPDATE users
SET registration_status = true,
is_selfcare_user = false,
force_password_change = false;

ALTER TABLE users
ALTER COLUMN is_selfcare_user SET NOT NULL,
ALTER COLUMN force_password_change SET NOT NULL,
ALTER COLUMN registration_status SET NOT NULL,
ALTER COLUMN sms_number DROP DEFAULT,
ALTER COLUMN sms_number DROP NOT NULL;

-- Fix duplicate values for sms_number
UPDATE users set sms_number = null
 WHERE sms_number IN (
  SELECT sms_number FROM users GROUP BY sms_number HAVING COUNT (sms_number) > 1);

-- add new constraints
ALTER TABLE users ADD CONSTRAINT users_sms_number UNIQUE (sms_number);

