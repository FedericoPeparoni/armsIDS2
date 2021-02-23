-- We require a `flight_rules` column, this currently comes back from SPATIA as `I` (Instrument Flight Rules IFR) or `V` (Visual Flight Rules VFR)
ALTER TABLE flight_movements
    ADD COLUMN flight_rules VARCHAR(1);
