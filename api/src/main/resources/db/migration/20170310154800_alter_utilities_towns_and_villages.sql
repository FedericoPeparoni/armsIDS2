-- Changing column length because we now support ELECTRIC_COMM
ALTER TABLE utilities_schedules ALTER COLUMN schedule_type TYPE varchar(13);

-- Update values as `ELECTRIC` no longer exists, change them all to `ELECTRIC_COMM`
UPDATE utilities_schedules SET schedule_type = 'ELECTRIC_COMM' WHERE schedule_type = 'ELECTRIC';

-- alter the table column, to match the previous sql statement
ALTER TABLE utilities_towns_and_villages RENAME electricity_utility_schedule TO commercial_electricity_utility_schedule;

-- truncate these tables as below where we add `residential_electricity_utility_schedule` which is NOT NULL
-- because we may have existing rows and no residential schedules, just truncate
TRUNCATE utilities_towns_and_villages;
TRUNCATE utilities_range_brackets;
TRUNCATE utilities_schedules CASCADE;

-- add new column for `residential`, it as well is FK to utilities schedules table
ALTER TABLE utilities_towns_and_villages ADD COLUMN residential_electricity_utility_schedule integer NOT NULL
    REFERENCES utilities_schedules(schedule_id);
