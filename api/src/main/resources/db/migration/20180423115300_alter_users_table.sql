-- Fix duplicate values for name
UPDATE users set name = format ('%s (%s)', name, id)
 WHERE name IN (
  SELECT name FROM users GROUP BY name HAVING COUNT (name) > 1);

-- add new constraints
ALTER TABLE users ADD CONSTRAINT users_name UNIQUE (name);
