-- Alter flight movements table to add arrival charge discount
-- column populated within flight movement charge utility.
--
-- This will require recalculation for any existing data that
-- must conform to the new structure.
ALTER TABLE flight_movements
    ADD COLUMN arrival_charge_discounts character varying;
