-- rename radar format system configuration if it hasn't been already
UPDATE system_configurations
    SET item_name = 'Radar flight strip format', updated_by = 'system', updated_at = now()
    WHERE item_name = 'Radar Summary Format';

-- update system configuration item to add INDRA-REC radar support
UPDATE system_configurations
    SET range = range || ',INDRA-REC', updated_by = 'system', updated_at = now()
    WHERE item_name = 'Radar flight strip format' AND range NOT LIKE '%INDRA-REC%';

-- add waypoint column to radar summary table
ALTER TABLE radar_summaries
    ADD COLUMN waypoints character varying,
    ADD COLUMN entry_point_flight_level character varying (5),
    ADD COLUMN exit_point_flight_level character varying (5),
    ADD COLUMN cruising_speed character varying (5),
    ADD COLUMN wake_turb character varying (5),
    ADD COLUMN flight_level character varying (5)
;
