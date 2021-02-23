CREATE TABLE revenue_statistics
  (
	id serial primary key,
	name varchar(50) not null unique,
	analysis_type varchar(50),
	billing_centres varchar,
	accounts varchar,
	aerodromes varchar,
	payment_mode varchar(50),
	charge_class varchar(100),
	charge_category varchar(100),
	charge_type varchar(100),
	temporal_group varchar(50),
	group_by varchar(200),
	sort varchar(200),
	fiscal_year boolean,
	value varchar(50),
	chart_type varchar(50),
	version bigint NOT NULL DEFAULT 0,
	created_at timestamp not null default now(),
	created_by varchar(50) not null,
	updated_at timestamp,
	updated_by varchar(50)
  );
