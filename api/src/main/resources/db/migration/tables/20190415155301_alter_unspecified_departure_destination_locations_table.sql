-- set all null country code columns to default country code if not done already
UPDATE unspecified_departure_destination_locations
    SET country_code = (
        SELECT id FROM countries WHERE country_code = (
            SELECT current_value FROM system_configurations WHERE item_name = 'Country code'))
    WHERE country_code IS NULL;

-- add not null constraint to country code column if not done already
ALTER TABLE unspecified_departure_destination_locations
    ALTER country_code SET NOT NULL;
