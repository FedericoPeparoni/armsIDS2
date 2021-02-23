-- update all existing flight movements' billing date to date of flight where null
UPDATE flight_movements SET billing_date = date_of_flight WHERE billing_date IS NULL;

-- add not null constraint
ALTER TABLE abms.flight_movements
    ALTER COLUMN billing_date SET NOT NULL;
