-- increase flight_notes length to MAX
ALTER TABLE flight_movements
    ALTER COLUMN flight_notes TYPE character varying;
