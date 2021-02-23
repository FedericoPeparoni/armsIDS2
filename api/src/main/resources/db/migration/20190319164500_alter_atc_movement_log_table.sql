-- rename "atc_movement_logs" column "operator_icao_code" to "operator_identifier"
ALTER TABLE atc_movement_logs
    RENAME operator_icao_code TO operator_identifier;

-- increase "atc_movement_logs" column "operator_identifier" length to 100 characters
ALTER TABLE atc_movement_logs
    ALTER COLUMN operator_identifier TYPE character varying (100);
