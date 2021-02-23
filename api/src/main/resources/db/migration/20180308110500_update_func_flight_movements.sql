DROP TRIGGER IF EXISTS trg_actual_dep_dest_ad ON flight_movements;

UPDATE flight_movements SET actual_dep_ad = item18_dep where dep_ad = 'ZZZZ' and COALESCE(TRIM(item18_dep), '') <> '';
UPDATE flight_movements SET actual_dep_ad = dep_ad where actual_dep_ad = '';

UPDATE flight_movements SET actual_dest_ad = arrival_ad where dest_ad = 'ZZZZ' and arrival_ad <> '';
UPDATE flight_movements SET actual_dest_ad = item18_dest where dest_ad = 'ZZZZ' and arrival_ad = '' and COALESCE(TRIM(item18_dest), '') <> '';
UPDATE flight_movements SET actual_dest_ad = dest_ad where actual_dest_ad = '';

CREATE OR REPLACE FUNCTION func_actual_dep_dest_ad()
  RETURNS trigger AS
$func$
BEGIN
    IF new.dep_ad = 'ZZZZ' AND COALESCE(TRIM(new.item18_dep), '') <> ''
        THEN new.actual_dep_ad := TRIM(new.item18_dep);
        ELSE new.actual_dep_ad := new.dep_ad;
    END IF;

    IF new.dest_ad = 'ZZZZ' AND new.arrival_ad <> '' THEN
      new.actual_dest_ad := new.arrival_ad;
    ELSEIF new.dest_ad = 'ZZZZ' AND COALESCE(TRIM(new.item18_dest), '') <> '' THEN
      new.actual_dest_ad := TRIM(new.item18_dest);
    ELSE
      new.actual_dest_ad := new.dest_ad;
    END IF;

    RETURN new;
END;
$func$ LANGUAGE plpgsql;

CREATE TRIGGER trg_actual_dep_dest_ad
BEFORE INSERT OR UPDATE ON flight_movements
FOR EACH ROW
EXECUTE PROCEDURE func_actual_dep_dest_ad();
