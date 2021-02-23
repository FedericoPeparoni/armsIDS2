-- Status should be NOT NULL.  To ensure it's a clean migration, update
UPDATE unspecified_departure_destination_locations
SET    status = 'Manual'
WHERE  status IS NULL;

ALTER TABLE unspecified_departure_destination_locations
    ALTER COLUMN status SET NOT NULL;
