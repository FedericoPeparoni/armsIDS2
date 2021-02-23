ALTER TABLE flight_movements
	ADD COLUMN crossing_distance_to_minimum double precision,
	ADD COLUMN enroute_cost_to_minimum double precision;