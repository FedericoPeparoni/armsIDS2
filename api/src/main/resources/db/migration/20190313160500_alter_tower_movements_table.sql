-- remove tower movement log operator icao code column as unused and not apart of tower movement log properties
ALTER TABLE tower_movement_logs DROP COLUMN IF EXISTS operator_icao_code;
