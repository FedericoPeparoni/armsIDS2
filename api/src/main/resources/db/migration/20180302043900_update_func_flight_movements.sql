DROP TRIGGER IF EXISTS trg_actual_dep_dest_ad ON flight_movements;

CREATE TRIGGER trg_actual_dep_dest_ad
BEFORE INSERT OR UPDATE ON flight_movements
FOR EACH ROW
EXECUTE PROCEDURE func_actual_dep_dest_ad();
