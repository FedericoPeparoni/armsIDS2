ALTER TABLE users
ADD COLUMN temporary_password varchar(60),
ADD COLUMN temporary_password_expiration timestamp;
