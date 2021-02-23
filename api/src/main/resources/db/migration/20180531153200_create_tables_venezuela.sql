-- create table flightmovement_categories
CREATE TABLE IF NOT EXISTS flightmovement_categories(
	id serial primary key,
	name varchar(50),
	sort_order integer,
	short_name varchar(50),
	enroute_currency_calculated integer,
	enroute_currency_invoice integer,
	version bigint NOT NULL DEFAULT 0,
	created_at timestamp not null default now(),
	created_by varchar(50) not null,
	updated_at timestamp,
	updated_by varchar(50),
	CONSTRAINT flightmovement_categories_fk1 FOREIGN KEY(enroute_currency_calculated)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT flightmovement_categories_fk2 FOREIGN KEY(enroute_currency_invoice)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT flightmovement_categories_uk1 UNIQUE (short_name),
	CONSTRAINT flightmovement_categories_uk2 UNIQUE (name)
);

-- create table flightmovement_category_attributes
CREATE TABLE IF NOT EXISTS flightmovement_category_attributes(
	id serial primary key,
	flightmovement_category integer,
	flight_type varchar(2),
	flight_scope varchar(2),
	flight_nationality varchar(2),
	version bigint NOT NULL DEFAULT 0,
	created_at timestamp not null default now(),
	created_by varchar(50) not null,
	updated_at timestamp,
	updated_by varchar(50),
	CONSTRAINT flightmovement_category_attributes_fk1 FOREIGN KEY(flightmovement_category)
	      REFERENCES flightmovement_categories (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION,  
	CONSTRAINT flightmovement_category_attributes_ck1       CHECK  (flight_type IN ('DO', 'AR', 'DE', 'OV', 'OT')),
	CONSTRAINT flightmovement_category_attributes_ck2       CHECK  (flight_scope IN ('DO', 'RE', 'IN')),
	CONSTRAINT flightmovement_category_attributes_ck3       CHECK  (flight_nationality IN ('NA', 'FO'))
);

CREATE TABLE IF NOT EXISTS enroute_air_navigation_charge_categories
(
  id serial primary key,
  mtow_category_upper_limit double precision NOT NULL,
  w_factor_formula character varying(255) NOT NULL DEFAULT 'sqrt(([MTOW] * 907.185) / 20000)'::character varying,
  version bigint NOT NULL DEFAULT 0,
  created_at timestamp not null default now(),
  created_by varchar(50) not null,
  updated_at timestamp,
  updated_by varchar(50),
  CONSTRAINT enroute_air_navigation_charge_categories_ukey UNIQUE (mtow_category_upper_limit)
);

CREATE TABLE IF NOT EXISTS enroute_air_navigation_charge_formulas
(
  id serial primary key,
  mtow_category integer NOT NULL,
  flightmovement_category integer not null,
  formula      character varying(255),
  d_factor_formula character varying(255),
  version bigint NOT NULL DEFAULT 0,
  created_at timestamp not null default now(),
  created_by varchar(50) not null,
  updated_at timestamp,
  updated_by varchar(50),
  CONSTRAINT enroute_air_navigation_charge_formulas_fk1 FOREIGN KEY(mtow_category)
	      REFERENCES enroute_air_navigation_charge_categories (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT enroute_air_navigation_charge_formulas_fk2 FOREIGN KEY(flightmovement_category)
	      REFERENCES flightmovement_categories (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION 
);

