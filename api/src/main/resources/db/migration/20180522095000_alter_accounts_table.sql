ALTER TABLE accounts ALTER COLUMN aircraft_parking_exemption DROP not null;
ALTER TABLE accounts ALTER COLUMN aircraft_parking_exemption SET DEFAULT 0;

