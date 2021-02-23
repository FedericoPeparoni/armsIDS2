--Original migration script was 20180207193600_create_air_traffic_statistics_table.sql that was REMOVED from changelog at unknown time

CREATE TABLE IF NOT EXISTS air_traffic_statistics
  (
	id serial primary key,
	name varchar(50) not null unique,
	aerodromes varchar,
	aircraft_types varchar,
	mtow_factor_class varchar(25),
	mtow_categories varchar,
	billing_centres varchar,
	accounts varchar,
	routes varchar,
	flight_levels varchar,
	flight_types varchar(50),
	flight_scopes varchar(50),
	flight_categories varchar(50),
	flight_rules varchar(50),
	temporal_group varchar(50),
	group_by varchar(200),
	sort varchar(200),
	value varchar(50),
	revenue_category varchar(65),
	chart_type varchar(50),
	version bigint NOT NULL DEFAULT 0,
	created_at timestamp not null default now(),
	created_by varchar(50) not null,
	updated_at timestamp,
	updated_by varchar(50)
  );
