-- Add column `airspace_full_name`, this is mapped from navdb `airspaces.nam`
-- varchar(240) because that's what it is in navdb
ALTER TABLE airspaces
    ADD airspace_full_name VARCHAR(240);
